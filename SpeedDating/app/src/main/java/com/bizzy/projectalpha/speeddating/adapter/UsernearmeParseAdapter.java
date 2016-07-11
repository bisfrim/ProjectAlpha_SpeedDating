package com.bizzy.projectalpha.speeddating.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.User;
import com.bizzy.projectalpha.speeddating.viewholder.PersonViewHolder;
import com.bumptech.glide.Glide;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.bizzy.projectalpha.speeddating.viewholder.ImageViewHolder;

/**
 * Created by androiddev on 24.11.15.
 */
public class UsernearmeParseAdapter extends ParseQueryAdapter<User> {
    private User mCurrentUser;
    public static final int TYPE_MALE = 0;
    public static final int TYPE_FEMALE = 1;
    public static final int TYPE_BOTH = 2;

    public UsernearmeParseAdapter(Context context, final int type) {
        super(context, new QueryFactory<User>() {
            @Override
            public ParseQuery<User> create() {
                User currentUser = (User)User.getCurrentUser();
                ParseQuery<User> query = User.getUserQuery();
                query.whereWithinKilometers(User.COL_GEO_POINT, currentUser.getGeoPoint(), 100);
                query.whereExists(User.COL_GEO_POINT);
                switch (type){
                    case TYPE_MALE:
                        query.whereEqualTo(User.USER_IS_MALE, "true");
                        break;
                    case TYPE_FEMALE:
                        query.whereEqualTo(User.USER_IS_MALE, "false");
                        break;
                    case TYPE_BOTH:
                        break;
                }
                
                query.whereNotEqualTo(User.USER_ID, currentUser.getObjectId());
                return query;
            }
        });
        mCurrentUser = (User)User.getCurrentUser();
    }

    @Override
    public View getNextPageView(View v, ViewGroup parent) {
        if(v == null){
            v = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_more, null);
            v.measure(v.getMeasuredWidth(), v.getMeasuredWidth());
        }
        return v;
    }

    @Override
    public View getItemView(User object, View v, ViewGroup parent) {
        //ViewHolder viewHolder = null;
        PersonViewHolder personImage;
        if(v == null){
            v = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_person, parent, false);
            //viewHolder = new ViewHolder();

            personImage = new PersonViewHolder(parent);
            personImage.mImg_face = (ImageView)v.findViewById(R.id.person_face);
            personImage.mTv_name = (TextView)v.findViewById(R.id.person_name);
            personImage.mTv_sign = (TextView)v.findViewById(R.id.person_sign);
            personImage.onlineStatusImage = (ImageView)v.findViewById(R.id.image_online_status);
            //viewHolder.userPhoto = (ImageView)v.findViewById(R.id.user_photo);
            //viewHolder.username = (TextView) v.findViewById(R.id.username);
            //viewHolder.onlineStatusImage = (ImageView) v.findViewById(R.id.image_online_status);
            //viewHolder.distance = (TextView)v.findViewById(R.id.text_distance);
            //v.setTag(viewHolder);
            v.setTag(personImage);
        } else {
            personImage = (PersonViewHolder) v.getTag();
        }
        Glide.with(getContext()).load(object.getPhotoUrl()).into(personImage.mImg_face);
        personImage.mTv_name.setText(object.getNickname());
        personImage.mTv_sign.setText(String.format("%.2f km",object.getGeoPoint().distanceInKilometersTo(mCurrentUser.getGeoPoint())));
        if(object.getOnlineStatus().equals("online")) {
            personImage.onlineStatusImage.setImageResource(R.drawable.ic_online_15_0_alizarin);
        } else {
            personImage.onlineStatusImage.setImageResource(R.drawable.ic_offline_15_0_alizarin);
        }


        //if(object.getOnlineStatus().equals("online")) {
          //  viewHolder.onlineStatusImage.setImageResource(R.drawable.ic_online_15_0_alizarin);
        //} else {
          //  viewHolder.onlineStatusImage.setImageResource(R.drawable.ic_offline_15_0_alizarin);
        //}
        return v;
    }

    private class ViewHolder{
        ImageView userPhoto;
        ImageView onlineStatusImage;
        TextView username;
        TextView distance;
    }
}
