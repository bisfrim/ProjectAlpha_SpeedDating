package com.bizzy.projectalpha.speeddating.models;

/**
 * Created by bismark.frimpong on 12/8/2015.
 */
public class Constant {

    //Identifer
    public final static int ID_PROFILE_DRAWER_ITEM = 0;

    //Request code
    public final static int REQ_CODE_LOGIN = 0;
    public final static int REQ_CODE_SIGN_UP = 1;
    public final static int FILE_CODE = 1;
    public final static int MSG_NEW_CLIENT = 2;

    //Result code
    public final static int RES_CODE_LOGIN_SUCCESS = 1;
    public final static int RES_CODE_SIGN_UP_SUCCESS = 2;
    public final static int RES_CODE_SIGN_UP_FAILURE = 3;
    public final static int RES_CODE_LOGIN_FAILURE = 4;

    //Drawer item code
    public final static int DRAWER_ID_PROFILE = 0;
    public final static int DRAWER_ID_LOGOUT = 1;
    public final static int DRAWER_ID_SETTINGS = 2;
    public final static int DRAWER_ID_MESSAGES = 3;
    public final static int DRAWER_ID_START_MATCH = 4;
    public final static int DRAWER_ID_PEOPLE_NEAR_ME = 5;

    public final static String ARG_AUTH_METHOD = "auth_method";
    public static final String ARG_GOOGLE_AUTH = "oauth2:";
    public final static int AUTH_FACEBOOK = 0;
    public static final int RC_SIGN_IN = 0;

}
