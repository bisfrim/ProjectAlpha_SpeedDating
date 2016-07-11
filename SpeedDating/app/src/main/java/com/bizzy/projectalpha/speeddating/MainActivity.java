package com.bizzy.projectalpha.speeddating;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.view.BezelImageView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import pl.aprilapps.easyphotopicker.EasyImage;

import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;
import nl.changer.polypicker.utils.ImageInternalFetcher;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    protected String LOG_TAG = "MainActivity";

    private UserUploadedPhotos userUploadedPhotos;
    private ParseImageView imagePreview;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int INTENT_REQUEST_GET_N_IMAGES = 14;

    private ParseObject imgupload;
    private ParseFile userPhotoFiles;

    private Context mContext;

    private ViewGroup mSelectedImagesContainer;
    HashSet<Uri> mMedia = new HashSet<Uri>();

    //Layouts
    CoordinatorLayout mProfileContentLayout;
    User mCurrentUser;
    EditText usernameField;
    TextView orientationField;
    TextView birthdayView, userBio;
    TextView userLoc;
    EditText usernameView;
    TextView text_gender;
    LinearLayout mErrorLayout;
    RelativeLayout mWaitLayout;
    private CharSequence mTitle;

    EditProfileFragment mProfileEditFragment;

    //drawer
    Drawer mNavigationDrawer;

    //ImageView
    private ImageView mGenderImage;
    private ImageView mOrientationImage;
    //private ImageView mGenderFemaleImage;

    //Profile drawer item
    ProfileDrawerItem mProfileDrawerItem;

    //ImageView
    ImageView mProfilePhoto, userImageView;
    FloatingActionButton editInfoBtn;

    //account header
    AccountHeader mAccountHeader;

    //Intents
    Intent mLoginIntent;

    //Callback
    private final FetchCallback fetchUserCallback = new FetchCallback();

    public void updateInfo() {
        if (fetchUserCallback != null)
            mCurrentUser = new User();
            mCurrentUser.fetchIfNeededInBackground(fetchUserCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //ButterKnife.bind(this);
        userUploadedPhotos = new UserUploadedPhotos();

        mContext = MainActivity.this;

        mSelectedImagesContainer = (ViewGroup) findViewById(R.id.selected_photos_container);

        View userImageHolder = LayoutInflater.from(this).inflate(R.layout.media_layout, null);

        userImageView = (ImageView) userImageHolder.findViewById(R.id.media_image);

        imagePreview = (ParseImageView) findViewById(R.id.media_image);

        mProfileContentLayout = (CoordinatorLayout) findViewById(R.id.profile_content_layout);

        mProfilePhoto = (ImageView) mProfileContentLayout.findViewById(R.id.profile_photo);
        //editInfoBtn = (FloatingActionButton)findViewById(R.id.edit_button);
        //usernameField = (EditText) findViewById(R.id.edit_nickname);
        usernameView = (EditText) findViewById(R.id.username);
        birthdayView = (TextView) findViewById(R.id.edit_age);
        orientationField = (TextView) findViewById(R.id.edit_orientation);
        userBio = (TextView) findViewById(R.id.about_me);
        mGenderImage = (ImageView) findViewById(R.id.user_gender);
        mOrientationImage = (ImageView)findViewById(R.id.orientation_image) ;
        text_gender = (TextView) findViewById(R.id.text_gender);
        //mGenderFemaleImage = (ImageView) findViewById(R.id.image_female);

        userLoc = (TextView) findViewById(R.id.user_location);
        //streetLocation = (TextView)findViewById(R.id.street_location);

        getUsersLocation(userLoc); //get the user loacation when the activity starts


        //Initialize toolbar
        CharSequence mDrawerTitle;
        mTitle = mDrawerTitle = getTitle();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //editInfoBtn.setOnClickListener(this);

        mCurrentUser = (User) User.getCurrentUser();
        mCurrentUser.setOnline(true);
        mCurrentUser.saveInBackground();
        mCurrentUser.fetchIfNeededInBackground(fetchUserCallback);

        mProfileDrawerItem = new ProfileDrawerItem().withEmail(mCurrentUser.getEmail()).withName(mCurrentUser.getNickname());
        mProfileDrawerItem.withIdentifier(Constant.ID_PROFILE_DRAWER_ITEM);

        mAccountHeader = new AccountHeaderBuilder()
                .withActivity(MainActivity.this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        mProfileDrawerItem
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                        return false;
                    }
                }).build();


        //Navigational drawer using 3party library
        mNavigationDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(mAccountHeader)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_profile).withIcon(FontAwesome.Icon.faw_user).withIdentifier(Constant.DRAWER_ID_PROFILE),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_people_near_me).withIcon(FontAwesome.Icon.faw_users).withIdentifier(Constant.DRAWER_ID_PEOPLE_NEAR_ME),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_start_match).withIcon(FontAwesome.Icon.faw_heart_o).withIdentifier(Constant.DRAWER_ID_START_MATCH),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_messages).withIcon(FontAwesome.Icon.faw_envelope_o).withIdentifier(Constant.DRAWER_ID_MESSAGES),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_users_near_me).withIcon(FontAwesome.Icon.faw_wrench).withIdentifier(Constant.DRAWER_ID_SETTINGS),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_logout).withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(Constant.DRAWER_ID_LOGOUT)
                )
                .build();

        //fetch and update profile info
        updateProfilePhotoThumb();

        mNavigationDrawer.setOnDrawerItemClickListener(new OnDrawerItemClickListener());


        //Android Marshmallow requires user to have control of permissions
        //These permission has to be granted to use camera and gps
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "Permission Granted");
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "Permission Denied" + deniedPermissions.toString());
            }
        };
        //TedPermission is a 3rd party library
        new TedPermission(MainActivity.this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("we need permission for read camera and storage")
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();

        //Fab button for adding images and editing user profile
        FloatingActionsMenu rightLabels = (FloatingActionsMenu) findViewById(R.id.right_labels);
        final com.getbase.floatingactionbutton.FloatingActionButton actionA = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.action_a);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNImages();
            }
        });

        final com.getbase.floatingactionbutton.FloatingActionButton actionB = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.edit_button);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.edit_button: {
                        if(mProfileEditFragment == null)
                            mProfileEditFragment = new EditProfileFragment();
                        Bundle args = new Bundle();
                        if(args == null){
                            //args = new Bundle();
                            args.putInt(EditProfileFragment.ARG_AGE, (Integer) mCurrentUser.getAge());
                            //mProfileEditFragment.setArguments(args);
                        } else {
                            args.putInt(EditProfileFragment.ARG_AGE, (Integer) mCurrentUser.getAge());
                        }
                        //getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_bottom, R.animator.slide_in_top).replace(R.id.profile_main_layout, mProfileEditFragment).commit();
                        Intent editProfileIntent = new Intent(MainActivity.this, EditProfileFragment.class);
                        startActivity(editProfileIntent);
                        break;
                    }
                }

            }
        });


        //ParseQuery query = new ParseQuery("ImageTable");
        //query.whereEqualTo("personPosting", ParseUser.getCurrentUser());
        //query.orderByDescending("createdAt");


        /*ParseFile photoFile = userUploadedPhotos.getParseFile("imageFile");
        if (photoFile != null) {
            imagePreview.setParseFile(photoFile);
            imagePreview.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    // nothing to do
                }
            });
        }*/


        /*ParseQuery<ParseObject> innerQuery = new ParseQuery<ParseObject>("ImageTable");
        innerQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if(list == null){
                    ArrayList<File> parseObj = new ArrayList<File>();
                    for(ParseObject j: list){
                        userPhotoFiles = (ParseFile) j.get("imageFile");
                    }

                }
            }
        });*/

    }

    private void getNImages() {
        Intent intent = new Intent(mContext, ImagePickerActivity.class);
        Config config = new Config.Builder()
                .setTabBackgroundColor(R.color.white)    // set tab background color. Default white.
                .setTabSelectionIndicatorColor(R.color.blue)
                .setCameraButtonColor(R.color.orange)
                .setSelectionLimit(30)    // set photo selection limit. Default unlimited selection.
                .build();
        ImagePickerActivity.setConfig(config);
        startActivityForResult(intent, INTENT_REQUEST_GET_N_IMAGES);
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId())
        {
            case R.id.edit_button:{
                Intent editProfileIntent = new Intent(MainActivity.this, EditProfileFragment.class);

                startActivity(editProfileIntent);
                break;
            }
        }*/
    }

    public void usernameClickHandler(View view) {
        //usernae dialogbox hered
        showInputDialog();

    }


    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.uername_input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edit_name);
        // setup a dialog window
        editText.setText(mCurrentUser.getNickname());
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //resultText.setText("Hello, " + editText.getText());
                        //mCurrentUser = (User)parseUser;
                        //editText.setText(mCurrentUser.getUsername());
                        usernameView.setText(editText.getText());
                        mCurrentUser.setNickname(editText.getText().toString());
                        //updateInfo();


                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alert.show();
    }


    //Drawer items
    private class OnDrawerItemClickListener implements Drawer.OnDrawerItemClickListener {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            Log.d("DriwerIdentifer", String.valueOf(drawerItem.getIdentifier()));
            switch (drawerItem.getIdentifier()) {
                case Constant.DRAWER_ID_LOGOUT:
                    mCurrentUser.setOnline(false);
                    mCurrentUser.saveInBackground();
                    ParseUser.logOut();
                    Intent logoutIntent = new Intent(MainActivity.this, UserDispatchActivity.class);
                    logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logoutIntent);
                    MainActivity.this.finish();
                    break;
                case Constant.DRAWER_ID_SETTINGS:
                    Intent userNearMeIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    MainActivity.this.startActivity(userNearMeIntent);
                    MainActivity.this.finish();
                    break;
                case Constant.DRAWER_ID_START_MATCH:
                    Intent startMatchIntent = new Intent(MainActivity.this, FindMatchActivity.class);
                    startActivity(startMatchIntent);
                    MainActivity.this.finish();
                    break;
                case Constant.DRAWER_ID_MESSAGES:
                    Intent messageListIntent = new Intent(MainActivity.this, MessageActivity.class);
                    startActivity(messageListIntent);
                    MainActivity.this.finish();
                    break;

                case Constant.DRAWER_ID_PEOPLE_NEAR_ME:
                    Intent people_near_meIntent = new Intent(MainActivity.this, PeopleNearMeActivity.class);
                    startActivity(people_near_meIntent);
                    MainActivity.this.finish();
                    break;
            }
            return false;
        }
    }

    //fetch and update user info from this callback
    private class FetchCallback implements GetCallback<ParseUser> {

        @Override
        public void done(ParseUser parseUser, ParseException e) {
            if (e != null) {
                mErrorLayout.setVisibility(View.VISIBLE);
                mWaitLayout.setVisibility(View.GONE);
                mProfileContentLayout.setVisibility(View.GONE);
            } else if (parseUser != null) {
                mCurrentUser = (User) parseUser;
                //usernameField.setText(mCurrentUser.getNickname());
                usernameView.setText(mCurrentUser.getNickname());
                userBio.setText(mCurrentUser.getUserBio());
                if (mCurrentUser.getAge() > 0)
                    birthdayView.setText(mCurrentUser.getAge().toString());
                //fit photo into our placeholder
                Glide.with(MainActivity.this)
                        .load(mCurrentUser.getPhotoUrl())
                        .bitmapTransform(new CropCircleTransformation(Glide.get(MainActivity.this).getBitmapPool()))
                        .placeholder(R.drawable.profile_photo_placeholder)
                        .into(mProfilePhoto);

                updateProfilePhotoThumb();

                if (TextUtils.equals(mCurrentUser.getGenderString(), "male"))
                    mGenderImage.setImageResource(R.drawable.ic_male_dark_icon);
                else if (TextUtils.equals(mCurrentUser.getGenderString(), "female"))
                    mGenderImage.setImageResource(R.drawable.ic_female_dark_icon);

                else if(TextUtils.equals(mCurrentUser.getOrientationString(), "straight"))
                    mOrientationImage.setImageResource(R.drawable.ic_straight);


                else if(TextUtils.equals(mCurrentUser.getOrientationString(), "gay"))
                    mOrientationImage.setImageResource(R.drawable.ic_gay);

                else if(TextUtils.equals(mCurrentUser.getOrientationString(), "bisexsual"))
                    mOrientationImage.setImageResource(R.drawable.ic_bi_sexsual);

                else if(TextUtils.equals(mCurrentUser.getOrientationString(), "no_answer"))
                    mOrientationImage.setImageResource(R.drawable.ic_no_answer);

                mProfileContentLayout.setVisibility(View.VISIBLE);


                //show orientation icon and string
                if(mCurrentUser.isBisexsual()){
                    mOrientationImage.setImageResource(R.drawable.ic_bi_sexsual);
                    orientationField.setText(mCurrentUser.getOrientationString());
                }

                if(mCurrentUser.isGay()){
                    mOrientationImage.setImageResource(R.drawable.ic_gay);
                    orientationField.setText(mCurrentUser.getOrientationString());
                }

                if(mCurrentUser.isStraight()){
                    mOrientationImage.setImageResource(R.drawable.ic_straight);
                    orientationField.setText(mCurrentUser.getOrientationString());
                }

                if(mCurrentUser.isNoanswer()){
                    mOrientationImage.setImageResource(R.drawable.ic_no_answer);
                    orientationField.setText(mCurrentUser.getOrientationString());
                }

                //show gender icon and string
                if (mCurrentUser.isMale()) {
                    mGenderImage.setImageResource(R.drawable.ic_male_dark_icon);
                    text_gender.setText(mCurrentUser.getGenderString());
                } else {
                    mGenderImage.setImageResource(R.drawable.ic_female_dark_icon);
                    text_gender.setText(mCurrentUser.getGenderString());
                }
            }


        }
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Log.d("main111", "onBackPressed");
    }

    /*
     * On resume, check and see if a photo has been added to the view
     * If it has, load the image in this activity and make the
     * preview image visible.
     */
    @Override
    protected void onResume() {
        super.onResume();

        ParseFile photoFile = getUserUploadedPhotos().getPhotoFile();
        if (photoFile != null) {
            imagePreview.setParseFile(photoFile);
            imagePreview.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    imagePreview.setVisibility(View.VISIBLE);
                }
            });
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("main111", "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("main111", "onPause");
    }

    private void login() {
        mCurrentUser = (User) ParseUser.getCurrentUser();
        if (mCurrentUser == null) {
            if (mLoginIntent == null) mLoginIntent = new Intent(this, LoginActivity.class);
            startActivity(mLoginIntent);
        }
    }

    //update drawer photo with this, it works for all activities opened
    public void updateProfilePhotoThumb() {
        BezelImageView profilePhotoThumb = (BezelImageView) mAccountHeader.getView().findViewById(R.id.material_drawer_account_header_current);
        if (profilePhotoThumb != null) {
            Glide.with(MainActivity.this).load(mCurrentUser.getPhotoUrl()).centerCrop().into(profilePhotoThumb);
        } else {
            Log.d("profileIcon", "can't find");
        }
    }

    private void selectProfileOptions() {
        EasyImage.openChooser(this, "Pick source");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES || requestCode == INTENT_REQUEST_GET_N_IMAGES) {
                Parcelable[] parcelableUris = data.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (parcelableUris == null) {
                    return;
                }

                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

                if (uris != null) {
                    for (Uri uri : uris) {
                        Log.i(TAG, " uri: " + uri);
                        mMedia.add(uri);
                    }

                    showMedia();
                }
            }
        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                //Handle the image
                onPhotoReturned(imageFile);
            }

            //incase we press cancel on camera delete the photo
            @Override
            public void onCanceled(EasyImage.ImageSource imageSource) {
                if (imageSource == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(MainActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });


    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    private void showMedia() {
        // Remove all views before
        // adding the new ones.
        mSelectedImagesContainer.removeAllViews();

        //File aFile = null;
        byte[] image = null;
        ArrayList<String> thisList = new ArrayList<>();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        Iterator<Uri> iterator = mMedia.iterator();
        ImageInternalFetcher imageFetcher = new ImageInternalFetcher(this, 500);
        while (iterator.hasNext()) {
            Uri uri = iterator.next();


            // showImage(uri);
            Log.i(TAG, " uri: " + uri);
            if (mMedia.size() >= 1) {
                mSelectedImagesContainer.setVisibility(View.VISIBLE);
            }

            View imageHolder = LayoutInflater.from(this).inflate(R.layout.media_layout, null);

            // View removeBtn = imageHolder.findViewById(R.id.remove_media);
            // initRemoveBtn(removeBtn, imageHolder, uri);
            ParseImageView thumbnail = (ParseImageView) imageHolder.findViewById(R.id.media_image);

            if (!uri.toString().contains("content://")) {
                // probably a relative uri
                uri = Uri.fromFile(new File(uri.toString()));
                //thisList.add(uri.toString());
            }


            imageFetcher.loadImage(uri, thumbnail);
            mSelectedImagesContainer.addView(imageHolder);

            ///aFile.listFiles();


            // set the dimension to correctly
            // show the image thumbnail.
            int wdpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
            int htpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics());
            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(wdpx, htpx));

            File aFile = new File(getRealPathFromURI(uri)); //Get the path of image URI
            //thisList.add(String.valueOf(uri.getPath().length()));

            aFile.listFiles(); //this only gets the first image (we want to get all images selected in a array or something similar)
            try {
                image = readInFile(aFile.toString()); // read in the file as a byte array and process as string
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //byte[] bFile = new byte[(int) aFile.length()];

            userPhotoFiles = new ParseFile("user_photo.jpg", image); //name the file as you wish, parse grabs the image as a byte
            userPhotoFiles.saveInBackground(new SaveCallback() {

                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(MainActivity.this,
                                "Error saving: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this,"saving: ", Toast.LENGTH_SHORT).show();
                        MainActivity.this.setResult(Activity.RESULT_OK);
                        _savePost(userPhotoFiles); //upload the image to parse

                    }
                }
            });



        }
    }

    public UserUploadedPhotos getUserUploadedPhotos(){
        return userUploadedPhotos;
    }

    private void addUserPhotoAndReturn(ParseFile photoFile){
        getUserUploadedPhotos().setPhotoFile(photoFile);
    }

    private void _savePost(ParseFile userImageFile) {
        // recall, that ParseFile is BOTH the "pile of data", the actual PNG,
        // but it's also what you save in the "post" table - not unlike you
        // often save a ParseUser as a column in a table.

        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage(getString(R.string.imge_loading));


        imgupload = new ParseObject("ImageTable");
        //imgupload.put("user_photo", "picturePath");
        imgupload.put("personPosting", User.getCurrentUser());
        //imgupload.put("tags", "profileImage");

        if (userImageFile != null) {
            imgupload.put("imageFile", userImageFile);
            // NOTE .. it seems best to "not set it,"
            // if there's NO image file on the post
        }

        dialog.show();
        imgupload.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                dialog.dismiss();
                if (e == null) {
                    //Toast.makeText(MainActivity.this,
                    //"Posting done.", Toast.LENGTH_SHORT).show();

                    Log.d(LOG_TAG, "error posting");

                    MainActivity.this.setResult(Activity.RESULT_OK);
                    //MainActivity.this.finish();

                } else {
                    int errCodeSimple = e.getCode();
                    Toast.makeText(MainActivity.this, errCodeSimple, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //activity result automatically calls this
    private void onPhotoReturned(final File photoFile) {
        //byte[] image = new byte[(int) photoFile.length()];
        /*Glide.with(MainActivity.this)
                .load(photoFile)
                .bitmapTransform(new CropCircleTransformation(Glide.get(MainActivity.this).getBitmapPool()))
                .placeholder(R.drawable.profile_photo_placeholder)
                .into(mProfilePhoto);*/

        Matrix matrix = new Matrix(); //trying to rotate the image to be centered - deosn't work
        //matrix.reset();
        matrix.postRotate(90);
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);


        final ParseFile uploadImage = new ParseFile(stream.toByteArray());

        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage(getString(R.string.progress_uploading));
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        uploadImage.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    return;
                dialog.dismiss();
                mCurrentUser.setProfilePhoto(uploadImage);
                mCurrentUser.setProfilePhotoThumb(uploadImage);
                mCurrentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Snackbar snackbar = Snackbar.make(mProfileContentLayout, "Changes successful saved", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setActionTextColor(Color.YELLOW);
                        snackbar.setAction("UPDATE PROFILE", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //fetusercallacks here
                                mCurrentUser.fetchIfNeededInBackground(fetchUserCallback);
                            }
                        });
                        snackbar.show();
                    }
                });
                Log.d("PICKED FILE", photoFile.toString());

            }
        });
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    //pick a source for the image
    public void imageProfileClickHandler(View view) {
        selectProfileOptions();
    }

    private byte[] readInFile(String path) throws IOException {

        byte[] data = null;
        File file = new File(path);
        InputStream input_stream = new BufferedInputStream(new FileInputStream(file));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        data = new byte[16384]; // 16K
        int bytes_read;

        while ((bytes_read = input_stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytes_read);
        }

        input_stream.close();
        return buffer.toByteArray();
    }

    public void getUsersLocation(final TextView showLocation) {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(MainActivity.this, "Enable location under permissions", Toast.LENGTH_SHORT).show();
            // You need to ask the user to enable the permissions
        } else {
            TrackerSettings settings =
                    new TrackerSettings()
                            .setUseGPS(true)
                            .setUseNetwork(true)
                            .setUsePassive(true)
                            .setTimeBetweenUpdates(10 * 60 * 1000)
                            .setMetersBetweenUpdates(100);

            LocationTracker tracker = new LocationTracker(mContext, settings) {

                @Override
                public void onLocationFound(Location location) {
                    // Do some stuff when a new location has been found.
                    try {

                        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        if (addresses.isEmpty()) {
                            showLocation.setText("Waiting for Location");
                        } else if (location != null || addresses.size() > 0) {
                            //userLoc.setText("Latitude: "+ String.valueOf(mLastLocation.getLatitude())+" - Longitude: "+
                            //String.valueOf(mLastLocation.getLongitude()));


                            String cityName = addresses.get(0).getAddressLine(0);
                            String stateName = addresses.get(0).getAddressLine(1);
                            String countryName = addresses.get(0).getAddressLine(2);

                            showLocation.setText(String.valueOf(stateName));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onTimeout() {

                }

            };
            tracker.startListening();
        }
    }
}
