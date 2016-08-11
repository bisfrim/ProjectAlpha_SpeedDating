package com.bizzy.projectalpha.speeddating.activities;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.transition.Explode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bizzy.projectalpha.speeddating.ConnectionDetect;
import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.models.Constant;
import com.bizzy.projectalpha.speeddating.models.User;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import android.view.View.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends AppCompatActivity implements OnClickListener,ConnectionCallbacks,OnConnectionFailedListener {
    @Bind(R.id.fab)
    FloatingActionButton fab;
    protected EditText usernameField, mPasswordField;
    protected TextInputLayout inputLayoutUsername, inputLayoutPassword;
    private RelativeLayout coordinatorLayout;
    private Boolean isInternetPresent = false;
    ConnectionDetect connectionDetect;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private Boolean parseLoginEmailAsUsername;
    private ConnectionResult mConnectionResult;
    private Button googleSignInBtn, facebookSignInBtn;

    private static final String dateFormat = "MM/dd/yyyy";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
    private Date convertDate;
    private int currentAge;

    private static final int PROFILE_PIC_SIZE = 150;

    ProgressDialog progressDialog;
    Snackbar snackbar;

    // Although Activity.isDestroyed() is in API 17, we implement it anyways for older versions.
    private boolean destroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        coordinatorLayout = (RelativeLayout) findViewById(R.id.layout_login);

        Log.d("myapp", "onCreate");

        //Create a connection instance
        connectionDetect = new ConnectionDetect(getApplicationContext());

        //username, password field
        googleSignInBtn = (Button) findViewById(R.id.google_btn);
        facebookSignInBtn = (Button)findViewById(R.id.facebook_btn);
        inputLayoutUsername = (TextInputLayout) findViewById(R.id.input_layout_username);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        usernameField = (EditText) findViewById(R.id.username_field);
        mPasswordField = (EditText) findViewById(R.id.password_field);

        TextView tv = (TextView) findViewById(R.id.forgot_password_click);
        tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            googleSignInBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.google_plus_vector, 0, 0, 0);
        }else{
            googleSignInBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_google_plus_white_36dp, 0, 0, 0);

        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            facebookSignInBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.facebook_vector, 0, 0, 0);
        } else {
            facebookSignInBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_facebook_white_36dp, 0, 0, 0);
        }

        googleSignInBtn.setOnClickListener(this);
        facebookSignInBtn.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this) //lets impement ConnectionCallbacks
                .addOnConnectionFailedListener(this).addApi(Plus.API) // lets implement OnConnectionFailedListener
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyed = true;
    }

    public void signUpClickHandler(View view) {
        Intent signUpIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        LoginActivity.this.startActivityForResult(signUpIntent, Constant.REQ_CODE_SIGN_UP);

    }

    public boolean isParseLoginEmailAsUsername() {
        if (parseLoginEmailAsUsername != null) {
            return parseLoginEmailAsUsername;
        } else {
            return false;
        }
    }

    public void signinClickHandler(View view) {
        boolean validationError = false;

        isInternetPresent = connectionDetect.isConnectingToInternet();
        if (isParseLoginEmailAsUsername()) {
            usernameField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }

        final String username = usernameField.getText().toString().trim();
        final String password = mPasswordField.getText().toString().trim();

        mPasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean isValidKey = event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
                boolean isValidAction = actionId == EditorInfo.IME_ACTION_DONE;

                if (isValidKey || isValidAction) {
                    // do login request
                }
                return false;
            }
        });

        //check for input validation here
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (username.length() == 0) {
            if(isParseLoginEmailAsUsername()){
                validationError = true;
                validationErrorMessage.append(getString(R.string.error_blank_username));

            }
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));

            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            //Toast.makeText(LoginActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
            showAlertDialog(LoginActivity.this, "Opps!", validationErrorMessage.toString(), false);
            return;
        }


        //Check internet connection, show snackbar error if no connection
        if (!isInternetPresent) {
            //showAlertDialog(LoginActivity.this, "No network connection!", "Check your internet connection", false);
            snackbar = Snackbar.make(coordinatorLayout, "No network connection", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();

        } else {

            // Set up a progress dialog
            final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage(getString(R.string.progress_login));
            dialog.show();

            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    dialog.dismiss();
                    if (parseUser != null) {
                        Log.d("OnClick", "User");
                        Intent mainIntent = new Intent(LoginActivity.this, UserDispatchActivity.class);
                        ParseInstallation.getCurrentInstallation().put("user", parseUser);
                        ParseInstallation.getCurrentInstallation().saveInBackground();
                        ((User) parseUser).setInstallation(ParseInstallation.getCurrentInstallation());
                        parseUser.saveInBackground();
                        startActivity(mainIntent);
                        finish();

                        // login with email
                    }else if(username.contains("@")){
                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereEqualTo("email", username);
                        query.getFirstInBackground(new GetCallback<ParseUser>() {
                            @Override
                            public void done(ParseUser object, ParseException e) {
                                if(object != null){
                                    ParseUser.logInInBackground(object.getString("username"), password, new LogInCallback() {
                                        @Override
                                        public void done(ParseUser parseUser, ParseException e) {
                                            //dialog.dismiss();
                                            if(parseUser != null){
                                                Log.d("OnClick", "User");

                                                Explode explode = new Explode();
                                                explode.setDuration(500);
                                                getWindow().setExitTransition(explode);
                                                getWindow().setEnterTransition(explode);
                                                ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this);

                                                Intent mainIntent = new Intent(LoginActivity.this, UserDispatchActivity.class);
                                                ParseInstallation.getCurrentInstallation().put("user", parseUser);
                                                ParseInstallation.getCurrentInstallation().saveInBackground();
                                                ((User) parseUser).setInstallation(ParseInstallation.getCurrentInstallation());
                                                parseUser.saveInBackground();
                                                startActivity(mainIntent,oc2.toBundle());
                                                finish();
                                            }

                                        }
                                    });
                                }
                            }
                        });

                    }
                    else {
                        switch (e.getCode()) {
                            case ParseException.OBJECT_NOT_FOUND:
                                showAlertDialog(LoginActivity.this, "Opps!", e.getMessage(), false);
                                break;
                            default:
                                Toast.makeText(LoginActivity.this
                                        , "Login failed! Try again", Toast.LENGTH_LONG).show();
                        }
                        Log.d("OnClick", e.toString());
                    }
                }
            });

        }

    }

    //Facebook login here
    public void facebookActivity() {
        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage(getString(R.string.progress_login));
        dialog.show();

        //Get list of facebook permissions
        final List<String> permissions = Arrays.asList("email", "public_profile", "user_birthday");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(final ParseUser parseUser, ParseException err) {
                dialog.dismiss();
                final User user = (User) parseUser;
                if (user == null && err != null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login." + err.toString());
                    //Toast.makeText(getApplicationContext(), "Uh oh. The user cancelled the Facebook login.",
                    // Toast.LENGTH_SHORT).show();
                } else if (user.isNew()) {
                    ParseInstallation.getCurrentInstallation().put("user", user);
                    ParseInstallation.getCurrentInstallation().saveInBackground();
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                    //Toast.makeText(getApplicationContext(), "User signed up and logged in through Facebook.",
                    //      Toast.LENGTH_SHORT).show();

                    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                            Log.d("MyApp", "Complete graph request");
                            if (jsonObject == null)
                                Log.d("MyApp", "jsonObject is null");
                            else
                                Log.d("MyApp", jsonObject.toString());
                            final String email = graphResponse.getJSONObject().optString("email");
                            final String nickname = graphResponse.getJSONObject().optString("first_name") + " " + graphResponse.getJSONObject().optString("last_name");
                            final String birthday = graphResponse.getJSONObject().optString("birthday");
                            final String photoProfile = graphResponse.getJSONObject().optJSONObject("picture").optJSONObject("data").optString("url");
                            //user.setNoVip();
                            user.setNickname(nickname);
                            user.setInstallation(ParseInstallation.getCurrentInstallation());
                            user.setEmail(email);

                            try {
                                convertDate = sdf.parse(birthday); //parse the date of birth format as string (dd-mm-yyyy)
                                currentAge = calculatedAge(convertDate); //calculate the parsed format from calculated method
                            } catch (java.text.ParseException e) {
                                e.printStackTrace();
                            }
                            user.setAge(currentAge);

                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        switch (e.getCode()) {
                                            case ParseException.EMAIL_TAKEN:
                                                Log.d("MyApp", "email taken");
                                                break;
                                            default:
                                                Log.d("MyApp", e.toString());
                                                break;
                                        }
                                    }
                                }
                            });
                        }

                    });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,email,gender,birthday,first_name,last_name");
                    request.setParameters(parameters);
                    request.executeAsync();

                    Intent mainIntent = new Intent(LoginActivity.this, UserDispatchActivity.class);
                    mainIntent.putExtra(Constant.ARG_AUTH_METHOD, Constant.AUTH_FACEBOOK);
                    startActivity(mainIntent);
                    LoginActivity.this.finish();


                } else {
                    Log.d("MyApp", "User logged in through Facebook!");
                    //Toast.makeText(getApplicationContext(), "User logged in through Facebook!",
                    //      Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(LoginActivity.this, UserDispatchActivity.class);
                    ParseInstallation.getCurrentInstallation().put("user", user);
                    ParseInstallation.getCurrentInstallation().saveInBackground();
                    user.setInstallation(ParseInstallation.getCurrentInstallation());
                    user.saveInBackground();
                    mainIntent.putExtra(Constant.ARG_AUTH_METHOD, Constant.AUTH_FACEBOOK);
                    startActivity(mainIntent);
                    LoginActivity.this.finish();
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Constant.RES_CODE_SIGN_UP_SUCCESS: {
                Intent mainIntent = new Intent(LoginActivity.this, UserDispatchActivity.class);
                startActivity(mainIntent);
            }
        }
    }


    //Alert dialog box: success / failed
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



    @Override
    @OnClick({R.id.fab})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_btn:
                // Signin button clicked
                loginUsingGoolgePlus();
                break;

            case R.id.fab:
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
                    startActivity(new Intent(this, RegisterActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(this, RegisterActivity.class));
                }
                break;
        }
    }


    private void loginUsingGoolgePlus() {
        // TODO Auto-generated method stub
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // TODO Auto-generated method stub
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        getProfileInformation();

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // TODO Auto-generated method stub
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    /**
     * Method to resolve any signin errors for google plus
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, Constant.RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }


    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation() {
        final User newUser = new User();
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                final String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e("myapp", "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                //personPhotoUrl = personPhotoUrl.substring(0,
                //      personPhotoUrl.length() - 2)
                //    + PROFILE_PIC_SIZE;


                newUser.setNickname(personName);
                newUser.setEmail(email);
                newUser.setInstallation(ParseInstallation.getCurrentInstallation());
                newUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null){
                            Toast.makeText(LoginActivity.this, "user in parse", Toast.LENGTH_SHORT).show();
                        }else{
                            Log.d("LoginException", e.toString());
                        }
                    }
                });

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        googleAuthWithParse(email);
                    }
                }).start();
            } else {
                Toast.makeText(this,
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void googleAuthWithParse(String email) {
        // TODO Auto-generated method stub
        String scopes = Constant.ARG_GOOGLE_AUTH + Scopes.PLUS_LOGIN + " ";
        String googleAuthCode = null;
        try {
            googleAuthCode = GoogleAuthUtil.getToken(
                    this,                                           // Context context
                    email,                                             // String email
                    scopes,                                            // String scope
                    null                                      // Bundle bundle
            );
        } catch (UserRecoverableAuthException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (GoogleAuthException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        //Log.i(TAG, "Authentication Code: " + googleAuthCode);

        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("code", googleAuthCode);
        params.put("email", email);
        //loads the Cloud function to create a Google user
        ParseCloud.callFunctionInBackground("accessGoogleUser", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object returnObj, ParseException e) {
                if (e == null) {
                    Log.e("AccessToken", returnObj.toString());
                    ParseUser.becomeInBackground(returnObj.toString(), new LogInCallback() {
                        public void done(final ParseUser parseUser, ParseException e) {
                            final User user = (User)parseUser;
                            if (user != null && e == null) {
                                Toast.makeText(LoginActivity.this, "The Google user validated", Toast.LENGTH_SHORT).show();

                                if(user.isNew()){
                                    //isNew means firsttime
                                    ParseInstallation.getCurrentInstallation().put("user", user);
                                    ParseInstallation.getCurrentInstallation().saveInBackground();
                                    Log.d("MyApp", "User signed up and logged in through Google!");
                                    //((User) user).setInstallation(ParseInstallation.getCurrentInstallation().getCurrentInstallation());
                                    //user.saveInBackground();

                                }else{
                                    Intent mainIntent = new Intent(LoginActivity.this, UserDispatchActivity.class);
                                    ParseInstallation.getCurrentInstallation().put("user", user);
                                    ParseInstallation.getCurrentInstallation().saveInBackground();
                                    user.setInstallation(ParseInstallation.getCurrentInstallation());
                                    user.saveInBackground();
                                    mainIntent.putExtra(Constant.ARG_GOOGLE_AUTH, Constant.RC_SIGN_IN);
                                    startActivity(mainIntent);
                                    LoginActivity.this.finish();

                                    //loginSuccess();
                                }
                            } else if (e != null) {
                                Toast.makeText(LoginActivity.this, "There was a problem creating your account.", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                                mGoogleApiClient.disconnect();
                            } else
                                Toast.makeText(LoginActivity.this, "The Google token could not be validated", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    if (e != null) {

                        try {
                            JSONObject jsonObject = new JSONObject(e.getMessage());
                            Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                        mGoogleApiClient.disconnect();
                    }
                }
            }
        });
    }

    private int calculatedAge(Date nowAge){
        Calendar age = Calendar.getInstance();
        age.setTime(nowAge); //Date of birth

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date()); //Now age

        return currentDate.get(Calendar.YEAR) - age.get(Calendar.YEAR);
    }




}
