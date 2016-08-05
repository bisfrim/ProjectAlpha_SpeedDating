package com.bizzy.projectalpha.speeddating;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by bismark.frimpong on 12/7/2015.
 */
public class AuthApplication extends Application {
    public static final String TODO_GROUP_NAME = "ALL_TODOS";

    @Override
    public void onCreate()
    {
        super.onCreate();
        //Parse.enableLocalDatastore(getApplicationContext());
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(UserUploadedPhotos.class);
        //Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));

        Parse.initialize(new Parse.Configuration.Builder(AuthApplication.this)
                .applicationId(getString(R.string.parse_app_id))
                .clientKey(getString(R.string.parse_client_key))
                .server("https://parseapi.back4app.com")
        .build()
        );


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
}
