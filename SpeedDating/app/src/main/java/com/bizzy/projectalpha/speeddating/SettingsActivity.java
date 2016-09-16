package com.bizzy.projectalpha.speeddating;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.bizzy.projectalpha.speeddating.activities.MainActivity;
import com.bizzy.projectalpha.speeddating.fragments.SettingsFragment;
import com.bizzy.projectalpha.speeddating.listeners.DetailsCommunicator;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import io.apptik.widget.MultiSlider;

public class SettingsActivity extends AppCompatActivity  implements ActivityWithToolbar{

    private static final String FRAGMENT_TAG = SettingsActivity.class.getSimpleName() + "::SettingsfragmentTag";

    private Toolbar mToolbar;
    private Drawer mDatingDrawer;
    SharedPreferences mPrefs;

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
    private Button footerButton;
    ListView lv ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatingDrawer = NavigationDrawerItems.createDrawer(this);

        if (savedInstanceState == null) {
            SettingsFragment fragment = new SettingsFragment();

            getSupportFragmentManager().beginTransaction().replace(R.id.main_settings, fragment).commit();
        }


        final TextView min1 = (TextView) findViewById(R.id.minValue1);
        final TextView max1 = (TextView) findViewById(R.id.maxValue1);

        MultiSlider multiSlider1 = (MultiSlider) findViewById(R.id.range_slider1);


        min1.setText(String.valueOf(multiSlider1.getThumb(0).getValue()));
        max1.setText(String.valueOf(multiSlider1.getThumb(1).getValue()));

        multiSlider1.setOnThumbValueChangeListener(new MultiSlider.SimpleOnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    min1.setText(String.valueOf(value));
                } else {
                    max1.setText(String.valueOf(value));
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private static int getResIdFromAttribute(final Activity activity, final int attr) {
        if (attr == 0) {
            return 0;
        }
        final TypedValue typedvalueattr = new TypedValue();
        activity.getTheme().resolveAttribute(attr, typedvalueattr, true);
        return typedvalueattr.resourceId;
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