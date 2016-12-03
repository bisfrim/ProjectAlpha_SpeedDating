package com.bizzy.projectalpha.speeddating.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bizzy.projectalpha.speeddating.R;


public class DetailsTabFragment extends Fragment {
    private static final String TEXT_FRAGMENT = "TEXT_FRAGMENT";
    private static final String LOG_TAG = "MyDetailsTabFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_details_tab,container,false);
        return v;
    }

}
