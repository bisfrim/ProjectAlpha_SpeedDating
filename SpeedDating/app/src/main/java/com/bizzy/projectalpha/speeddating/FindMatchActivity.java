package com.bizzy.projectalpha.speeddating;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialdrawer.Drawer;

public class FindMatchActivity extends AppCompatActivity implements ActivityWithToolbar {

    private Toolbar mToolbar;
    private Drawer mDatingDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_match);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Find Match");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        return NavigationDrawerItems.DRAWER_ID_START_MATCH;
    }
}
