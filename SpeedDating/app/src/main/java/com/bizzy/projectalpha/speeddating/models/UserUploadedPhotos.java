package com.bizzy.projectalpha.speeddating.models;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by nubxf5 on 6/30/2016.
 */
@ParseClassName("UserUploadedPhotos")
public class UserUploadedPhotos extends ParseObject{
    private Context mContext;
    protected String LOG_TAG = "UserUploadedPhotos";
    public UserUploadedPhotos(){
        // default constructor required for object to be created in Parse
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("albumTitle", title);
    }

    public ParseUser getAuthor() {
        return getParseUser("author");
    }

    public void setAuthor(ParseUser user) {
        put("author", user.getUsername());
    }


    public void getApprove(boolean approve){
        put("approved", approve);
    }

    public void creatorID(ParseUser user){
        put("creatorID", user);
    }


    public String getRating() {
        return getString("rating");
    }

    public void setRating(String rating) {
        put("rating", rating);
    }

    public ParseFile getPhotoFile() {
        return getParseFile("photo");
    }

    public void setPhotoFile(ParseFile file) {
        put("photo", file);

        /*file.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    //Toast.makeText(MainActivity.this,
                    //"Posting done.", Toast.LENGTH_SHORT).show();
                    Log.d(LOG_TAG, "error posting");
                    //MainActivity.this.finish();
                } else {
                    int errCodeSimple = e.getCode();
                    Toast.makeText(mContext, errCodeSimple, Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }


}