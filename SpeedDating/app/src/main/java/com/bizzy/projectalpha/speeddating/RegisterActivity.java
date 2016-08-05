package com.bizzy.projectalpha.speeddating;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.phajduk.rxvalidator.RxValidationResult;
import com.github.phajduk.rxvalidator.RxValidator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.yayandroid.locationmanager.LocationBaseActivity;
import com.yayandroid.locationmanager.LocationConfiguration;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.constants.FailType;
import com.yayandroid.locationmanager.constants.LogType;
import com.yayandroid.locationmanager.constants.ProviderType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class RegisterActivity extends LocationBaseActivity {
    RelativeLayout mSignUpActivity;
    Intent mLoginIntent;

    private Toolbar mToolbar;
    private ParseGeoPoint mParsegeoPoint;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private ProgressDialog locProgressDialog;

    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    private boolean mMaleSelected,mFemaleSelected = false;

    //EditText
    protected EditText registerNickname, registerUser, registerEmail,
            registerPass, register_re_pass,register_Loc;

    //TextView
    private EditText dateView;

    //Local fields
    private Calendar calendar;
    private int currentAge;


    private static final String TAG = "RxValidator";
    private static final String dateFormat = "dd-MM-yyyy";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
    private Date convertDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        mSignUpActivity = (RelativeLayout) findViewById(R.id.activity_register);

        mLoginIntent = this.getIntent();

        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);

        registerNickname = (EditText) findViewById(R.id.signup_nickname);
        registerUser = (EditText) findViewById(R.id.signup_username);
        registerEmail = (EditText) findViewById(R.id.signup_email);
        registerPass = (EditText) findViewById(R.id.signup_password);
        register_re_pass = (EditText) findViewById(R.id.signup_re_password);

        register_Loc = (EditText)findViewById(R.id.register_location);

        LocationManager.setLogType(LogType.GENERAL);
        getLocation();

        //Age Calendar
        dateView = (EditText) findViewById(R.id.signup_birthday);
        //locationZip = (EditText)findViewById(R.id.signup_location);

        setDatePickerListener(dateView);

        doValidation();



        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mToolbar.setClickable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_material, null));
        }
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle("Register");

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        dismissProgress();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getLocationManager().isWaitingForLocation()
                && !getLocationManager().isAnyDialogShowing()) {
            displayProgress();
        }
    }

    public void goSigninClickHandler(View view) {
        Intent signUpIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(signUpIntent);
        RegisterActivity.this.finish();
    }


    public void registerAccountClickHandler(View view) {
        int selectedId = radioSexGroup.getCheckedRadioButtonId(); // get selected radio button from radioGroup
        final String nickname = registerNickname.getText().toString().trim();
        final String username = registerUser.getText().toString().trim();
        final String email = registerEmail.getText().toString().trim();
        final String password = registerPass.getText().toString().trim();
        final String re_password = register_re_pass.getText().toString().trim();
        final String birthday = dateView.getText().toString().trim();
        final String userLocation = register_Loc.getText().toString().trim();
        //final String userLocation = locationZip.getText().toString().trim();

        radioSexButton = (RadioButton) findViewById(selectedId);// find the radiobutton by returned id

        try {
            convertDate = sdf.parse(birthday); //parse the date of birth format as string (dd-mm-yyyy)
            currentAge = calculatedAge(convertDate); //calculate the parsed format from calculated method
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        if (radioSexGroup.getCheckedRadioButtonId() == -1){
            Toast.makeText(RegisterActivity.this, "Opps, Please choose a gender", Toast.LENGTH_SHORT).show();
            return;
        }

/*
        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));

        if(nickname.length() == 0){
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_name));
        }

        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }


        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        if (!password.equals(re_password)) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_invalid_email));
        }

        if(birthday.length() == 0 ){
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_age));
        }else if (Integer.valueOf(birthday) < 18){
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_check_age));
        }

        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            showAlertDialog(RegisterActivity.this, "Opps!", validationErrorMessage.toString(), false);
            //Toast.makeText(RegisterActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();

            return;
        }*/




        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        Glide.with(RegisterActivity.this)
                .load(R.drawable.heart_icon)
                .asBitmap()
                .toBytes()
                .into(new SimpleTarget<byte[]>() {
                    @Override
                    public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                        final ParseFile image = new ParseFile(resource);
                        image.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    User newUser = new User();
                                    //LocationZipCode myLocationZip = new LocationZipCode();
                                    newUser.setEmail(email);
                                    newUser.setNickname(nickname);
                                    newUser.setUsername(username);
                                    newUser.setPassword(password);
                                    newUser.setAge(currentAge);
                                    //newUser.setUserLoc(userLocation);

                                    if (radioSexButton.getId() == R.id.radioMale) {
                                        newUser.setGenderIsMale(true);

                                        Log.d("RegisterActivity", radioSexButton.getText().toString());
                                    } else if(radioSexButton.getId() == R.id.radioFemale) {
                                        newUser.setGenderIsMale(false);
                                        Log.d("RegisterActivity", radioSexButton.getText().toString());
                                    }

                                    newUser.setGeoPoint(mParsegeoPoint);

                                    //myLocationZip.getAddress(userLocation);
                                    newUser.setInstallation(ParseInstallation.getCurrentInstallation());
                                    newUser.setProfilePhotoThumb(image);
                                    newUser.setProfilePhoto(image);
                                    newUser.signUpInBackground(new SignUpCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            dialog.dismiss();
                                            if (e == null) {
                                                //Log.d("SignUpException", e.toString());
                                                //RegisterActivity.this.setResult(Constant.RES_CODE_SIGN_UP_SUCCESS, mLoginIntent);
                                                startActivity(new Intent(RegisterActivity.this, UserDispatchActivity.class));
                                                finish();
                                            } else {
                                                Log.d("SignUpException", e.toString());
                                                switch (e.getCode()) {
                                                    case ParseException.USERNAME_TAKEN:
                                                        Toast.makeText(RegisterActivity.this
                                                                , "Sorry, this username has already been taken", Toast.LENGTH_LONG).show();
                                                        break;
                                                    case ParseException.ACCOUNT_ALREADY_LINKED:
                                                        Toast.makeText(RegisterActivity.this, "Sorry this account is already registered", Toast.LENGTH_LONG).show();
                                                        break;
                                                    case ParseException.INVALID_EMAIL_ADDRESS:
                                                        Toast.makeText(RegisterActivity.this, "Sorry email is invalid, Please correct it and try again", Toast.LENGTH_LONG).show();
                                                        break;
                                                    case ParseException.OBJECT_NOT_FOUND:
                                                        Toast.makeText(RegisterActivity.this
                                                                , "Sorry, those credentials were invalid", Toast.LENGTH_LONG).show();
                                                        break;
                                                    default:
                                                        Toast.makeText(RegisterActivity.this
                                                                , e.getMessage(), Toast.LENGTH_LONG).show();
                                                        break;

                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
    }

    protected void debugLog(String text) {
        if (Parse.getLogLevel() <= Parse.LOG_LEVEL_DEBUG &&
                Log.isLoggable(getLogTag(), Log.WARN)) {
            Log.w(getLogTag(), text);
        }
    }

    protected String getLogTag() {
        return null;
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.ic_success : R.drawable.ic_failed);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }




    private void setDatePickerListener(final EditText birthday) {
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showDatePicker((EditText) v);
            }
        });
    }

    private void showDatePicker(final EditText birthday) {
        DatePickerDialog dpd =
                new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
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

    private void doValidation(){
        RxValidator.createFor(registerNickname)
                .nonEmpty()
                .onValueChanged()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RxValidationResult<EditText>>() {
                    @Override public void call(RxValidationResult<EditText> result) {
                        result.getItem().setError(result.isProper() ? null : result.getMessage());
                        Log.i(TAG, "Validation result " + result.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        Log.e(TAG, "Validation error", throwable);
                    }
                });

        RxValidator.createFor(registerUser)
                .nonEmpty()
                .onValueChanged()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RxValidationResult<EditText>>() {
                    @Override public void call(RxValidationResult<EditText> result) {
                        result.getItem().setError(result.isProper() ? null : result.getMessage());
                        Log.i(TAG, "Validation result " + result.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        Log.e(TAG, "Validation error", throwable);
                    }
                });

        RxValidator.createFor(registerEmail)
                .nonEmpty()
                .email()
                .onValueChanged()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RxValidationResult<EditText>>() {
                    @Override public void call(RxValidationResult<EditText> result) {
                        result.getItem().setError(result.isProper() ? null : result.getMessage());
                        Log.i(TAG, "Validation result " + result.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        Log.e(TAG, "Validation error", throwable);
                    }
                });

        RxValidator.createFor(registerPass)
                .nonEmpty()
                .minLength(5, "Min length is 5")
                .onFocusChanged()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RxValidationResult<EditText>>() {
                    @Override public void call(RxValidationResult<EditText> result) {
                        result.getItem().setError(result.isProper() ? null : result.getMessage());
                        Log.i(TAG, "Validation result " + result.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        Log.e(TAG, "Validation error", throwable);
                    }
                });

        RxValidator.createFor(register_re_pass)
                .sameAs(registerPass, "Password does not match")
                .onFocusChanged()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RxValidationResult<EditText>>() {
                    @Override public void call(RxValidationResult<EditText> result) {
                        result.getItem().setError(result.isProper() ? null : result.getMessage());
                        Log.i(TAG, "Validation result " + result.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        Log.e(TAG, "Validation error", throwable);
                    }
                });


        RxValidator.createFor(dateView)
                .age("You have to be 18y old", 18, sdf)
                .onValueChanged()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RxValidationResult<EditText>>() {
                    @Override public void call(RxValidationResult<EditText> result) {
                        result.getItem().setError(result.isProper() ? null : result.getMessage());
                        Log.i(TAG, "Validation result " + result.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        Log.e(TAG, "Validation error", throwable);
                    }
                });


    }





    @Override
    public LocationConfiguration getLocationConfiguration() {
        return new LocationConfiguration()
                .keepTracking(true)
                .askForGooglePlayServices(true)
                .setMinAccuracy(200.0f)
                .setWaitPeriod(ProviderType.GOOGLE_PLAY_SERVICES, 5 * 1000)
                .setWaitPeriod(ProviderType.GPS, 10 * 1000)
                .setWaitPeriod(ProviderType.NETWORK, 5 * 1000)
                .setGPSMessage("Would you mind to turn GPS on?")
                .setRationalMessage("We need permission for GPS?, If you reject permission, you cannot use this service\n\n" +
                        "Please turn on permissions at [Setting] > [Permission]");
    }

    @Override
    public void onLocationFailed(int failType) {
        dismissProgress();

        switch (failType) {
            case FailType.PERMISSION_DENIED: {
                register_Loc.setText("Couldn't get location, because user didn't give permission!");
                break;
            }
            case FailType.GP_SERVICES_NOT_AVAILABLE:
            case FailType.GP_SERVICES_CONNECTION_FAIL: {
                register_Loc.setText("Couldn't get location, because Google Play Services not available!");
                break;
            }
            case FailType.NETWORK_NOT_AVAILABLE: {
                register_Loc.setText("Couldn't get location, because network is not accessible!");
                break;
            }
            case FailType.TIMEOUT: {
                register_Loc.setText("Couldn't get location, and timeout!");
                break;
            }
            case FailType.GP_SERVICES_SETTINGS_DENIED: {
                register_Loc.setText("Couldn't get location, because user didn't activate providers via settingsApi!");
                break;
            }
            case FailType.GP_SERVICES_SETTINGS_DIALOG: {
                register_Loc.setText("Couldn't display settingsApi dialog!");
                break;
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        dismissProgress();
        setLocationText(location);
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


    public void setLocationText(Location location) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            //String cityName = addresses.get(0).getAddressLine(0);
            String stateName = addresses.get(0).getAddressLine(1);
            //String countryName = addresses.get(0).getAddressLine(2);
            String zipCode = addresses.get(0).getPostalCode();

            mParsegeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

            String appendValue = location.getLatitude() + ", " + location.getLongitude() + "\n";

            String newValue;
            CharSequence current = register_Loc.getText();

            if (!TextUtils.isEmpty(current)) {
                register_Loc.getText();
            } else {
                register_Loc.setText(zipCode);
            }




        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}