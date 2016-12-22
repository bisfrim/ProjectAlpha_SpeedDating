package com.bizzy.projectalpha.speeddating.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.models.User;
import com.bizzy.projectalpha.speeddating.utils.MD5Util;
import com.bumptech.glide.Glide;
import com.parse.ParseUser;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by nubxf5 on 12/13/2016.
 */

public class UserAdapter extends ArrayAdapter<User> {

    protected Context mContext;
    protected List<User> mUsers;

    public UserAdapter(Context context, List<User> users) {
        super(context, R.layout.user_item, users);
        mContext = context;
        mUsers = users;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
            holder.userImageView = (ImageView) convertView.findViewById(R.id.userImageView);
            //holder.checkImageView = (ImageView)convertView.findViewById(R.id.checkImageView);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        User user = mUsers.get(position);
        String email = user.getObjectId().toLowerCase();

        if (email.equals("")){
            holder.userImageView.setImageResource(R.drawable.profile_photo_placeholder);
        }else {
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash +"?s=204&d=404";

            Glide.with(mContext).load(user.getPhotoUrl())
                    .bitmapTransform(new CropCircleTransformation(Glide.get(getContext()).getBitmapPool()))
                    .placeholder(R.drawable.profile_photo_placeholder)
                    .into(holder.userImageView);
        }

        holder.nameLabel.setText(user.getNickname());

      /*  GridView gridView = (GridView)parent;
        if (gridView.isItemChecked(position)) {
            holder.checkImageView.setVisibility(View.VISIBLE);
        }else {
            holder.checkImageView.setVisibility(View.INVISIBLE);
        }*/


        return convertView;
    }


    private static class ViewHolder {
        ImageView userImageView;
        ImageView checkImageView;
        TextView nameLabel;
    }

    public void refill(List<User> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }
}
