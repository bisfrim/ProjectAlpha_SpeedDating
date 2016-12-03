package com.bizzy.projectalpha.speeddating.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.WaitForInternetConnectionView;
import com.bizzy.projectalpha.speeddating.adapter.ViewPagerAdapter;
import com.bizzy.projectalpha.speeddating.layout_tab.SlidingTabLayout;
import com.bizzy.projectalpha.speeddating.models.User;
import com.bizzy.projectalpha.speeddating.profile_header.HeaderView;
import com.bumptech.glide.Glide;
import com.joaquimley.faboptions.FabOptions;
import com.mikepenz.materialdrawer.Drawer;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UsersProfileActivity extends AppCompatActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    @Bind(R.id.toolbar_header_view)
    protected HeaderView toolbarHeaderView;

    @Bind(R.id.float_header_view)
    protected HeaderView floatHeaderView;

    @Bind(R.id.appbar)
    protected AppBarLayout appBarLayout;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    private CharSequence Titles[] = {"Photos", "Details"};
    private int Numboftabs = 2;
    private boolean isHideToolbarView = false;
    private Toolbar mToolbar;
    private Drawer mDatingDrawer;
    private User mCurrentUser;
    private int position = 1;
    private Handler mHandler;
    private ImageView other_users_image, onlineStatus;
    private WaitForInternetConnectionView mWaitForInternetConnectionView;
    public final static String EXTRA_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mWaitForInternetConnectionView = (WaitForInternetConnectionView)findViewById(R.id.wait_for_internet_connection);
        other_users_image = (ImageView) findViewById(R.id.other_user_image);
        onlineStatus = (ImageView) findViewById(R.id.image_online_status);
        mCurrentUser = (User) User.getCurrentUser(); //get the current user

        FabOptions fabOptions = (FabOptions) findViewById(R.id.fab_options);
        fabOptions.setOnClickListener(this);

       // call the header view

    /*    adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

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
        tabs.setViewPager(pager);*/
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mHandler = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUi();
    }


    private int isPositionHeader() {
        return position;
    }

    private void initUi() {
        appBarLayout.addOnOffsetChangedListener(this);
        mWaitForInternetConnectionView.checkInternetConnection(new WaitForInternetConnectionView.OnConnectionIsAvailableListener() {
            @Override
            public void onConnectionIsAvailable() {
                ParseQuery<User> userQuery = User.getUserQuery();
                Log.d("ProjectAlpha:UserProfA", getIntent().getStringExtra(EXTRA_USER_ID));
                userQuery.getInBackground(getIntent().getStringExtra(EXTRA_USER_ID), new GetCallback<User>() {
                    @Override
                    public void done(User user, ParseException e) {
                        if (user != null) {
                            //getSupportActionBar().setTitle(user.getNickname());
                            StringBuilder sb = new StringBuilder();
                            //sb.append(", "+ user.getAge());
                            toolbarHeaderView.bindTo(user.getNickname().concat(String.valueOf(sb.append(", "+ user.getAge()))), String.format("%.2f km", user.getGeoPoint().distanceInKilometersTo(user.getGeoPoint()))); //this is not dynamic
                            floatHeaderView.bindTo(user.getNickname(), String.format("%.2f km", user.getGeoPoint().distanceInKilometersTo(mCurrentUser.getGeoPoint())));

                            if(user.getOnlineStatus().equals("online")) {
                                onlineStatus.setImageResource(R.drawable.ic_online_15_0_alizarin);
                            } else {
                                onlineStatus.setImageResource(R.drawable.ic_offline_15_0_alizarin);
                            }

                            Glide.with(UsersProfileActivity.this)
                                    .load(user.getPhotoUrl())
                                    //.fitCenter()
                                    //.centerCrop()
                                    .into(other_users_image);
                        }
                        mWaitForInternetConnectionView.close();

                    }
                });
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    @Override
    public void onClick(View view) {

    }
}
