package com.bizzy.projectalpha.speeddating.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bizzy.projectalpha.speeddating.ActivityWithToolbar;
import com.bizzy.projectalpha.speeddating.ConnectionDetect;
import com.bizzy.projectalpha.speeddating.NavigationDrawerItems;
import com.bizzy.projectalpha.speeddating.PhotoContent;
import com.bizzy.projectalpha.speeddating.ProjectAlphaClasses;
import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.WaitForInternetConnectionView;
import com.bizzy.projectalpha.speeddating.adapter.UsernearmeParseAdapter;
import com.bizzy.projectalpha.speeddating.loadmore.PersonAdapter;
import com.bizzy.projectalpha.speeddating.models.User;
import com.bizzy.projectalpha.speeddating.viewholder.PersonViewHolder;
import com.google.android.gms.vision.text.Text;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.mikepenz.materialdrawer.Drawer;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;
import com.schibstedspain.leku.LocationPicker;
import com.schibstedspain.leku.LocationPickerActivity;
import com.schibstedspain.leku.tracker.LocationPickerTracker;
import com.schibstedspain.leku.tracker.TrackEvents;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class PeopleNearMeActivity extends AppCompatActivity implements ActivityWithToolbar,View.OnClickListener{

    private Toolbar mToolbar;
    private Drawer mDatingDrawer;
    private List<PhotoContent> dataList;

    private ConnectionDetect connectionDetect;
    private User mCurrentUser;
    private LinearLayout mUserNotFoundLayout;
    private Button mRetryButton;
    private TextView locationChange;

    private TextView mMaleButton, mFemaleButton;
    private ParseGeoPoint mParseGeoPoint;

    private Boolean isInternetPresent = false;

    protected LinearLayout mLoading_ProgressBar;

    //protected ParseRelation<ParseUser> mFriendsRelation;
    protected GridView mListView;
    public static final String TAG = PeopleNearMeActivity.class.getSimpleName();

    protected List<User> mUsers;
    protected MenuItem mSendButton;

    private EasyRecyclerView recyclerView;
    private PersonAdapter adapter;
    private Handler handler = new Handler();

    private int page = 0;
    private boolean hasNetWork = true;
    ViewGroup parent;


    PersonViewHolder personImage;

    private ProgressDialog progressDialog;
    private String getLocationLocality;

    private UsernearmeParseAdapter genderAdaper,currentAdapter;
    private WaitForInternetConnectionView mWaitForInternetConnectionView;
    //swipe to refresh
    private SwipeRefreshLayout swipeRefreshLayout;
    
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        setContentView(R.layout.activity_people_near_me);
        mCurrentUser = (User)User.getCurrentUser();

        mToolbar = (Toolbar) findViewById(R.id.people_near_me_toolbar);

        mListView = (GridView)findViewById(android.R.id.list);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);


        locationChange = (TextView) findViewById(R.id.change_location);
        locationChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LocationPickerActivity.class);
                intent.putExtra(LocationPickerActivity.LAYOUTS_TO_HIDE, "street"); //this is optional if you want to hide some info
                //intent.putExtra(LocationPickerActivity.SEARCH_ZONE, "es_ES"); //this is optional if an specific search location
                intent.putExtra("test", "this is a test");
                startActivityForResult(intent, 1);
            }
        });
        //toggleColor(locationChange);
        initializeLocationPickerTracker();

        //mLoading_ProgressBar = (LinearLayout)findViewById(R.id.loadingProgressBar);
        mWaitForInternetConnectionView = (WaitForInternetConnectionView) findViewById(R.id.wait_for_internet_connection);


        //setup the swipeToRefreshLayout i.e. onSwipeDown, fetch new rooms added
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadGenderUser();
            }
        });
        //configure the swipe refresh colours
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.accent));



        //isInternetPresent = connectionDetect.isConnectingToInternet();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDatingDrawer = NavigationDrawerItems.createDrawer(this);
        mListView.setOnItemClickListener(new OnItemClickListener());


    }

    private void loadGenderUser() {
        //final ProgressDialog dialog = new ProgressDialog(PeopleNearMeActivity.this);
        //dialog.setMessage(getString(R.string.loading_users));
        //dialog.show();
        //mLoading_ProgressBar.setVisibility(View.VISIBLE);
        if(genderAdaper == null){
            genderAdaper = new UsernearmeParseAdapter(this);


        }
        //mLoading_ProgressBar.setVisibility(View.INVISIBLE);
        currentAdapter = genderAdaper;
        mListView.setAdapter(currentAdapter);
        currentAdapter.loadObjects();
        swipeRefreshLayout.setRefreshing(false);


    }


    private void toggleColor(TextView v) {
        if (v.isSelected()){
            v.setHighlightColor(Color.WHITE);
        }else{
            v.setHighlightColor(getResources().getColor(R.color.accent));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Log.d("PeopleNearMe:", "onResume");

        mWaitForInternetConnectionView.checkInternetConnection(new WaitForInternetConnectionView.OnConnectionIsAvailableListener() {
            @Override
            public void onConnectionIsAvailable() {
                loadGenderUser();
                mWaitForInternetConnectionView.close();
            }
        });


        //mCurrentUser = (User)User.getCurrentUser();
        //mFriendsRelation = mCurrentUser.getRelation(User.KEY_FRIENDS_RELATION);

        //create();
       /*
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereWithinMiles(User.COL_GEO_POINT, mCurrentUser.getGeoPoint(), 100);
        query.whereExists(User.COL_GEO_POINT);
        query.orderByAscending(User.KEY_USERNAME);
        query.whereNotEqualTo(User.USER_ID, mCurrentUser.getObjectId());
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                mLoading_ProgressBar.setVisibility(View.INVISIBLE);
                if(e == null){
                    //Success
                    mUsers = users;


                    //Loading only Username
                    String[] usernames = new String[mUsers.size()]; //Set size as same as mUserList from Parse
                    int i = 0;
                    for(ParseUser user: mUsers)
                    {
                        usernames[i] = user.getUsername();
                        i++;
                    }

                    //Remove No Contact label
                    //if(i > 0){
                      //  mContactEmpty = (TextView)findViewById(R.id.contactEmpty);
                        //mContactEmpty.setVisibility(View.INVISIBLE);
                    //}

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            PeopleNearMeActivity.this,
                            android.R.layout.simple_list_item_checked,
                            usernames);


                    mListView = (GridView)findViewById(android.R.id.list);
                    recyclerView = (EasyRecyclerView) findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                    DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, Util.dip2px(getActivity(),0.5f), Util.dip2px(getActivity(),72),0);
                    itemDecoration.setDrawLastItem(false);
                    recyclerView.addItemDecoration(itemDecoration);


                    mListView.setAdapter(adapter);
                    mListView.setChoiceMode(mListView.CHOICE_MODE_MULTIPLE);

                    mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            if (mListView.isItemChecked(position)) {

                            }
                            if(mListView.getCheckedItemCount() > 0){
                                mSendButton.setVisible(true);
                            }else{
                                mSendButton.setVisible(false);
                            }
                        }
                    });




                }else{
                    //Failed - Error
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder error = new AlertDialog.Builder(PeopleNearMeActivity.this);
                    error.setMessage(R.string.error_loading_backend)
                            .setTitle(R.string.error_label)
                            .setNeutralButton(android.R.string.ok, null);

                    AlertDialog dialog = error.create();

                    dialog.show();
                }
            }
        });*/


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




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_user, menu);
        //mSendButton = menu.getItem(0);
        return true;
    }

    @Override
    public void onClick(View view) {

    }

    private void initializeLocationPickerTracker() {
        LocationPicker.setTracker(new LocationPickerTracker() {
            @Override
            public void onEventTracked(TrackEvents event) {
                //Toast.makeText(PeopleNearMeActivity.this, "Event: " + event.getEventName(), Toast.LENGTH_SHORT)
                  //      .show();
                if(mParseGeoPoint != null){
                    mCurrentUser.setGeoPoint(mParseGeoPoint);
                    mCurrentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            mCurrentUser.fetchInBackground();
                            //Log.d("mCurrentUser Error: " , e.getMessage());
                        }
                    });
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                double latitude = data.getDoubleExtra(LocationPickerActivity.LATITUDE, 0);
                Log.d("LATITUDE****", String.valueOf(latitude));
                double longitude = data.getDoubleExtra(LocationPickerActivity.LONGITUDE, 0);
                Log.d("LONGITUDE****", String.valueOf(longitude));
                mParseGeoPoint = new ParseGeoPoint(latitude, longitude);
                mCurrentUser.setGeoPoint(mParseGeoPoint);
                Log.d("GeoPoint: ", mParseGeoPoint.toString());
                String address = data.getStringExtra(LocationPickerActivity.LOCATION_ADDRESS);
                Log.d("ADDRESS****", String.valueOf(address));
                String postalcode = data.getStringExtra(LocationPickerActivity.ZIPCODE);
                Log.d("POSTALCODE****", String.valueOf(postalcode));
                Bundle bundle = data.getBundleExtra(LocationPickerActivity.TRANSITION_BUNDLE);
                Log.d("BUNDLE TEXT****", bundle.getString("test"));
                Address fullAddress = data.getParcelableExtra(LocationPickerActivity.ADDRESS);
                locationChange.setText(fullAddress.getLocality());
                Log.d("FULL ADDRESS****", fullAddress.toString());

            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }




    private class OnItemClickListener implements GridView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent profileIntent = new Intent(PeopleNearMeActivity.this, UsersProfileActivity.class);
            profileIntent.putExtra(UsersProfileActivity.EXTRA_USER_ID, currentAdapter.getItem(position).getObjectId());
            PeopleNearMeActivity.this.startActivity(profileIntent);
        }
    }
}