package com.bizzy.projectalpha.speeddating.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.models.User;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by kevin on 6/1/15.
 */
public class CardAdapter extends ArrayAdapter<User> {

    public CardAdapter(Context context, List<User> users){
        super(context, R.layout.card, R.id.name, users);
    }

    @Override
    public CardView getView(int position, View convertView, ViewGroup parent) {
        CardView v = (CardView)super.getView(position, convertView, parent);
        TextView nameView = (TextView)v.findViewById(R.id.name);
        nameView.setText(getItem(position).getNickname());
        ImageView userImage= (ImageView)v.findViewById(R.id.card_user_image);
        Glide.with(getContext())
                .load(getItem(position).getPhotoUrl())
                .into(userImage);
        return v;
    }
}
