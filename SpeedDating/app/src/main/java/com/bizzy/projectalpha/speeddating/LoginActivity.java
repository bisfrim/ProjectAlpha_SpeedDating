package com.bizzy.projectalpha.speeddating;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import android.view.View.OnClickListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class LoginActivity extends AppCompatActivity implements OnClickListener {
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

    ProgressDialog progressDialog;
    Snackbar snackbar;

    // Although Activity.isDestroyed() is in API 17, we implement it anyways for older versions.
    private boolean destroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .addScope(new Scope(Scopes.EMAIL))
                .build();*/

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
                                                Intent mainIntent = new Intent(LoginActivity.this, UserDispatchActivity.class);
                                                ParseInstallation.getCurrentInstallation().put("user", parseUser);
                                                ParseInstallation.getCurrentInstallation().saveInBackground();
                                                ((User) parseUser).setInstallation(ParseInstallation.getCurrentInstallation());
                                                parseUser.saveInBackground();
                                                startActivity(mainIntent);
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
        List<String> permissions = Arrays.asList("email", "public_profile", "user_birthday");

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
                            //user.setNoVip();
                            user.setNickname(nickname);
                            user.setInstallation(ParseInstallation.getCurrentInstallation());
                            user.setEmail(email);
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
                    parameters.putString("fields", "id,email,gender,first_name,last_name");
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
    public void onClick(View v) {

        //code here

    }

}
