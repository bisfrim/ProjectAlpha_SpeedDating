package com.bizzy.projectalpha.speeddating.activities;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bizzy.projectalpha.speeddating.ActivityWithToolbar;
import com.bizzy.projectalpha.speeddating.NavigationDrawerItems;
import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.adapter.ViewPagerAdapter;
import com.bizzy.projectalpha.speeddating.layout_tab.SlidingTabLayout;
import com.mikepenz.materialdrawer.Drawer;

public class ConnectionsActivity extends AppCompatActivity implements ActivityWithToolbar,View.OnClickListener {
    private Toolbar mToolbar;
    private Drawer mDatingDrawer;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    private CharSequence Titles[] = {"Contacts", "Feeds"};
    private int Numboftabs = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ConnectionsActivity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatingDrawer = NavigationDrawerItems.createDrawer(this);

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
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
        return NavigationDrawerItems.DRAWER_ID_CONNECTIONS;
    }

    @Override
    public void onClick(View view) {

    }
}
