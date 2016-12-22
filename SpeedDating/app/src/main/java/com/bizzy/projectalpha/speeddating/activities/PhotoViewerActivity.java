package com.bizzy.projectalpha.speeddating.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.WaitForInternetConnectionView;
import com.bizzy.projectalpha.speeddating.adapter.PhotoListAdapter;
import com.bizzy.projectalpha.speeddating.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PhotoViewerActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    public final static String EXTRA_ID = "userId";
    private User mCurrentUser;
    private Intent mProfileIntent;
    private String userToId;
    private PhotoListAdapter myPhotoList,currentPhotoAdapter;
    protected GridView mGridView;
    //private WaitForInternetConnectionView mWaitForConnectionView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        mCurrentUser = (User) User.getCurrentUser();

        mProfileIntent = getIntent();
        userToId = mProfileIntent.getStringExtra(EXTRA_ID);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("Message");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setTitle("Photo's");

        mGridView = (GridView)findViewById(android.R.id.list);


        ParseQuery<User> userQuery = User.getUserQuery();
        //Log.d("ProjectAlpha:Photo", getIntent().getStringExtra(EXTRA_ID));
        userQuery.getInBackground(userToId, new GetCallback<User>() {
            @Override
            public void done(User user, ParseException e) {
                if(user != null){
                    getSupportActionBar().setTitle(user.getNickname());
                }
            }
        });


    }

    private void loadPhotos(){
        if(myPhotoList == null){
            myPhotoList = new PhotoListAdapter(this);
        }
        currentPhotoAdapter = myPhotoList;
        mGridView.setAdapter(currentPhotoAdapter);
        currentPhotoAdapter.loadObjects();


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
        loadPhotos();

    }

    public void alert(String Message) {
        Toast.makeText(getApplicationContext(), Message, Toast.LENGTH_LONG).show();
    }

}
