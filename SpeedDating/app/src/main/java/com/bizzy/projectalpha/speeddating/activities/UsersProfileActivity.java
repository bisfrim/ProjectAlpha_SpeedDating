package com.bizzy.projectalpha.speeddating.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.WaitForInternetConnectionView;
import com.bizzy.projectalpha.speeddating.adapter.ViewPagerAdapter;
import com.bizzy.projectalpha.speeddating.fragments.MenuItemFragment;
import com.bizzy.projectalpha.speeddating.layout_tab.SlidingTabLayout;
import com.bizzy.projectalpha.speeddating.models.Constants;
import com.bizzy.projectalpha.speeddating.models.User;
import com.bizzy.projectalpha.speeddating.profile_header.HeaderView;
import com.bumptech.glide.Glide;
import com.mikepenz.materialdrawer.Drawer;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

public class UsersProfileActivity extends AppCompatActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener, OnMenuItemClickListener, OnMenuItemLongClickListener {

    @Bind(R.id.toolbar_header_view)
    protected HeaderView toolbarHeaderView;

    @Bind(R.id.float_header_view)
    protected HeaderView floatHeaderView;

    @Bind(R.id.appbar)
    protected AppBarLayout appBarLayout;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    private User mUser;
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
    private FloatingActionButton fabConnectUser;
    protected List<ParseUser> mUsersList;
    protected ParseRelation<ParseUser> mContactRelation;
    // protected ParseUser mUserContact;
    public final static String EXTRA_USER_ID = "userId";
    private String TAG = "UserProfileActivity";
    private TextView mTagLineText, mLocationTxt, mInterestTxt, mEthnicityTxt, mOrientationTxt, mGenderTxt;
    private ImageView mOrientationImage,mGenderImage;
    private LinearLayout mOrientationImageItemLayout,mGenderImageItemLayout;

    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initMenuFragment();
        addFragment(new MenuItemFragment(), true, R.id.container);

        mOrientationImageItemLayout = (LinearLayout) findViewById(R.id.layout_orientation_select_item);
        mGenderImageItemLayout = (LinearLayout) findViewById(R.id.layout_gender_select_item);

        mOrientationImage = (ImageView) mOrientationImageItemLayout.findViewById(R.id.orientation_select_image);
        mGenderImage = (ImageView) mGenderImageItemLayout.findViewById(R.id.user_image_gender);

        mTagLineText = (TextView) findViewById(R.id.tagline_bio);
        mLocationTxt = (TextView)findViewById(R.id.user_loaction_txt);
        mInterestTxt = (TextView)findViewById(R.id.user_interest_txt);
        mEthnicityTxt = (TextView)findViewById(R.id.user_ethnicity_txt);
        mOrientationTxt = (TextView)findViewById(R.id.orientation_select_txt);
        mGenderTxt = (TextView)findViewById(R.id.user_gender_txt);

        mWaitForInternetConnectionView = (WaitForInternetConnectionView) findViewById(R.id.wait_for_internet_connection);
        other_users_image = (ImageView) findViewById(R.id.other_user_image);
        onlineStatus = (ImageView) findViewById(R.id.image_online_status);
        mCurrentUser = (User) User.getCurrentUser(); //get the current user
        mContactRelation = mCurrentUser.getRelation(Constants.KEY_CONTACT_RELATION);

        other_users_image.setOnClickListener(this);

        //mUsersList = (List<ParseUser>) ParseUser.getQuery();
    }


    //Check if Friend or User is mCurrentUSer
    private void checkIfFriend(){
        mContactRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> contacts, ParseException e) {
                if(e == null){
                    for(int i = 0; i < mUsersList.size() ;i++ ){
                        //Find all the user from list
                        ParseUser user = mUsersList.get(i);

                        for(ParseUser contact: contacts){
                            //Check if user (from contactRelation) is equal to the one on my mUsersList
                            if(user.getObjectId().equals(contact.getObjectId())) {
                                //Turn check On
                                //mListView.setItemChecked(i, true);
                                //fabConnectUser.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                }else{
                    //Failed - Error
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder error = new AlertDialog.Builder(UsersProfileActivity.this);
                    error.setMessage(R.string.error_loading_backend)
                            .setTitle(R.string.error_label)
                            .setNeutralButton(android.R.string.ok, null);

                    AlertDialog dialog = error.create();

                    dialog.show();
                }
            }
        });
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

        switch (id) {
            case R.id.context_menu:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
            case android.R.id.home:
                onBackPressed();

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void getUserQuery() {

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
                            toolbarHeaderView.bindTo(user.getNickname().concat(String.valueOf(sb.append(", " + user.getAge()))), String.format("%.2f km", user.getGeoPoint().distanceInKilometersTo(user.getGeoPoint()))); //this is not dynamic
                            floatHeaderView.bindTo(user.getNickname(), String.format("%.2f km", user.getGeoPoint().distanceInKilometersTo(mCurrentUser.getGeoPoint())));

                            if (user.getOnlineStatus().equals("online")) {
                                onlineStatus.setImageResource(R.drawable.ic_online_15_0_alizarin);
                            } else {
                                onlineStatus.setImageResource(R.drawable.ic_offline_15_0_alizarin);
                            }

                            mUser = user;

                            mTagLineText.setText(user.getUserBio());
                            mLocationTxt.setText(user.getUserSetLocation());
                            mInterestTxt.setText(user.getUserInterest());
                            mEthnicityTxt.setText(user.getEthnicity());

                            mGenderTxt.setText(user.getGenderString());
                            if(TextUtils.equals(mGenderTxt.getText(), "male")){
                                mGenderImage.setImageResource(R.drawable.ic_male_alizarin_24_8);
                            }else if(TextUtils.equals(mGenderTxt.getText(), "female")){
                                mGenderImage.setImageResource(R.drawable.ic_female_alizarin_24_8);
                            }

                            mOrientationTxt.setText(user.getOrientationString());
                            if(TextUtils.equals(mOrientationTxt.getText(), "straight")){
                                mOrientationImage.setImageResource(R.drawable.ic_straight);
                            }else if(TextUtils.equals(mOrientationTxt.getText(), "gay")){
                                mOrientationImage.setImageResource(R.drawable.ic_gay);
                            }else if(TextUtils.equals(mOrientationTxt.getText(), "bisexsual")){
                                mOrientationImage.setImageResource(R.drawable.ic_bi_sexsual);
                            }else if(TextUtils.equals(mOrientationTxt.getText(), "no_answer")){
                                mOrientationImage.setImageResource(R.drawable.ic_no_answer);
                            }

                            //String gender = mCurrentUser.getGenderString();
                            //String orientation = mCurrentUser.getOrientationString();


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

    public void showPhoto(){
        Intent imageViewerActivity = new Intent(this, ImageViewerActivity.class);
        imageViewerActivity.putExtra(ImageViewerActivity.EXTRA_IMAGE_URL, mUser.getPhotoUrl());
        startActivity(imageViewerActivity);
    }

    public void photoViewer(){
        Intent startPhotoViewer = new Intent(this, PhotoViewerActivity.class);
        startActivity(startPhotoViewer);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.other_user_image:
                //showPhoto();
                photoViewer();
                break;

            default:
                // no-op
        }

    }

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setAnimationDuration(30);
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }


    private List<MenuObject> getMenuObjects() {
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)
        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)
        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);

        MenuObject send = new MenuObject("Send message");
        send.setResource(R.drawable.icn_1);

        MenuObject like = new MenuObject("Like profile");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.icn_2);
        like.setBitmap(b);

        MenuObject addFr = new MenuObject("Add to friends");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.icn_3));
        addFr.setDrawable(bd);

        MenuObject addFav = new MenuObject("Add to favorites");
        addFav.setResource(R.drawable.icn_4);

        MenuObject block = new MenuObject("Block user");
        block.setResource(R.drawable.icn_5);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
        menuObjects.add(block);
        return menuObjects;
    }


    protected void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        invalidateOptionsMenu();
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(containerId, fragment, backStackName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        //Toast.makeText(this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
        if(position == 3){
            if (mCurrentUser != null) {
                final ParseQuery<User> userQuery = User.getUserQuery();
                userQuery.getInBackground(getIntent().getStringExtra(EXTRA_USER_ID), new GetCallback<User>() {
                    @Override
                    public void done(User object, ParseException e) {
                        if (object != null) {
                            //checkIfFriend();
                            mContactRelation.add(object);
                            mCurrentUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(UsersProfileActivity.this, "Connection Added", Toast.LENGTH_LONG).show();
                                        //Success Saved
                                    } else {
                                        //Failed
                                        AlertDialog.Builder error = new AlertDialog.Builder(UsersProfileActivity.this);
                                        error.setMessage(e.getMessage())
                                                .setTitle(R.string.error_label)
                                                .setNeutralButton(android.R.string.ok, null);

                                        AlertDialog dialog = error.create();

                                        dialog.show();
                                    }
                                }
                            });
                        }
                    }
                });
            }

        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        Toast.makeText(this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else {
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context_items, menu);
        return true;
    }
}
