package com.bizzy.projectalpha.speeddating;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;

public class SettingsActivity extends PreferenceActivity implements ActivityWithToolbar {

    private Toolbar mToolbar;
    private Drawer mDatingDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        addPreferencesFromResource(R.xml.prefs);

        mToolbar.setClickable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha, null));
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
