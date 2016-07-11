package com.bizzy.projectalpha.speeddating.loadmore;

import android.content.Context;
import android.view.ViewGroup;

import com.bizzy.projectalpha.speeddating.entites.Person;
import com.bizzy.projectalpha.speeddating.viewholder.PersonViewHolder;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;


/**
 * Created by Mr.Jude on 2015/7/18.
 */
public class PersonAdapter extends RecyclerArrayAdapter<Person> {
    public PersonAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new PersonViewHolder(parent);
    }


    public boolean isPositionHeader(int position) {
        return position == 0;
    }
}
