package com.bizzy.projectalpha.speeddating.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.RangeBarPreference;
import com.google.android.gms.ads.internal.client.ThinAdSizeParcel;
import com.pavelsikun.seekbarpreference.SeekBarPreference;
import com.pavelsikun.seekbarpreference.SeekBarPreferenceView;

import java.util.HashSet;
import java.util.Set;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new MyPreferenceFragment())
                .commit();

        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(true);
        }
    }




    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private static SharedPreferences mPrefs;
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //getPreferenceManager().setSharedPreferencesName("settings");
            //getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);

            addPreferencesFromResource(R.xml.prefs);

            Preference ageRange = findPreference("age_range");
            ageRange.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    return true;
                }
            });

            SwitchPreference maleSwitch = (SwitchPreference) findPreference("switch_male_pref");
            if (maleSwitch != null) {
                maleSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        boolean isMaleSwitchPref = ((Boolean) newValue).booleanValue();
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putBoolean("switch_male_pref", isMaleSwitchPref);
                        editor.commit();
                        return true;
                    }
                });
            }
            SwitchPreference femaleSwitch = (SwitchPreference) findPreference("switch_female_pref");
            if (femaleSwitch != null) {
                femaleSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        boolean isFemaleSwitchPref = ((Boolean) newValue).booleanValue();
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putBoolean("switch_female_pref", isFemaleSwitchPref);
                        editor.commit();
                        return true;
                    }
                });
            }

            SeekBarPreference seekBarPreference = (SeekBarPreference)findPreference("search_distance_radio");
            if(seekBarPreference != null){
                seekBarPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        int isSwitchBarPreference = ((Integer) newValue).intValue();
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putInt("search_distance_radio", isSwitchBarPreference);
                        editor.commit();
                        return true;
                    }
                });
            }


            final Preference visibility = findPreference("search_visibility");
            visibility.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    //Reference to database
                    //mDatabase = FirebaseDatabase.getInstance().getReference();
                    //mDatabase.child("users").child(user.getUid()).child("searchVisibility").setValue(o);
                    return true;
                }
            });


        }

      /*  public static int getSearchDistance() {
            int mySeekBarPref = seekBarPreference.getCurrentValue();
            return mPrefs.getInt("search_distance_radio", mySeekBarPref);
        }*/


        @Override
        public void onStart() {
            super.onStart();
            mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            Log.d("SettingsFragment", "onSharedPreferenceChanged " + s);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            //finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
