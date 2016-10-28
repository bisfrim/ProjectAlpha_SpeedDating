package com.bizzy.projectalpha.speeddating.models;

import android.app.Activity;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.bizzy.projectalpha.speeddating.activities.MessageActivity;
import com.bizzy.projectalpha.speeddating.adapter.MessageAdapter;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by bismark.frimpong on 12/8/2015.
 */

@ParseClassName("_User")
public class User extends ParseUser {

    private static final String TAG = "USER";
    public static final String USER_PHOTO = "photo";
    public static final String USER_PHOTO_THUMB = "photo_thumb";
    public static final String USER_AGE = "age";
    public static final String USER_DESCRIPTION = "desc";
    public static final String USER_EMAIL_VERIFIED = "emailVerified";
    public static final String USER_EMAIL = "email";
    public static final String USER_INSTALLATION = "installation";
    public static final String USER_IS_MALE = "isMale";
    public static final String USER_GEO_POINT = "location";
    public static final String COL_GEO_POINT = "geoPoint";
    public static final String USER_ID = "objectId";
    public static final String USER_ONLINE = "online";
    public static final String COL_NICKNAME = "name";
    public static final String USER_ZIPCODE = "zipcode";
    public static final String USER_LOCATION ="userSetLocation";
    public static final String USER_NOANSWER = "no_answer";
    public static final String USER_STRAIGHT = "straight";
    public static final String USER_GAY = "gay";
    public static final String USER_BISEXSUAL = "bi_sexsual";
    public static final String USER_ORIENTATION = "orientation";
    public static final String USER_ETHNICITY = "ethnicity";
    public static final String USER_BIO = "bio";
    public static final String USER_INTEREST ="interest";
    public static final String TYPE_FALSE = "false";
    public static final String TYPE_TRUE = "true";

    //Default search distance
    public static final float DEFAULT_SEARCH_DISTANCE = 100.0f;
    // Conversion from feet to meters
    public static final float METERS_PER_FEET = 0.3048f;
    // Conversion from kilometers to meters
    public static final int METERS_PER_KILOMETER = 1000;


    public ParseFile userUploaded;

    // Field names
    public static final String KEY_USERNAME = "username";
    public static final String KEY_FRIENDS_RELATION = "friendsRelation";
    public static final String KEY_RECIPIENT_IDS = "recipientIds";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_FILE = "file";
    public static final String KEY_FILE_TYPE = "fileType";
    public static final String KEY_CREATED_AT = "createdAt";

    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";

    public User(){}


    @Override
    public boolean equals(Object o) {
        return TextUtils.equals(this.getObjectId(), ((User) o).getObjectId());
    }

    @Override
    public int hashCode() {
        return this.getObjectId().hashCode();
    }

    public String getPhotoUrl(){
        ParseFile photoFile = getParseFile("photo");
        if(photoFile != null)
            return getParseFile("photo").getUrl();
        else
            return "";
    }

    public String getUploadedPhotos(){
        ParseQuery<ParseObject> innerQuery = new ParseQuery<ParseObject>("ImageTable");
        innerQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (object != null) {
                    userUploaded = (ParseFile) object.get("imageFile");

                }

            }
        });
        return "";
    }

    public boolean isMale(){
        return TextUtils.equals(getString(USER_IS_MALE), "true");
    }

    public boolean isBisexsual(){
        return TextUtils.equals(getString(USER_BISEXSUAL), "true");
    }

    public boolean isStraight(){
        return TextUtils.equals(getString(USER_STRAIGHT), "true");
    }

    public boolean isGay(){
        return TextUtils.equals(getString(USER_GAY), "true");
    }

    public boolean isNoanswer(){
        return TextUtils.equals(getString(USER_NOANSWER), "true");
    }


    public String getNickname(){
        String yourname = getString(COL_NICKNAME);
        if(yourname != null){
            return yourname;
        } else {
            return "<undefined>";
        }
    }



    public void setNickname(String nickname){
        put(COL_NICKNAME, nickname);
    }

    public String getUserBio() {
        String userbio = getString(USER_BIO);
        if(userbio != null){
            return userbio;
        }else{
            return "<no info>";
        }
    }

    public void setUserBio(String userBio){
        put(USER_BIO, userBio);
    }

    public String getOrientation(){
        return getString(USER_ORIENTATION);
    }

    public void setOrientation(String orientation){
        put(USER_ORIENTATION, orientation);
    }

    public Integer getAge(){
        if(getNumber(USER_AGE) == null)
            return -1;
        else
            return this.getInt(USER_AGE);
    }

    public void setDescriptionToTextView(TextView textView){
        if(!TextUtils.isEmpty(getDescription())){
            textView.setVisibility(View.VISIBLE);
            textView.setText(getDescription());
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    public String getDescription(){
        if(get(USER_DESCRIPTION) == null) return "";
        else return get(USER_DESCRIPTION).toString();
    }

    public void setUserInterest(String userInterest){
        put(USER_INTEREST, userInterest);
    }

    public String getUserInterest(){
        return getString(USER_INTEREST);
    }

    public String getEmail(){
        return getString(USER_EMAIL);
    }

    public void setEmail(String email){
        put(USER_EMAIL, email);
    }

    public void setZipCode(final String zipCode){
        put(USER_ZIPCODE, zipCode);
    }

    public void setUserEthnicity(final String userEthnicity){
        put(USER_ETHNICITY, userEthnicity);
    }

    public String getEthnicity(){
        return getString(USER_ETHNICITY);
    }

    public void setUserLoc(String location){
        put(USER_LOCATION, location);
    }

    public String getUserSetLocation(){
        return getString(USER_LOCATION);
    }

    public void getPhoto(GetDataCallback callback){
        getParseFile(USER_PHOTO).getDataInBackground(callback);
    }

    public void setAge(int age) {
        put(USER_AGE, age);
    }

    public void setProfilePhoto(ParseFile file) {
        put(USER_PHOTO, file);
    }

    public void setProfilePhotoThumb(ParseFile file) {
        put(USER_PHOTO_THUMB, file);
    }

    public void setDescription(String description){
        put(USER_DESCRIPTION, description);
    }

    public String getOnlineStatus(){
        if(getString("online") == null) {
            put("online", "no");
            saveInBackground();
            return "offline";
        }
        if(getString("online").equals("yes"))
            return "online";
        else
            return "offline";
    }

    public String getGenderString(){
        if(TextUtils.equals(this.getString(USER_IS_MALE), "true")){
            return "male";
        } else if(TextUtils.equals(this.getString(USER_IS_MALE), "false")){
            return "female";
        } else {
            return "no";
        }
    }

    public String getOrientationString(){
        if(TextUtils.equals(this.getString(USER_STRAIGHT), "true")){
            return "straight";
        }else if(TextUtils.equals(this.getString(USER_GAY), "true")){
            return "gay";
        }else if(TextUtils.equals(this.getString(USER_BISEXSUAL), "true")){
            return "bisexsual";
        }else if(TextUtils.equals(this.getString(USER_NOANSWER), "true")){
            return "no_answer";
        }else{
            return "no";
        }
    }

    public void setGenderIsMale(boolean isMale){
        if(isMale) put(USER_IS_MALE, "true");
        else if(!isMale)put(USER_IS_MALE, "false");
    }

    /*public void setUserOrientation(boolean isOrientation){
        if(isOrientation)put(USER_BISEXSUAL, "true");
        else if(isOrientation)put(USER_GAY, "true");
        else if(isOrientation)put(USER_STRAIGHT,"true");
        else if(!isOrientation)put(USER_NOANSWER, "false");
    }*/

    public void setOrientationStraight(boolean isStraight){
        if(isStraight)put(USER_STRAIGHT, "true");
        else if(!isStraight)put(USER_STRAIGHT, "false");
    }

    public void setOrientationGay(boolean isGay){
        if(isGay)put(USER_GAY, "true");
        else if(!isGay)put(USER_GAY, "false");
    }

    public void setOrientationBisexsual(boolean isBi){
        if(isBi)put(USER_BISEXSUAL, "true");
        else if(!isBi)put(USER_BISEXSUAL, "false");
    }

    public void setOrientationNoanswer(boolean isNoanswer){
        if(isNoanswer)put(USER_NOANSWER, "true");
        else if(!isNoanswer)put(USER_NOANSWER, "false");
    }

    public static User getUser(){
        return (User)ParseUser.getCurrentUser();
    }


    public static ParseQuery<User> getUserQuery(){
        return ParseQuery.getQuery(User.class);
    }

    public void setInstallation(ParseInstallation installation){
        put(USER_INSTALLATION,installation);
    }

    public ParseInstallation getInstallation(){
        return (ParseInstallation)get("installation");
    }

    public void setOnline(boolean online){
        if(online){
            put(USER_ONLINE, "yes");
        } else {
            put(USER_ONLINE, "no");
        }
    }

    public boolean isOnline(){
        return TextUtils.equals(getString(USER_ONLINE), "yes");
    }



    //Geo loaction here
    public void setGeoPoint(double lat, double lon) {
        put(COL_GEO_POINT, new ParseGeoPoint(lat,lon));
    }

    public void setGeoPoint(Location location){
        put(COL_GEO_POINT, new ParseGeoPoint(location.getLatitude(),location.getLongitude()));
    }

    public void setGeoPoint(ParseGeoPoint location){
        put(COL_GEO_POINT, location);
    }

    public ParseGeoPoint getGeoPoint(){
        return (ParseGeoPoint)get(COL_GEO_POINT);
    }

    public ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    public String getGeoPointString(){
        ParseGeoPoint geoPoint = (ParseGeoPoint)get(COL_GEO_POINT);
        if(geoPoint == null)
            return "";
        else {
            String geoPointString = String.format("%.4f",geoPoint.getLatitude()) + " : " + String.format("%.4f",geoPoint.getLongitude());
            return geoPointString;
        }
    }


    public void sendMessage(final Activity activity, final Pubnub pubnub, final Message message, final MessageAdapter msgAdapter) {
        final String receiverId = message.getReceiverId();
        message.setTimeSent(new Date());

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    JSONObject messageObject = message.toJSON();

                    try {
                        pubnub.subscribe(receiverId, new Callback() {
                            @Override
                            public void successCallback(String channel, Object message) {
                                super.successCallback(channel, message);
                            }

                            @Override
                            public void errorCallback(String channel, PubnubError error) {
                                super.errorCallback(channel, error);
                            }
                        });
                    } catch (PubnubException pubNubException) {
                        pubNubException.printStackTrace();
                    }

                    Callback messageCallback = new Callback() {
                        public void successCallback(String channel, Object response) {
                            Log.d(TAG, response.toString());

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Add the message to the adapter
                                    msgAdapter.addMessage(message);

                                    // Hide the dialog
                                    ((MessageActivity) activity).hideProgressDialog();
                                }
                            });

                            // Unsubscribe from the channel once the message is sent
                            pubnub.unsubscribe(receiverId);
                        }

                        public void errorCallback(String channel, PubnubError error) {
                            Log.d(TAG, "Error sending message:" + error.toString());

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "Error sending message", Toast.LENGTH_LONG).show();

                                    // Hide the dialog
                                    ((MessageActivity) activity).hideProgressDialog();
                                }
                            });

                            // Unsubscribe from the channel once the message is sent
                            pubnub.unsubscribe(receiverId);
                        }
                    };

                    pubnub.publish(receiverId, messageObject, messageCallback);
                } else {
                    Log.d(TAG, "Error saving message:" + e.toString());

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Error sending message", Toast.LENGTH_LONG).show();

                            // Hide the dialog
                            ((MessageActivity) activity).hideProgressDialog();

                        }
                    });
                }
            }
        });


    }



}
