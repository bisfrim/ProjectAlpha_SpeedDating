package com.bizzy.projectalpha.speeddating;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;

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


        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_settings);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        LayoutInflater inflater =
                (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view =inflater.inflate(R.layout.activity_extra_settings, null);
        //params.leftMargin = 50;
        //params.topMargin = 10;
        mainLayout.addView(view);
        //mainLayout.setGravity(params);
        // may be logo is a layout file, if not then put your layout file which contain an ImageView


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