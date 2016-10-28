package com.bizzy.projectalpha.speeddating.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bizzy.projectalpha.speeddating.MainActivityPermissions;
import com.bizzy.projectalpha.speeddating.ProjectAlphaClasses;
import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.models.Constant;
import com.bizzy.projectalpha.speeddating.models.User;
import com.bizzy.projectalpha.speeddating.models.UserUploadedPhotos;
import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.ikkong.wximagepicker.Constants;
import com.ikkong.wximagepicker.ImagePickerAdapter;
import com.ikkong.wximagepicker.ImagePreviewDelActivity;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.GlideImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
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
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yayandroid.locationmanager.LocationBaseActivity;
import com.yayandroid.locationmanager.LocationConfiguration;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.constants.FailType;
import com.yayandroid.locationmanager.constants.LogType;
import com.yayandroid.locationmanager.constants.ProviderType;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import id.zelory.compressor.Compressor;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import nl.changer.polypicker.utils.ImageInternalFetcher;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;


public class MainActivity extends LocationBaseActivity implements View.OnClickListener,
        ImagePickerAdapter.OnRecyclerViewItemClickListener {

    protected String LOG_TAG = "MainActivity";
    public final static String SELECTED_PHOTOS   = "SELECTED_PHOTOS";

    private UserUploadedPhotos userUploadedPhotos;
    private ParseImageView imagePreview;


    private RecyclerView recylerView;
    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList;//当前选择的所有图片
    private int maxImgCount = 30;//允许选择图片最大数

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int INTENT_REQUEST_GET_N_IMAGES = 14;

    private ParseObject imgupload;
    private ParseFile userPhotoFiles;

    private Context mContext;

    private ViewGroup mSelectedImagesContainer;
    HashSet<Uri> mMedia = new HashSet<Uri>();
    private ArrayList<Uri> filePaths;
    private String mPhotoFilePath;

    //Layouts
    CoordinatorLayout mProfileContentLayout;
    User mCurrentUser;
    EditText usernameField;
    TextView orientationField;
    TextView birthdayView, userBio;
    TextView userLoc;
    public EditText usernameView;
    TextView text_gender;
    LinearLayout mErrorLayout;
    LinearLayout mLayout;
    RelativeLayout mWaitLayout;

    private CharSequence mTitle;

    private ProgressDialog locProgressDialog;

    EditProfileFragment mProfileEditFragment;

    //drawer
    Drawer mNavigationDrawer;

    //ImageView
    private ImageView mGenderImage, iv_img;
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
        mLayout = (LinearLayout)findViewById(R.id.mLinLayout);


        Intent mIntent = getIntent();
        String activeUsername = mIntent.getStringExtra("username");

        initImagePicker();//最好放到 Application oncreate执行
        initWidget();

        //ButterKnife.bind(this);
        userUploadedPhotos = new UserUploadedPhotos();

        mContext = MainActivity.this;

        //mSelectedImagesContainer = (ViewGroup) findViewById(R.id.selected_photos_container);

        View userImageHolder = LayoutInflater.from(this).inflate(R.layout.media_layout, null);

        userImageView = (ImageView) userImageHolder.findViewById(R.id.media_image);

        imagePreview = (ParseImageView) findViewById(R.id.media_image);

        mProfileContentLayout = (CoordinatorLayout) findViewById(R.id.profile_content_layout);

        mProfilePhoto = (ImageView) mProfileContentLayout.findViewById(R.id.profile_photo);
        //editInfoBtn = (FloatingActionButton)findViewById(R.id.edit_button);
        //usernameField = (EditText) findViewById(R.id.edit_nickname);
        usernameView = (EditText) mProfileContentLayout.findViewById(R.id.username);
        birthdayView = (TextView) mProfileContentLayout.findViewById(R.id.edit_age);
        orientationField = (TextView) mProfileContentLayout.findViewById(R.id.edit_orientation);
        userBio = (TextView) mProfileContentLayout.findViewById(R.id.about_me);
        mGenderImage = (ImageView) mProfileContentLayout.findViewById(R.id.user_gender);
        mOrientationImage = (ImageView)mProfileContentLayout.findViewById(R.id.orientation_image) ;
        text_gender = (TextView)mProfileContentLayout. findViewById(R.id.text_gender);
        //mGenderFemaleImage = (ImageView) findViewById(R.id.image_female);

        userLoc = (TextView) mProfileContentLayout.findViewById(R.id.user_location);
        //streetLocation = (TextView)findViewById(R.id.street_location);

        //getUsersLocation(userLoc); //get the user loacation when the activity starts

        LocationManager.setLogType(LogType.GENERAL);
        getLocation();


        //Initialize toolbar
        CharSequence mDrawerTitle;
        mTitle = mDrawerTitle = getTitle();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //editInfoBtn.setOnClickListener(this);

        mCurrentUser = (User) User.getCurrentUser();
        //toolbar.setTitle(mCurrentUser.getNickname());
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

        //Fab button for adding images and editing user profile
        FloatingActionsMenu rightLabels = (FloatingActionsMenu) findViewById(R.id.right_labels);
        final com.getbase.floatingactionbutton.FloatingActionButton actionA = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.action_a);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivityPermissions.onPhotCheckPermissions(MainActivity.this);
                //getNImages();
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
                            args.putInt(EditProfileFragment.ARG_AGE, mCurrentUser.getAge());
                            //mProfileEditFragment.setArguments(args);
                        } else {
                            args.putInt(EditProfileFragment.ARG_AGE, mCurrentUser.getAge());
                        }
                        //getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_bottom, R.animator.slide_in_top).replace(R.id.profile_main_layout, mProfileEditFragment).commit();
                        Intent editProfileIntent = new Intent(MainActivity.this, EditProfileFragment.class);
                        startActivity(editProfileIntent);
                        break;
                    }
                }

            }
        });


       /* ParseQuery<ParseObject> mImageQuery = new ParseQuery<ParseObject>("ImageTable");
        mImageQuery.whereEqualTo("username",activeUsername);
        //newest first
        mImageQuery.orderByAscending("createdAt");

        mImageQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null)
                {
                    if(objects.size() > 0)
                    {
                        for(ParseObject mImage: objects)
                        {
                            ParseFile mFile = (ParseFile)mImage.get("imageFile");
                            //download the image file
                            mFile.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if(e == null)
                                    {
                                        //add an image view every time we find a new Image
                                        Bitmap mImageBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        iv_img = (ImageView)findViewById(com.ikkong.wximagepicker.R.id.iv_img);

                                        iv_img.setImageBitmap(mImageBitmap);
                                        //ImageView mImageView = new ImageView(getApplicationContext());
                                        //mImageView.setImageBitmap(mImageBitmap);
                                        //mImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                          //      ViewGroup.LayoutParams.WRAP_CONTENT));
                                        //add imageview to layout
                                        //mLayout.addView(mImageView);
                                    }
                                    else
                                    {
                                        alert(e.getMessage());
                                    }
                                }
                            });
                        }
                    }
                    else
                    {
                        alert("User has no posts yet");
                    }
                }
                else
                {
                    alert(e.getMessage());
                }
            }
        });
*/

    }

    public void alert(String Message) {
        Toast.makeText(getApplicationContext(), Message, Toast.LENGTH_LONG).show();
    }


    private void initWidget() {
        recylerView = (RecyclerView) findViewById(R.id.recylerView);
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this,selImageList,maxImgCount);
        adapter.refresh();
        GridLayoutManager manager = new GridLayoutManager(this,4);
        recylerView.setLayoutManager(manager);
        recylerView.setHasFixedSize(true);
        recylerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(false);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

   /* private void getNImages() {
        Intent intent = new Intent(mContext, ImagePickerActivity.class);
        Config config = new Config.Builder()
                .setTabBackgroundColor(R.color.white)    // set tab background color. Default white.
                .setTabSelectionIndicatorColor(R.color.blue)
                .setCameraButtonColor(R.color.orange)
                .setSelectionLimit(30)    // set photo selection limit. Default unlimited selection.
                .build();
        ImagePickerActivity.setConfig(config);
        startActivityForResult(intent, INTENT_REQUEST_GET_N_IMAGES);
    }*/

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

    @Override
    public void onItemClick(View view, String data) {

        switch(data){
            case Constants.IMAGEITEM_DEFAULT_ADD:
                //打开选择
                //本次允许选择的数量
                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size() + 1);
                //打开选择器
                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                break;
            default:
                //打开预览
                Intent intent1 = new Intent(this, ImagePreviewDelActivity.class);
                intent1.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, adapter.getRealSelImage());
                intent1.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION,Integer.parseInt(data));
                startActivityForResult(intent1,Constants.IMAGE_PREVIEW_ACTIVITY_REQUEST_CODE);
                break;
        }

    }


    //Drawer items
    private class OnDrawerItemClickListener implements Drawer.OnDrawerItemClickListener {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            Log.d("DriwerIdentifer", String.valueOf(drawerItem.getIdentifier()));
            switch ((int) drawerItem.getIdentifier()) {
                case Constant.DRAWER_ID_LOGOUT:
                    mCurrentUser.setOnline(false);
                    mCurrentUser.saveInBackground();
                    //String userId = User.getCurrentUser().getObjectId();
                    //((AuthApplication) getApplication()).unsubscribeFromMessageChannel(userId);
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

        ProjectAlphaClasses.PreferenceSettings.getConfigHelper();

        if (getLocationManager().isWaitingForLocation()
                && !getLocationManager().isAnyDialogShowing()) {
            displayProgress();
        }

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
        dismissProgress();
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
        EasyImage.openChooserWithDocuments(this, "Pick source", 0);
        //EasyImage.openChooserWithGallery(this, "Pick source", 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);

/*
                try {
                    Uri selectedImage = data.getData();
                    filePaths = new ArrayList<>();
                    filePaths.add(selectedImage);
                    Bitmap mBitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    //lets put the image in the image view
                    //mImageView.setImageBitmap(mBitmapImage);
                    Log.i(getPackageName(), "Image Recieved");
                    //attempt Image upload
                    //convert image into byte array
                    ByteArrayOutputStream mImageStream = new ByteArrayOutputStream();
                    mBitmapImage.compress(Bitmap.CompressFormat.PNG, 100, mImageStream);
                    byte[] mImageBytes = mImageStream.toByteArray();

                    ParseFile mImage = new ParseFile("user_photo.png", mImageBytes);

                    ParseObject mImageUploadObject = new ParseObject("ImageTable");
                    mImageUploadObject.put("username", User.getCurrentUser());
                    mImageUploadObject.put("imageFile", mImage);
                    //give the object a public read access
                    ParseACL mAcl = new ParseACL();
                    mAcl.setPublicReadAccess(true);
                    mImageUploadObject.setACL(mAcl);

                    mImageUploadObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                alert("Your Image has been Posted :)");
                            } else
                            {
                                alert(e.getMessage());
                            }
                        }
                    });

                }
                catch(Exception e)
                {
                    alert(e.getMessage());
                }*/

                selImageList.addAll(selImageList.size()-1,images);
                adapter.refresh();

            }
        }else if(resultCode == ImagePicker.RESULT_CODE_BACK){
            if (data != null&& requestCode == Constants.IMAGE_PREVIEW_ACTIVITY_REQUEST_CODE) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);

                selImageList.clear();
                selImageList.addAll(images);
                adapter.refresh();


            }
        }

      /*  if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES || requestCode == INTENT_REQUEST_GET_N_IMAGES) {
                Parcelable[] parcelableUris = data.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
                //filePaths = data.getStringArrayListExtra(SELECTED_PHOTOS);

                if (parcelableUris == null) {
                    return;
                }

                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);
                byte[] image = null;

                if (uris != null) {
                    for (Uri uri : uris) {
                        Log.i(TAG, " uri: " + uri);
                        filePaths = new ArrayList<>();
                        filePaths.add(uri);
                        Log.d(TAG, "LIST IMAGES:" + filePaths);
                        try {
                            image = readInFile(filePaths.toString()); // read in the file as a byte array and process as string
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        //byte[] bFile = new byte[(int) aFile.length()];

                        userPhotoFiles = new ParseFile("user_photo.jpg", image); //name the file as you wish, parse saves the image as a byte
                        userPhotoFiles.saveInBackground(new SaveCallback() {

                            public void done(ParseException e) {
                                if (e != null) {
                                    Toast.makeText(MainActivity.this,
                                            "Error saving: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "saving: ", Toast.LENGTH_SHORT).show();
                                    MainActivity.this.setResult(Activity.RESULT_OK);
                                    _savePost(userPhotoFiles); //upload the image to parse

                                }
                            }
                        });

                        mMedia.add(uri);
                    }
                    showMedia();
                }
            }
        }*/

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }


            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                //Handle the image

                File compressedImage = null;
                compressedImage =  Compressor.getDefault(getApplicationContext()).compressToFile(imageFile);
                onPhotoReturned(compressedImage);
            }


            //incase we press cancel on camera delete the photo
            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
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
            }


            imageFetcher.loadImage(uri, thumbnail);
            mSelectedImagesContainer.addView(imageHolder);
            // set the dimension to correctly
            // show the image thumbnail.
            int wdpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
            int htpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics());
            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(wdpx, htpx));


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
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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

 /*   public void getUsersLocation(final TextView showLocation) {

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
    }*/



    ///Check LocationConfiguration documentation for descriptions on required methods to use
    @Override
    public LocationConfiguration getLocationConfiguration() {
        return new LocationConfiguration()
                .keepTracking(true)
                .askForGooglePlayServices(true)
                .askForSettingsApi(true)
                .askForEnableGPS(true)
                .setMinAccuracy(200.0f)
                .setTimeInterval(10 * 1000) //time interval to get for location updates, this is in milliseconds
                .setWaitPeriod(ProviderType.GOOGLE_PLAY_SERVICES, 5 * 1000)
                .setWaitPeriod(ProviderType.GPS, 10 * 1000)
                .setWaitPeriod(ProviderType.NETWORK, 5 * 1000)
                .setGPSMessage("GPS Permission required")
                .setRationalMessage("We need permission for GPS?, If you reject permission, you cannot use this service\n\n" +
                        "Please turn on permissions at [Setting] > [Permission]");

//we need permission for read camera and storage
        //If you reject permission,you can not use this service

        //Please turn on permissions at [Setting] > [Permission]"
    }

    @Override
    public void onLocationFailed(int failType) {
        dismissProgress();

        switch (failType) {
            case FailType.PERMISSION_DENIED: {
                userLoc.setText("Couldn't get location, because user didn't give permission!");
                break;
            }
            case FailType.GP_SERVICES_NOT_AVAILABLE:
            case FailType.GP_SERVICES_CONNECTION_FAIL: {
                userLoc.setText("Couldn't get location, because Google Play Services not available!");
                break;
            }
            case FailType.NETWORK_NOT_AVAILABLE: {
                userLoc.setText("Couldn't get location, because network is not accessible!");
                break;
            }
            case FailType.TIMEOUT: {
                userLoc.setText("Couldn't get location, and timeout!");
                break;
            }
            case FailType.GP_SERVICES_SETTINGS_DENIED: {
                userLoc.setText("Couldn't get location, because user didn't activate providers via settingsApi!");
                break;
            }
            case FailType.GP_SERVICES_SETTINGS_DIALOG: {
                userLoc.setText("Couldn't display settingsApi dialog!");
                break;
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        dismissProgress();
        setTextLocation(location);
    }


    private void displayProgress() {
        if (locProgressDialog == null) {
            locProgressDialog = new ProgressDialog(this);
            locProgressDialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
            locProgressDialog.setMessage("Getting location...");
        }

        if (!locProgressDialog.isShowing()) {
            locProgressDialog.show();
        }
    }

    private void dismissProgress() {
        if (locProgressDialog != null && locProgressDialog.isShowing()) {
            locProgressDialog.dismiss();
        }
    }


    public void setTextLocation(Location location) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            //String cityName = addresses.get(0).getAddressLine(0);
            String stateName = addresses.get(0).getLocality();
            //String countryName = addresses.get(0).getAddressLine(2);

            //String appendValue = location.getLatitude() + ", " + location.getLongitude() + "\n";

            String newValue;
            CharSequence current = userLoc.getText();

            if (!TextUtils.isEmpty(current)) {
                userLoc.getText();
            } else {
                userLoc.setText(stateName);
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


