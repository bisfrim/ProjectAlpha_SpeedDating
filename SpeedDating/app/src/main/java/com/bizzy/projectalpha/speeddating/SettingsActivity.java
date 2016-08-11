package com.bizzy.projectalpha.speeddating;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;

import com.bizzy.projectalpha.speeddating.listeners.DetailsCommunicator;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity implements ActivityWithToolbar {

    private Toolbar mToolbar;
    private Drawer mDatingDrawer;

    //private static SwitchPreference malePrefSwitch;
    //private static SwitchPreference femalePrefSwitch;

    private static ListPreference genderPref;
    private static CheckBoxPreference genderPublicPref;

    private static ListPreference countryPref;
    private static CheckBoxPreference countryPublicPref;

    private static EditTextPreference occupationsPref;
    private static EditTextPreference interestsPref;

    private static ListPreference relationshipPref;
    private static CheckBoxPreference relationshipPublicPref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_settings);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new PreferencesScreen())
                .commit();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mToolbar.setClickable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_material, null));
        }
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle("Settings");

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mDatingDrawer = NavigationDrawerItems.createDrawer(this);


    }


    public static class PreferencesScreen extends PreferenceFragment implements DetailsCommunicator {

        private boolean genderMaleSwitch;
        private boolean genderFemaleSwitch;
        private boolean genderChanged;
        private boolean countryChanged;
        private boolean occupationChanged;
        private boolean interestsChanged;
        private boolean relationshipStatusChanged;
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            genderMaleSwitch = genderFemaleSwitch = genderChanged = countryChanged = occupationChanged = interestsChanged = relationshipStatusChanged = false;
            int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
            int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
            int topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(R.dimen.activity_vertical_margin) - 15, getResources().getDisplayMetrics());

            View v = super.onCreateView(inflater, container, savedInstanceState);
            ListView lv = (ListView) v.findViewById(android.R.id.list);
            lv.setPadding(horizontalMargin, topMargin, horizontalMargin, verticalMargin);
            setViews();
            return v;

        }


        private void setViews() {
            /**
             * Gender
             */

          /*  malePrefSwitch = (SwitchPreference) findPreference("switch_male_pref");
            malePrefSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    genderMaleSwitch = true;
                    malePrefSwitch.setSummary(newValue.toString());
                    return false;
                }
            });

            femalePrefSwitch = (SwitchPreference) findPreference("switch_female_pref");
            femalePrefSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    genderFemaleSwitch = true;
                    femalePrefSwitch.setSummary(newValue.toString());
                    return false;
                }
            });*/

            genderPref = (ListPreference) findPreference("gender");
            genderPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    genderChanged = true;
                    genderPref.setSummary(newValue.toString());
                    return false;
                }
            });
            genderPublicPref = (CheckBoxPreference) findPreference("genderPublic");

            /**
             * Country
             */
            countryPref = (ListPreference) findPreference("country");
            ArrayList<String> countries = getCountries();
            CharSequence[] cs = countries.toArray(new CharSequence[countries.size()]);
            countries = null;
            countryPref.setEntries(cs);
            countryPref.setEntryValues(cs);
            countryPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    countryChanged = true;
                    countryPref.setSummary(newValue.toString());
                    return false;
                }
            });
            countryPublicPref = (CheckBoxPreference) findPreference("countryPublic");

            /**
             * Occupations
             */
            occupationsPref = (EditTextPreference) findPreference("occupations");
            occupationsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    occupationChanged = true;
                    occupationsPref.setSummary(newValue.toString());
                    return false;
                }
            });

            /**
             * Interests
             */
            interestsPref = (EditTextPreference) findPreference("interests");
            interestsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    interestsChanged = true;
                    interestsPref.setSummary(newValue.toString());
                    return false;
                }
            });

            /**
             * Relationship
             */
            relationshipPref = (ListPreference) findPreference("relationshipStatus");
            relationshipPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    relationshipStatusChanged = true;
                    relationshipPref.setSummary(newValue.toString());
                    return false;
                }
            });
            relationshipPublicPref = (CheckBoxPreference) findPreference("relationshipPublic");
        }


        @Override
        public HashMap<String, String> getDataFromFragment() {
            HashMap<String, String> profileDetails = new HashMap<>();

            //profileDetails.put("MaleSwitch", (genderMaleSwitch) ? (String) malePrefSwitch.getSummary() : "");
            //profileDetails.put("FemaleSwitch", (genderFemaleSwitch) ? (String) femalePrefSwitch.getSummary() : "");

            profileDetails.put("Gender", (genderChanged) ? (String) genderPref.getSummary() : "");
            profileDetails.put("GenderPublic", (genderPublicPref.isChecked()) ? "yes" : "no");

            profileDetails.put("Country", (countryChanged) ? (String) countryPref.getSummary() : "");
            profileDetails.put("CountryPublic", (countryPublicPref.isChecked()) ? "yes" : "no");

            profileDetails.put("Occupation", (occupationChanged) ? (String) occupationsPref.getSummary() : "");
            profileDetails.put("Interests", (interestsChanged) ? (String) interestsPref.getSummary() : "");

            profileDetails.put("Relationship", (relationshipStatusChanged) ? (String) relationshipPref.getSummary() : "");
            profileDetails.put("RelationshipPublic", (relationshipPublicPref.isChecked()) ? "yes" : "no");
            return profileDetails;
        }
    }

    private static ArrayList<String> getCountries() {
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        for (Locale loc : locales) {
            String country = loc.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);
        return countries;
    }





    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public int getDriwerId() {
        return NavigationDrawerItems.DRAWER_ID_SETTINGS;
    }



}