package com.bizzy.projectalpha.speeddating.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bizzy.projectalpha.speeddating.ProjectAlphaClasses;
import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.models.User;
import com.bizzy.projectalpha.speeddating.profile_header.HeaderView;
import com.bumptech.glide.Glide;
import com.codemybrainsout.placesearch.PlaceSearchDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.thomashaertel.widget.MultiSpinner;
import com.toptoche.multiselectwidget.MultiSelectView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This activity updates our user profile
 */
public class EditProfileFragment extends AppCompatActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    @Bind(R.id.toolbar_header_view)
    protected HeaderView toolbarHeaderView;

    @Bind(R.id.float_header_view)
    protected HeaderView floatHeaderView;

    @Bind(R.id.appbar)
    protected AppBarLayout appBarLayout;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    private boolean isHideToolbarView = false;

    static final boolean DEBUG = true;
    static final String  LOG_TAG = "EditProfileFragment:";

    private Context mContext;
    private int currentAge;
    private static final String TAG = "RxValidator";
    private static final String dateFormat = "dd-MM-yyyy";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
    private Date convertDate;
    private String emptyUserInterest;

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private String currentLoc;


    private Handler mHandler;
    public static String ARG_AGE = "age";
    private EditText editUsername, userAge, userBio;
    private TextInputEditText userLocation;
    private ImageView mMaleImage,mFemaleImage,mStriaghtImage,mGayImage,mBisexsualImage, mNoanswerImge;
    private boolean mMaleSelected,mFemaleSelected = false;
    private boolean mStraightSelected,mGaySelected,mBisexsualSelected,mNoanswerSelcted = false;
    private Button saveUserInfo, cancelInfo;
    private ProgressDialog dialog;
    private StringBuilder builder;
    private ImageView imgAdd;

    private MultiSpinner spinner;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> interestAdapter;


    //Views
    private LinearLayout mMaleSelectionItemLayout;
    private LinearLayout mFemaleSelectionItemLayout;
    private LinearLayout mStraightSelectionItemLayout;
    private LinearLayout mGaySelectionItemLayout;
    private LinearLayout mBiSexsualSelectionLayout;
    private LinearLayout mNoanswerSelectionLayout;

    private TextView mMaleText,mGayText,mStraightTetxt,mBisexsualText, mNoanswerText;
    private TextView mFemaleText, ethnicityView, ethnicityLabel;

    private RadioGroup orientationGroup;

    private MultiSelectView multiSelectView; //interest widget

    private User mCurrentUser;
    MainActivity mMainActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) { // onCreate is called when the activity starts
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_profile);
        ButterKnife.bind(this);

        Bundle parameters = new Bundle();

        mMainActivity = new MainActivity();
        mContext = EditProfileFragment.this;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mCurrentUser = (User) User.getCurrentUser(); //get the current user
        initUi();// call the header view

        imgAdd = (ImageView) findViewById(R.id.user_image);
        Glide.with(EditProfileFragment.this).load(mCurrentUser.getPhotoUrl()).centerCrop().into(imgAdd);

        //mCurrentUser.getEthnicity();

        multiSelectView = (MultiSelectView) findViewById(R.id.m);

        //Gender selection config
        mMaleSelectionItemLayout = (LinearLayout) findViewById(R.id.layout_male_select_item);
        mFemaleSelectionItemLayout = (LinearLayout) findViewById(R.id.layout_female_select_item);

        mMaleImage = (ImageView) mMaleSelectionItemLayout.findViewById(R.id.image_male);
        mMaleText = (TextView) mMaleSelectionItemLayout.findViewById(R.id.text_male);

        mFemaleImage = (ImageView) mFemaleSelectionItemLayout.findViewById(R.id.image_female);
        mFemaleText = (TextView) mFemaleSelectionItemLayout.findViewById(R.id.text_female);

        //Orientation selection config
        mStraightSelectionItemLayout = (LinearLayout)findViewById(R.id.layout_straight_select_item);
        mGaySelectionItemLayout = (LinearLayout)findViewById(R.id.layout_gay_select_item);
        mBiSexsualSelectionLayout = (LinearLayout)findViewById(R.id.layout_bi_select_item);
        mNoanswerSelectionLayout = (LinearLayout)findViewById(R.id.layout_noanswer_select_item);

        mStriaghtImage = (ImageView)mStraightSelectionItemLayout.findViewById(R.id.striaght_image);
        mStraightTetxt = (TextView)mStraightSelectionItemLayout.findViewById(R.id.straight_radio);

        mGayImage = (ImageView)mGaySelectionItemLayout.findViewById(R.id.gay_image);
        mGayText = (TextView)mGaySelectionItemLayout.findViewById(R.id.gay_radio);

        mBisexsualImage = (ImageView)mBiSexsualSelectionLayout.findViewById(R.id.bi_image);
        mBisexsualText = (TextView)mBiSexsualSelectionLayout.findViewById(R.id.bi_radio);

        mNoanswerImge = (ImageView)mNoanswerSelectionLayout.findViewById(R.id.no_answer_image);
        mNoanswerText = (TextView)mNoanswerSelectionLayout.findViewById(R.id.no_answer_radio);

        //Gender Listener
        mMaleSelectionItemLayout.setOnClickListener(this);
        mFemaleSelectionItemLayout.setOnClickListener(this);

        //Orientation Listener
        mStraightSelectionItemLayout.setOnClickListener(this);
        mBiSexsualSelectionLayout.setOnClickListener(this);
        mGaySelectionItemLayout.setOnClickListener(this);
        mNoanswerSelectionLayout.setOnClickListener(this);

        //save and cancel button/listeners
        saveUserInfo = (Button) findViewById(R.id.save_btn);
        saveUserInfo.setOnClickListener(this);
        cancelInfo = (Button)findViewById(R.id.cancel_btn);
        cancelInfo.setOnClickListener(this);

        //get current username
        editUsername = (EditText) findViewById(R.id.edit_name);
        editUsername.setText(mCurrentUser.getNickname());

        //get current user bio
        userBio = (EditText)findViewById(R.id.edit_bio);
        userBio.setText(mCurrentUser.getUserBio());

        userLocation = (TextInputEditText)findViewById(R.id.edit_loaction);
        userLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    showPlacePickerDialog();
                }
                return false;
            }
        });

        //get the location set by the user
        if(mCurrentUser.getString("userSetLocation") != null){
            userLocation.setText(mCurrentUser.getUserSetLocation());
        }

        userAge = (EditText) findViewById(R.id.user_age);

        setDatePickerListener(userAge); //set the datepicker

        if (parameters == null)
            parameters.putInt(EditProfileFragment.ARG_AGE, mCurrentUser.getAge());
        else {
            parameters.putInt(EditProfileFragment.ARG_AGE, mCurrentUser.getAge());
        }

        //Check for negative input for user age
        if (parameters != null && parameters.getInt(ARG_AGE) != -1) {
            userAge.setText(String.valueOf(parameters.getInt(ARG_AGE)));
        }


        userEthnicity();
        //multiSelectView.setText(mCurrentUser.getUserInterest()); //get


        // return the selected string and add a redLine to the selected item
        if (mCurrentUser != null) {
            String gender = mCurrentUser.getGenderString();
            String orientation = mCurrentUser.getOrientationString();

            //Gender selection
            if (TextUtils.equals(gender, "male")) selectMaleItem();
            else if (TextUtils.equals(gender, "female")) selectFemaleItem();

            //Orientation selection
            if(TextUtils.equals(orientation, "straight")) selectedStraightItem();
            else if (TextUtils.equals(orientation, "gay"))selectedGayItem();
            else if(TextUtils.equals(orientation, "bisexsual"))selectedBisexsualItem();
            else if(TextUtils.equals(orientation, "no_answer"))selectedNoanswerItem();
        }

        //use regular expression to split comma, quotes white spaces,etc..
        // when retrieving user selected interest from parse
        //this is because of how the MultiSelectView library stores the string
        List<String> myInterestList = new ArrayList<>();
        String getInterest = mCurrentUser.getUserInterest();
        if(getInterest == null){
            getInterest = "Not set";
        }else{
            String[] regex = getInterest.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            for(String t: regex){
                myInterestList.add(t);
            }
        }

        //orientationGroup = (RadioGroup)findViewById(R.id.orientation_group);

        //Get list of user interest
        List<String> stringList = new ArrayList<>();
        stringList.add(getResources().getStringArray(R.array.interest)[0]);

        multiSelectView.setTitle("Select an interest");
        multiSelectView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.interest)), myInterestList);



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mHandler = null;
    }

    //Gender selection
    public void selectMaleItem() {
        deselectFemaleItem();
        mMaleSelected = true;
        mMaleText.setTextColor(getResources().getColor(R.color.alizarin));
        mMaleImage.setImageResource(R.drawable.ic_male_alizarin_24_8);
        mMaleSelectionItemLayout.setSelected(true);
    }

    public void deselectMaleItem() {
        mMaleSelected = false;
        mMaleSelectionItemLayout.setSelected(false);
        mMaleText.setTextColor(getResources().getColor(R.color.text_color));
        mMaleImage.setImageResource(R.drawable.ic_male_dark_icon);
    }


    public void selectFemaleItem() {
        deselectMaleItem();
        mFemaleSelected = true;
        mFemaleText.setTextColor(getResources().getColor(R.color.alizarin));
        mFemaleImage.setImageResource(R.drawable.ic_female_alizarin_24_8);
        mFemaleSelectionItemLayout.setSelected(true);
    }

    public void deselectFemaleItem() {
        mFemaleSelectionItemLayout.setSelected(false);
        mFemaleSelected = false;
        mFemaleText.setTextColor(getResources().getColor(R.color.text_color));
        mFemaleImage.setImageResource(R.drawable.ic_female_dark_icon);
    }

    //Orientation selection
    public void selectedStraightItem(){
        deselectedGayItem();
        deSelctedBisexsualItem();
        deselectedNoanswerItem();
        mStraightSelected = true;
        mStraightTetxt.setTextColor(getResources().getColor(R.color.alizarin));
        mStriaghtImage.setImageResource(R.drawable.ic_straight);
        mStraightSelectionItemLayout.setSelected(true);
    }

    public void deSelectedStraightItem() {
        mStraightSelected = false;
        mStraightSelectionItemLayout.setSelected(false);
        mStraightTetxt.setTextColor(getResources().getColor(R.color.text_color));
        mStriaghtImage.setImageResource(R.drawable.ic_male_dark_icon);
    }

    public void selectedGayItem() {
        deSelectedStraightItem();
        deSelctedBisexsualItem();
        deselectedNoanswerItem();
        mGaySelected = true;
        mGayText.setTextColor(getResources().getColor(R.color.alizarin));
        mGayImage.setImageResource(R.drawable.ic_gay);
        mGaySelectionItemLayout.setSelected(true);
    }

    public void deselectedGayItem(){
        mGaySelectionItemLayout.setSelected(false);
        mGaySelected = false;
        mGayText.setTextColor(getResources().getColor(R.color.text_color));
        mGayImage.setImageResource(R.drawable.ic_gay);
    }

    public void selectedBisexsualItem(){
        deselectedNoanswerItem();
        deselectedGayItem();
        deSelectedStraightItem();
        mBisexsualSelected = true;
        mBisexsualText.setTextColor(getResources().getColor(R.color.alizarin));
        mBisexsualImage.setImageResource(R.drawable.ic_bi_sexsual);
        mBiSexsualSelectionLayout.setSelected(true);
    }

    public void deSelctedBisexsualItem(){
        mBiSexsualSelectionLayout.setSelected(false);
        mBisexsualSelected = false;
        mBisexsualText.setTextColor(getResources().getColor(R.color.text_color));
        mBisexsualImage.setImageResource(R.drawable.ic_bi_sexsual);
    }


    public void selectedNoanswerItem() {
        deselectedGayItem();
        deSelectedStraightItem();
        deSelctedBisexsualItem();
        mNoanswerSelcted = true;
        mNoanswerText.setTextColor(getResources().getColor(R.color.alizarin));
        mNoanswerImge.setImageResource(R.drawable.ic_no_answer);
        mNoanswerSelectionLayout.setSelected(true);
    }

    public void deselectedNoanswerItem(){
        mNoanswerSelectionLayout.setSelected(false);
        mNoanswerSelcted = false;
        mNoanswerText.setTextColor(getResources().getColor(R.color.text_color));
        mNoanswerImge.setImageResource(R.drawable.ic_no_answer);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (saveUserInfo != null)
            saveUserInfo.setOnClickListener(this);
    }

    private void initUi() {
        appBarLayout.addOnOffsetChangedListener(this);

        toolbarHeaderView.bindTo(mCurrentUser.getNickname(), "Last seen today at 7.00PM"); //this is not dynamic
        floatHeaderView.bindTo(mCurrentUser.getNickname(), "Last seen today at 7.00PM");
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
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.layout_male_select_item:
                if (mMaleSelectionItemLayout.isSelected()) deselectMaleItem();
                else selectMaleItem();
                break;
            case R.id.layout_female_select_item:
                if (mFemaleSelectionItemLayout.isSelected()) deselectFemaleItem();
                else selectFemaleItem();
                break;
            case R.id.layout_straight_select_item:
                if(mStraightSelectionItemLayout.isSelected())deSelectedStraightItem();
                else selectedStraightItem();
                break;
            case R.id.layout_gay_select_item:
                if(mGaySelectionItemLayout.isSelected())deselectedGayItem();
                else selectedGayItem();
                break;
            case R.id.layout_bi_select_item:
                if(mBiSexsualSelectionLayout.isSelected())deSelctedBisexsualItem();
                else selectedBisexsualItem();
                break;
            case R.id.layout_noanswer_select_item:
                if(mNoanswerSelectionLayout.isSelected())deselectedNoanswerItem();
                else selectedNoanswerItem();
                break;
            case R.id.save_btn:
                dialog = new ProgressDialog(EditProfileFragment.this);
                dialog.setMessage(getString(R.string.saving_info));

                userAge.clearFocus();
                editUsername.clearFocus();

                //Validate selected gender items
                if (mMaleSelected) mCurrentUser.setGenderIsMale(true);
                else mCurrentUser.setGenderIsMale(false);

                if(mStraightSelected)mCurrentUser.setOrientationStraight(true);
                else mCurrentUser.setOrientationStraight(false);

                if(mGaySelected)mCurrentUser.setOrientationGay(true);
                else mCurrentUser.setOrientationGay(false);

                if(mBisexsualSelected)mCurrentUser.setOrientationBisexsual(true);
                else mCurrentUser.setOrientationBisexsual(false);

                if(mNoanswerSelcted)mCurrentUser.setOrientationNoanswer(true);
                else mCurrentUser.setOrientationNoanswer(false);

                //Valid and convert user calendar age to readable age
                String ageString = userAge.getText().toString();
                try {
                    convertDate = sdf.parse(ageString); //parse the date of birth format as string (dd-mm-yyyy)
                    currentAge = calculatedAge(convertDate); //calculate the parsed format from calculated method
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

                Log.d("myapp", String.format("ageEditText = %d", Integer.valueOf(currentAge)));
                if (currentAge >= 18) {
                    mCurrentUser.setAge(currentAge);

                    //convert calender year/month
                   // mCurrentUser.setAge(Integer.parseInt(new ProjectAlphaClasses.PreferenceSettings().getAge(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))));
                }else{
                    Toast.makeText(EditProfileFragment.this, "You must be 18 or older!", Toast.LENGTH_LONG).show();
                    //return;
                }


                //Update name
                String nickname = editUsername.getText().toString();
                if (!TextUtils.isEmpty(editUsername.getText().toString())  && !TextUtils.equals(editUsername.getText().toString(), mCurrentUser.getNickname()) ) {
                    mCurrentUser.setNickname(editUsername.getText().toString());
                }

                //Update user Bio
                if(!TextUtils.isEmpty(userBio.getText().toString()) && !TextUtils.equals(userBio.getText().toString(), mCurrentUser.getUserBio())){
                    mCurrentUser.setUserBio(userBio.getText().toString());
                }

                //save currentSetLocation
                if (!TextUtils.isEmpty(userLocation.getText().toString())) {

                    mCurrentUser.setUserLoc(userLocation.getText().toString());
                }


                //Check for selected spinner item and save to parse
                if(spinner.isSelected()) //this check doesn't work
                {
                    mCurrentUser.setUserEthnicity(builder.toString());
                }

                //mCurrentUser.setUserEthnicity(builder.toString());

                //Save the selected widget item to parse
                mCurrentUser.setUserInterest(multiSelectView.getSelectedItems().toString());



                //saveUserInfo.setImageResource(R.drawable.load_animation);
                //saveUserInfo.setEnabled(false);

                // save in background ---throws an FC - call's back to MainActivity
                dialog.show();
                mCurrentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        dialog.dismiss();
                        if (e == null) {
                            saveUserInfo.setEnabled(true);
                            saveUserInfo.setOnClickListener(mMainActivity);
                            mMainActivity.updateInfo();
                        }
                    }
                });
                break;
            case R.id.cancel_btn:
                startActivity(new Intent(EditProfileFragment.this, MainActivity.class));
                finish();
        }

    }


    private void userEthnicity()
    {
        // create spinner list elements
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.add("African American");
        adapter.add("Asian");
        adapter.add("Caucasian/White");
        adapter.add("East Indian");
        adapter.add("Hispanic/Latino");
        adapter.add("Middle Eastern");
        adapter.add("Native American");
        adapter.add("Pacific Islander");
        adapter.add("Other");




        ethnicityView = (TextView)findViewById(R.id.user_ethnicity);
        spinner  = (MultiSpinner)findViewById(R.id.ethnicity_spinner);

        spinner.setAdapter(adapter, false, onSelectedListener);

        // set initial selection
        boolean[] selectedItems = new boolean[adapter.getCount()];
        selectedItems[0] = false; // select second item
        spinner.setSelected(selectedItems);

        ethnicityView.setText(mCurrentUser.getEthnicity());
    }

    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items

            builder = new StringBuilder();

            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    builder.append(adapter.getItem(i)).append(" ");
                    ethnicityView.setText(builder.toString());
                    //ethnicitySelected = builder.toString();
                    //System.out.println("ethnicity"+ ethnicitySelected);
                    Log.d("ethnicity", builder.toString());

                }
            }
            Toast.makeText(EditProfileFragment.this, builder.toString(), Toast.LENGTH_SHORT).show();
        }
    };


    private void setDatePickerListener(final EditText birthday) {
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showDatePicker((EditText) v);
            }
        });
    }

    private void showDatePicker(final EditText birthday) {
        DatePickerDialog dpd =
                new DatePickerDialog(EditProfileFragment.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Date selectedDate = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
                        birthday.setText(sdf.format(selectedDate));
                    }
                }, 2016, 0, 1);
        dpd.show();
    }

    private int calculatedAge(Date nowAge){
        Calendar age = Calendar.getInstance();
        age.setTime(nowAge); //Date of birth

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date()); //Now age

        return currentDate.get(Calendar.YEAR) - age.get(Calendar.YEAR);
    }


    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(EditProfileFragment.this);
        View promptView = layoutInflater.inflate(R.layout.user_location_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditProfileFragment.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.user_location_change);
        // setup a dialog window
        //editText.setText(mCurrentUser.getNickname());
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //resultText.setText("Hello, " + editText.getText());
                        //mCurrentUser = (User)parseUser;
                        //editText.setText(mCurrentUser.getUsername());
                        userLocation.setText(editText.getText());
                        //mCurrentUser.setNickname(editText.getText().toString());
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

    private void showPlacePickerDialog() {
        PlaceSearchDialog placeSearchDialog = new PlaceSearchDialog(this, new PlaceSearchDialog.LocationNameListener() {
            @Override
            public void locationName(String locationName) {
                userLocation.setText(locationName);
            }
        });
        placeSearchDialog.show();
    }

   /* public void userCurrentLocationClickHandler(View view) {
        showInputDialog();
    }*/

}
