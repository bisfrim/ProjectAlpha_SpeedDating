package com.bizzy.projectalpha.speeddating.activities;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bizzy.projectalpha.speeddating.ActivityWithToolbar;
import com.bizzy.projectalpha.speeddating.NavigationDrawerItems;
import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.WaitForInternetConnectionView;
import com.bizzy.projectalpha.speeddating.fragments.ChooseMatchFragment;
import com.bizzy.projectalpha.speeddating.fragments.MatchesFragment;
import com.mikepenz.materialdrawer.Drawer;

public class FindMatchActivity extends AppCompatActivity implements ActivityWithToolbar, ViewPager.OnPageChangeListener, View.OnClickListener{

    private Toolbar mToolbar;
    private Drawer mDatingDrawer;
    private ImageView mChoosingIcon;
    private ImageView mMatchesIcon;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private WaitForInternetConnectionView mWaitForInternetConnectionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_match);


        mPager = (ViewPager)findViewById(R.id.pager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(this);



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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {

    }


    public class PagerAdapter extends FragmentStatePagerAdapter {

        PagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new ChooseMatchFragment();
                case 1:
                    return new MatchesFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
