package com.bizzy.projectalpha.speeddating.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bizzy.projectalpha.speeddating.R;

import rikka.materialpreference.CheckBoxPreference;
import rikka.materialpreference.Preference;
import rikka.materialpreference.PreferenceFragment;
import rikka.materialpreference.SwitchPreference;


/**
 * Created by nubxf5 on 8/30/2016.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences mPrefs;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        getPreferenceManager().setSharedPreferencesName("settings");
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);

        setPreferencesFromResource(R.xml.prefs, null);

    }

    @Override
    public DividerDecoration onCreateItemDecoration() {
        return new rikka.materialpreference.PreferenceFragment.CategoryDivideDividerDecoration();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getPreferenceManager().setDefaultPackages(new String[]{"rikka."});

        final CheckBoxPreference prefMale = (CheckBoxPreference) findPreference("pref_male");
        if (prefMale != null) {
            prefMale.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isMalePref = ((Boolean) newValue).booleanValue();
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putBoolean("pre_male", isMalePref);
                    editor.commit();
                    return true;
                }
            });
        }
        CheckBoxPreference pref_Female = (CheckBoxPreference) findPreference("pref_female");
        if (pref_Female != null) {
            pref_Female.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isFemalePref = ((Boolean) newValue).booleanValue();
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putBoolean("pre_female", isFemalePref);
                    editor.commit();
                    return true;
                }
            });
        }


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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("SettingsFragment", "onSharedPreferenceChanged " + key);
    }
}
