package com.bizzy.projectalpha.speeddating;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.bizzy.projectalpha.speeddating.activities.MessageActivity;
import com.bizzy.projectalpha.speeddating.fragments.ConversationThreadFragment;
import com.bizzy.projectalpha.speeddating.listeners.MessageListener;
import com.bizzy.projectalpha.speeddating.models.Message;
import com.bizzy.projectalpha.speeddating.models.Photo;
import com.bizzy.projectalpha.speeddating.models.User;
import com.bizzy.projectalpha.speeddating.models.UserUploadedPhotos;
import com.cloudinary.Cloudinary;
import com.cloudinary.android.*;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.neumob.api.Neumob;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import io.fabric.sdk.android.Fabric;
import org.json.JSONObject;

/**
 * Created by bismark.frimpong on 12/7/2015.
 */
public class AuthApplication extends Application {
    public static final String TODO_GROUP_NAME = "ALL_TODOS";
    private static String TAG = "AuthApp";
    private Pubnub pubnub;
    private ConversationThreadFragment conversationFragment;
    private Cloudinary cloudinary;
    public Cloudinary getCloudinary() {
        return cloudinary;
    }

    /**
     * Provides access to the singleton and the getCloudinary method
     * @param context Android Application context
     * @return instance of the Application singleton.
     */
    public static AuthApplication getInstance(Context context) {
        return (AuthApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Stetho.initializeWithDefaults(this);
        initUIL();
        initCloudinary();

        //Parse.enableLocalDatastore(getApplicationContext());
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(UserUploadedPhotos.class);
        //ParseObject.registerSubclass(MessagingUser.class);
        //ParseObject.registerSubclass(Photo.class);
        ParseObject.registerSubclass(Message.class);
        //Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));

        Parse.initialize(new Parse.Configuration.Builder(AuthApplication.this)
                .applicationId(getString(R.string.parse_app_id))
                .clientKey(getString(R.string.parse_client_key))
                .server("https://parseapi.back4app.com")
                .build()
        );
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        Log.i(TAG, "Parse initialized");

        Neumob.initialize(getApplicationContext(),"3TKJqupvbFenN8xL" ,new Runnable() {
        @Override
        public void run() {
            if (Neumob.isInitialized()) {
                boolean isAccelerated = Neumob.isAccelerated();
                Log.d(TAG, String.valueOf(isAccelerated));
            } else if (Neumob.isAuthenticated()) {
            }
        }
    });


        ParseFacebookUtils.initialize(this);
        ParsePush.subscribeInBackground("global", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
        //ParseUser.enableRevocableSessionInBackground();

        //ParseUser.enableAutomaticUser();
        //ParseACL defaultACL = new ParseACL();
        //ParseACL.setDefaultACL(defaultACL, true);


        //FacebookSdk.sdkInitialize(getApplicationContext());

    }

    private void initUIL() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
        Log.i(TAG, "Universal Image Loader initialized");
    }

    private void initCloudinary() {
        // Cloudinary: creating a cloudinary instance using meta-data from manifest

        cloudinary = new Cloudinary(com.cloudinary.android.Utils.cloudinaryUrlFromContext(this));
        Log.i(TAG, "Cloudinary initialized");
    }


    public void subscribeToMessagingChannel() {
        String userId = ParseUser.getCurrentUser().getObjectId();
        pubnub = new Pubnub(getString(R.string.pubnub_publish_key), getString(R.string.pubnub_subscribe_key));
        try {
            pubnub.subscribe(userId, new Callback() {

                @Override
                public void successCallback(String channel, Object messageObject) {

                    // Create Parse Object from JSON
                    final Message message = Message.messageFromJSON((JSONObject) messageObject);

                    // Use the message sender ID to find the sender
                    ParseUser.getQuery().getInBackground(message.getSenderId(), new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser sender, ParseException e) {
                            if (e == null) {
                                // Make a call to the callback method of the message listener
                                messageListener.onMessageReceived(sender, message);
                            } else {
                                Log.e(TAG, "Error fetching user for message", e);
                            }
                        }
                    });
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.d(TAG, "SUBSCRIBE : ERROR on channel " + channel
                            + " : " + error.toString());
                }

            });
        } catch (PubnubException e) {
            e.printStackTrace();
            Log.e(TAG, "Error subscribing to channel:" + e.getMessage());
        }
    }

    public void unsubscribeFromMessageChannel(String userId) {
        pubnub.unsubscribe(userId);
    }

    private MessageListener messageListener = new MessageListener() {

        @Override
        public void onMessageReceived(ParseUser sender, Message message) {

            if (conversationFragment != null && conversationFragment.belongsToSender(sender)) {
                // If senders conversation thread is open just publish message to adapter
                conversationFragment.addMessage(message);
            } else  {
                // Otherwise send a notification
                sendMessageNotification(sender, message);
            }

        }
    };

    // For updating UI if conversation is visible
    public void onConversationResumed(ConversationThreadFragment conversationFragment) {
        this.conversationFragment = conversationFragment;
    }

    public void onConversationPaused() {
        this.conversationFragment = null;
    }

    private void sendMessageNotification(ParseUser sender, Message message) {
        final int notificationId = 100;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("Message from " + sender.getUsername())
                        .setContentText(message.getMessageBody())
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MessageActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MessageActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // notification id  allows you to update the notification later on.
        notificationManager.notify(notificationId, mBuilder.build());
    }

}
