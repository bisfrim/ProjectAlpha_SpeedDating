package com.bizzy.projectalpha.speeddating.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bizzy.projectalpha.speeddating.ProjectAlphaClasses;
import com.parse.ParseUser;

/**
 * Created by bismark.frimpong on 12/7/2015.
 */
public class UserDispatchActivity extends Activity {

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
            startActivity(new Intent(this, MainActivity.class));
        } else{
            // start an intent for this logged out user
            startActivity(new Intent(this, LoginActivity.class)); //LoginActivity
        }
    }
}
