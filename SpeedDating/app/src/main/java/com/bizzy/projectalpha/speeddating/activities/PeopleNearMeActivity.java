package com.bizzy.projectalpha.speeddating.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bizzy.projectalpha.speeddating.ActivityWithToolbar;
import com.bizzy.projectalpha.speeddating.ConnectionDetect;
import com.bizzy.projectalpha.speeddating.NavigationDrawerItems;
import com.bizzy.projectalpha.speeddating.PhotoContent;
import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.adapter.ViewPagerAdapter;
import com.bizzy.projectalpha.speeddating.layout_tab.SlidingTabLayout;
import com.bizzy.projectalpha.speeddating.models.User;
import com.mikepenz.materialdrawer.Drawer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class PeopleNearMeActivity extends AppCompatActivity implements ActivityWithToolbar {

    private Toolbar mToolbar;
    private Drawer mDatingDrawer;
    private List<PhotoContent> dataList;

    private ConnectionDetect connectionDetect;
    private User mCurrentUser;
    private LinearLayout mUserNotFoundLayout;
    private Button mRetryButton;

    private TextView mMaleButton, mFemaleButton;
    List<User> mUsers;

    private Boolean isInternetPresent = false;

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Male","Female"};
    int Numboftabs =2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_people_near_me);


        mCurrentUser = (User)User.getCurrentUser();

      /*  mMaleButton = (TextView) findViewById(R.id.button_male);
        mFemaleButton = (TextView) findViewById(R.id.button_female);

        mUserNotFoundLayout = (LinearLayout)findViewById(R.id.layout_userNotFound_wrapper);
        mRetryButton = (Button) findViewById(R.id.button_retry);

        final TelephonyManager tm =(TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        Config.log(tm.getDeviceId());


        mMaleButton.setOnClickListener(this);
        mFemaleButton.setOnClickListener(this);
        mRetryButton.setOnClickListener(this);*/

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //isInternetPresent = connectionDetect.isConnectingToInternet();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("People Near Me");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDatingDrawer = NavigationDrawerItems.createDrawer(this);



        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

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
                return getResources().getColor(R.color.colorPrimary);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);


    }


    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
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
        return NavigationDrawerItems.DRAWER_ID_PEOPLE_NEAR_ME;
    }



  /*  protected void setMaleButtonSelected(boolean selected){
        if(selected){
            mMaleButton.setTextColor(getResources().getColor(R.color.alizarin));
            mMaleButton.setSelected(true);
            setFemaleButtonSelected(false);
        } else {
            mMaleButton.setTextColor(getResources().getColor(R.color.text_color));
            mMaleButton.setSelected(false);
        }
    }

    protected void setFemaleButtonSelected(boolean selected){
        if(selected){
            mFemaleButton.setTextColor(getResources().getColor(R.color.alizarin));
            mFemaleButton.setSelected(true);
            setMaleButtonSelected(false);
        } else {
            mFemaleButton.setTextColor(getResources().getColor(R.color.text_color));
            mFemaleButton.setSelected(false);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.button_male:
                setMaleButtonSelected(true);
                //getMaleUsers();
                break;
            case R.id.button_female:
                setFemaleButtonSelected(true);
                //getFemaleUsers();
                break;
            case  R.id.button_retry:
                //getBothUsers();
                break;
        }

    }*/



}
