package com.bizzy.projectalpha.speeddating.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.activities.ConnectionsActivity;
import com.bizzy.projectalpha.speeddating.activities.MainActivity;
import com.bizzy.projectalpha.speeddating.activities.PeopleNearMeActivity;
import com.bizzy.projectalpha.speeddating.activities.UsersProfileActivity;
import com.bizzy.projectalpha.speeddating.models.Constants;
import com.bizzy.projectalpha.speeddating.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class ConnectionsTabFragment extends ListFragment {
    private static final String TEXT_FRAGMENT = "TEXT_FRAGMENT";
    private static final String LOG_TAG = "MyPhotoTabFragment";
    public static final String TAG = "ContactFragment";

    protected List<ParseUser> mFriendsList;
    protected ListView mListView;
    protected TextView mContactEmpty;
    protected Button mButtonConnect;
    protected LinearLayout mLoading_ProgressBar;
    protected ParseRelation<ParseUser> mContactRelation;
    protected ParseUser mCurrentUser;

    public ConnectionsTabFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connections_tab, container, false);
        mListView = (ListView) v.findViewById(android.R.id.list);
        mContactEmpty = (TextView) v.findViewById(R.id.contactEmpty);
        mButtonConnect = (Button)v.findViewById(R.id.button_find_contact);
        mLoading_ProgressBar = (LinearLayout) v.findViewById(R.id.loadingProgressBar);
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();


        mLoading_ProgressBar.setVisibility(View.VISIBLE);

        mCurrentUser = ParseUser.getCurrentUser();
        mContactRelation = mCurrentUser.getRelation(Constants.KEY_CONTACT_RELATION);

        mButtonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoContacts = new Intent(getContext(), PeopleNearMeActivity.class);
                gotoContacts.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(gotoContacts);
            }
        });

        ParseQuery<ParseUser> query = mContactRelation.getQuery();
        query.orderByAscending(User.KEY_USERNAME);
        query.setLimit(Constants.KEY_LIMIT_CONTACT);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(final List<ParseUser> friends, ParseException e) {
                mLoading_ProgressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    //Success
                    mFriendsList = friends;

                    //Loading only Username
                    String[] usernames = new String[mFriendsList.size()]; //Set size as same as mUserList from Parse
                    int i = 0;
                    for (ParseUser user : mFriendsList) {
                        usernames[i] = user.getUsername();
                        i++;
                    }

                    //Remove No Contact label
                    if (i > 0) {
                        mContactEmpty.setVisibility(View.INVISIBLE);
                        mButtonConnect.setVisibility(View.INVISIBLE);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            //Set Context using ListView
                            getListView().getContext(),
                            android.R.layout.simple_expandable_list_item_1,
                            usernames);

                    setListAdapter(adapter);

                    mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            String itemSelected = mListView.getItemAtPosition(position).toString();
                            //Toast.makeText(getContext(), itemSelected, Toast.LENGTH_LONG).show();
                            if(itemSelected !=  null){
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                                alertDialog.setTitle("Confirm Delete...");

                                // Setting Dialog Message
                                alertDialog.setMessage("Remove: " + itemSelected + " from your connections?");

                                // Setting Icon to Dialog
                                //alertDialog.setIcon(R.drawable.delete);

                                // Setting Positive "Yes" Button
                                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int which) {
                                        //Remove from Contact list
                                        mContactRelation.remove(mFriendsList.get(position));
                                        mCurrentUser.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                Toast.makeText(getContext(), "Removed", Toast.LENGTH_SHORT).show();
                                                updateStudentsList(friends);
                                            }
                                        });
                                    }
                                });

                                // Setting Negative "NO" Button
                                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Write your code here to invoke NO event
                                        //Toast.makeText(getContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                });

                                // Showing Alert Message
                                alertDialog.show();

                            } else {
                                Toast.makeText(getContext(), R.string.error_label, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else {
                    //Failed - Error
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder error = new AlertDialog.Builder(getActivity());
                    error.setMessage(R.string.error_loading_backend)
                            .setTitle(R.string.error_label)
                            .setNeutralButton(android.R.string.ok, null);

                    AlertDialog dialog = error.create();

                    dialog.show();

                }

            }
        });

    }


    public void updateStudentsList(List<ParseUser> newlist){
        mFriendsList.clear();
        mFriendsList = newlist;
        mListView.invalidateViews();
        mListView.refreshDrawableState();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int pos = parent.getPositionForView(view);
        //Toast.makeText(this, "Feeds", Toast.LENGTH_SHORT).show();
    }


}
