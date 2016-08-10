package com.bizzy.projectalpha.speeddating.activities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.bizzy.projectalpha.speeddating.ActivityWithToolbar;
import com.bizzy.projectalpha.speeddating.NavigationDrawerItems;
import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.loadmore.PersonAdapter;
import com.mikepenz.materialdrawer.Drawer;

public class UsersProfileActivity extends AppCompatActivity implements ActivityWithToolbar {

    public final static String EXTRA_USER_ID = "userId";
    private PersonAdapter adapter;
    private Toolbar mToolbar;
    private Drawer mDatingDrawer;
    private int position = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        adapter = new PersonAdapter(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle(adapter.getItem(isPositionHeader()).getName());
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
        return NavigationDrawerItems.DRAWER_ID_USERS_PROFILE;
    }

    private int isPositionHeader() {
        return position;
    }

}
