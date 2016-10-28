package com.bizzy.projectalpha.speeddating.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bizzy.projectalpha.speeddating.ActionDataSource;
import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.models.User;
import com.bizzy.projectalpha.speeddating.models.UserDataModel;

import java.util.ArrayList;
import java.util.List;


public class MatchesFragment extends Fragment implements ActionDataSource.ActionDataCallbacks, UserDataModel.UserDataCallbacks,
        AdapterView.OnItemClickListener {



    private static final String TAG = "MatchesFragment";
    private MatchesAdapter mAdapter;
    private ArrayList<User> mUsers;

    public MatchesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ActionDataSource.getMatches(this);
        View v =  inflater.inflate(R.layout.fragment_matches, container, false);
        ListView matchesList = (ListView)v.findViewById(R.id.matches_list);
        mUsers = new ArrayList<>();
        mAdapter = new MatchesAdapter(mUsers);
        matchesList.setAdapter(mAdapter);
        matchesList.setOnItemClickListener(this);
        return v;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onFetchedMatches(List<String> matchIds) {
        UserDataModel.getUsersIn(matchIds, this);
    }

    @Override
    public void onUsersFetched(List<User> users) {
        for (User user : users){
            mUsers.clear();
            mUsers.addAll(users);
            mAdapter.notifyDataSetChanged();
        }
    }


    public class MatchesAdapter extends ArrayAdapter<User>{
        MatchesAdapter(List<User> users){
            super(MatchesFragment.this.getActivity(), android.R.layout.simple_list_item_1, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView v =  (TextView)super.getView(position, convertView, parent);
            v.setText(getItem(position).getNickname());
            return v;
        }
    }
}
