package com.bizzy.projectalpha.speeddating.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.bizzy.projectalpha.speeddating.ProjectAlphaClasses;
import com.bizzy.projectalpha.speeddating.models.User;
import com.parse.ParseUser;

/**
 * Created by bismark.frimpong on 12/7/2015.
 */
public class UserDispatchActivity extends Activity {
    private static String DISPATCHER = "Dispatcher";
    private User mCurrentUser;

    public UserDispatchActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProjectAlphaClasses.PreferenceSettings projPreference = new ProjectAlphaClasses.PreferenceSettings();

        projPreference.setDefaultPreferences(this);

        //check the current parse user info
        if(ParseUser.getCurrentUser() != null){
            // Log the user into the main activity
            mCurrentUser = (User) User.getCurrentUser();
            mCurrentUser.setOnline(true);
            mCurrentUser.saveInBackground();
            startActivity(new Intent(this, MainActivity.class));
        } else{
            // start an intent for this logged out user
            startActivity(new Intent(this, LoginActivity.class)); //LoginActivity
        }
    }

    public void onResume() {
        super.onResume();
        if(!isNetworkAvailable()) {
            Log.d(DISPATCHER, "Network is unavailable!");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivitymanager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivitymanager.getActiveNetworkInfo();
        return networkInfo !=null && networkInfo.isConnected();
    }
}
