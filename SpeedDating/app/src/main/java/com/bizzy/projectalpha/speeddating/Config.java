package com.bizzy.projectalpha.speeddating;

import android.util.Log;

/**
 * Created by androiddev on 25.11.15.
 */
public class Config {
    private static final String LOG_TAG = "myapp";
    private static final Boolean LOG_DEBUG = true;

    public static void log(String message){
        Log.d(LOG_TAG, message);
    }
}
