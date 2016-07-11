package com.bizzy.projectalpha.speeddating;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bizzy.projectalpha.speeddating.adapter.UsernearmeParseAdapter;

/**
 * Created by nubxf5 on 6/23/2016.
 */
public class FemaleFragmentTab extends Fragment {

    private UsernearmeParseAdapter femaleAdapter, currentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_female_tab,container,false);
        return v;
    }
}
