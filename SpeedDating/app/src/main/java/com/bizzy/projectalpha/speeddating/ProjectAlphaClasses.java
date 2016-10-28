package com.bizzy.projectalpha.speeddating;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nubxf5 on 10/19/2016.
 */

public class ProjectAlphaClasses {
    private static ConfigHelper configHelper;

    public static class PreferenceSettings{
        private static final String TAG = "TutumpClass";
        public String getAge(int year, int month, int day){
            Calendar dob = Calendar.getInstance();
            Calendar today = Calendar.getInstance();

            dob.set(year, month, day);

            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
                age--;
            }

            Integer ageInt = new Integer(age);
            String ageS = ageInt.toString();

            return ageS;
        }
        public void setDefaultPreferences(Context context)
        {
            SharedPreferences prefs =  context.getSharedPreferences(context.getPackageName()+"_preferences", Context.MODE_PRIVATE);
            configHelper = new ConfigHelper();
            configHelper.fetchConfigIfNeeded();
            Boolean searchMen = prefs.getBoolean("switch_male_pref", false);
            Boolean searchWomen = prefs.getBoolean("switch_female_pref", false);
            Boolean searchByDistance = prefs.getBoolean("search_by_distance", false);
            Integer searchDistanceRadio = prefs.getInt("search_distance_radio", 5);
            Boolean visible = prefs.getBoolean("search_visibility", false);
            Set<String> ageRange = new HashSet<>();
            ageRange.add("18");
            ageRange.add("55");
            Set<String> ageRangePref = new HashSet<>();
            ageRangePref = prefs.getStringSet("age_range", ageRange);


            Log.d(TAG,"Search Men: " + searchMen.toString());
            Log.d(TAG,"Search WoMen: " + searchWomen.toString());
            Log.d(TAG,"Search By Distance: " + searchByDistance.toString());
            Log.d(TAG,"Search Distance Radio: " + searchDistanceRadio.toString());
            Log.d(TAG,"Visible : " + visible.toString());
            List<String> list = new ArrayList<String>(ageRangePref);
            Log.d(TAG,"Max : " + list.get(0) );
            Log.d(TAG,"Min : " + list.get(1));

        }

        public static ConfigHelper getConfigHelper() {
            return configHelper;
        }

    }
}
