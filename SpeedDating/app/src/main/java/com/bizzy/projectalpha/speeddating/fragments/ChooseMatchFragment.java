package com.bizzy.projectalpha.speeddating.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bizzy.projectalpha.speeddating.ActionDataSource;
import com.bizzy.projectalpha.speeddating.CardStackContainer;
import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.WaitForInternetConnectionView;
import com.bizzy.projectalpha.speeddating.adapter.CardAdapter;
import com.bizzy.projectalpha.speeddating.models.User;
import com.bizzy.projectalpha.speeddating.models.UserDataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChooseMatchFragment extends Fragment implements
        UserDataModel.UserDataCallbacks, CardStackContainer.SwipeCallbacks {


    private static final String TAG = "ChooseMatchFragment";
    private WaitForInternetConnectionView mWaitForInternetConnectionView;
    private CardStackContainer mCardStack;
    private List<User> mUsers;
    private CardAdapter mCardAdapter;


    public ChooseMatchFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_choose_match, container, false);
        mCardStack = (CardStackContainer)v.findViewById(R.id.card_stack);
        mWaitForInternetConnectionView = (WaitForInternetConnectionView) v.findViewById(R.id.wait_for_internet_connection);

        mWaitForInternetConnectionView.checkInternetConnection(new WaitForInternetConnectionView.OnConnectionIsAvailableListener() {
            @Override
            public void onConnectionIsAvailable() {
                UserDataModel.getUnseenUsers(ChooseMatchFragment.this);
                mWaitForInternetConnectionView.close();
            }
        });

        mUsers = new ArrayList<>();
        mCardAdapter = new CardAdapter(getActivity(), mUsers);
        mCardStack.setAdapter(mCardAdapter);
        mCardStack.setSwipeCallbacks(this);
        ImageView nahButton = (ImageView) v.findViewById(R.id.nah_button);
        nahButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardStack.swipeLeft();
            }
        });
        ImageView yeahButton = (ImageView)v.findViewById(R.id.yeah_button);
        yeahButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardStack.swipeRight();
            }
        });

        return v;
    }


    @Override
    public void onUsersFetched(List<User> users) {
        mUsers.addAll(users);
        mCardAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSwipeLeft(User user) {
        ActionDataSource.saveUserSkipped(user.getObjectId());
    }

    @Override
    public void onSwipeRight(User user) {
        ActionDataSource.saveUserLiked(user.getObjectId());
    }

}
