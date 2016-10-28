package com.bizzy.projectalpha.speeddating.models;

import android.content.Context;

import com.bizzy.projectalpha.speeddating.ActionDataSource;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nubxf5 on 10/4/2016.
 */

public class UserDataModel {
    private static final String TAG = "User Data Model";


    private Context mContext;

    UserDataModel(Context context){
        mContext = context;
    }


    public static void getUnseenUsers(final UserDataCallbacks callback){
        ParseQuery<ParseObject> seenUsersQuery = new ParseQuery<ParseObject>(ActionDataSource.TABLE_NAME);
        seenUsersQuery.whereEqualTo(ActionDataSource.COLUMN_BY_USER, ParseUser.getCurrentUser().getObjectId());
        seenUsersQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null){
                    List<String> ids = new ArrayList<String>();
                    for (ParseObject parseObject : list){
                        ids.add(parseObject.getString(ActionDataSource.COLUMN_TO_USER));
                    }
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereNotEqualTo("objectId", User.getCurrentUser().getObjectId());
                    query.whereNotContainedIn("objectId", ids);
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> list, ParseException e) {
                            formatCallback(list, e, callback);
                        }
                    });
                }
            }
        });
    }



    private static void formatCallback(List<ParseUser> list, ParseException e, UserDataCallbacks callback) {
        if (e == null){
            List<User> users = new ArrayList<User>();
            for (ParseUser parseUser : list){
                User user = parseUserToUser(parseUser);
                users.add(user);
            }
            if (callback != null){
                callback.onUsersFetched(users);
            }
        }
    }

    public static void getUsersIn(List<String> ids, final UserDataCallbacks callbacks){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn(User.USER_ID,ids);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                formatCallback(list, e, callbacks);
            }
        });
    }

    private static User parseUserToUser(ParseUser parseUser){
        //User user = new User();
        parseUser.getObjectId();
        parseUser.getString(User.COL_NICKNAME);
        parseUser.getString(User.USER_PHOTO);
        parseUser.getUsername();
        return (User) parseUser;
    }



    public interface UserDataCallbacks{
        void onUsersFetched(List<User> users);
    }


}
