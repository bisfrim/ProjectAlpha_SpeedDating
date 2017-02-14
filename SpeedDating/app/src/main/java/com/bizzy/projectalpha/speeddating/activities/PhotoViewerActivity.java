package com.bizzy.projectalpha.speeddating.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.adapter.PhotosAdapter;
import com.bizzy.projectalpha.speeddating.models.Album;
import com.bizzy.projectalpha.speeddating.models.Photo;
import com.bizzy.projectalpha.speeddating.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class PhotoViewerActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    public final static String EXTRA_ID = "userId";
    private User mCurrentUser;
    //protected GridView mGridView;

    GridView listView;
    String username = "user";
    ArrayList<Photo> photos = new ArrayList<>();
    ArrayList<Album> albums = new ArrayList<>();
    public static PhotosAdapter adapter = null;
    boolean hasAccess;
    ArrayList<String> users = new ArrayList<String>();
    JSONArray usersArray;
    final private int PICTURE_REQUEST = 1;
    LinearLayout imageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        mCurrentUser = (User) User.getCurrentUser();


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("Message");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(UsersProfileActivity.mUser.concat("'s") + "  - Photo's");

        //mGridView = (GridView)findViewById(android.R.id.list);


        listView = (GridView) findViewById(R.id.listViewPhotosListing);

        //String username = getIntent().getStringExtra("username");
        //imageLayout = (LinearLayout) findViewById(R.id.imageLayout);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserUploadedPhotos");
        query.whereEqualTo("author", UsersProfileActivity.mUser2);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    //Log.d("demo", "Retrieved " + photoList.size() + " albums");
                    for (ParseObject p : objects) {
                        if(p.getBoolean("approved")){
                            Photo photo = new Photo();

                            photo.setAlbum(p.getString("albumTitle"));
                            photo.setImageUrl(p.getParseFile("photo").getUrl());
                            photo.setId(p.getObjectId());

                            photos.add(photo);

                        }
                    }

                    //Call listView adapter
                    adapter = new PhotosAdapter(PhotoViewerActivity.this, R.layout.photos_list_layout, photos, username);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String temp2Id;
                            temp2Id = photos.get(position).getId();
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("UserUploadedPhotos");
                            query.getInBackground(temp2Id, new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    String tempUrl;
                                    tempUrl = object.getParseFile("photo").getUrl();
                                    Intent i = new Intent(getBaseContext(), SingleImageViewActivity.class);
                                    i.putExtra("URL", tempUrl);
                                    startActivity(i);
                                }
                            });
                        }
                    });

                }
            }
        });


    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
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

    }

    public void alert(String Message) {
        Toast.makeText(getApplicationContext(), Message, Toast.LENGTH_LONG).show();
    }

}
