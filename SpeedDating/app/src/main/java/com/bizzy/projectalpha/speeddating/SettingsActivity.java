package com.bizzy.projectalpha.speeddating;

import android.app.Activity;
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

import com.bizzy.projectalpha.speeddating.activities.MainActivity;
import com.bizzy.projectalpha.speeddating.listeners.DetailsCommunicator;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class SettingsActivity extends PreferenceActivity {

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

        addPreferencesFromResource(R.xml.prefs);

        footerButton = new Button(this);
        footerButton.setText(R.string.action_logout);
        footerButton.setTextColor(Color.WHITE);
        lv = getListView();
        lv.addFooterView(footerButton);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setClickable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_material, null));
        }
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle(R.string.drawer_item_users_near_me);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //finish();
                Intent logoutIntent = new Intent(SettingsActivity.this, MainActivity.class);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logoutIntent);
            }
        });


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


}