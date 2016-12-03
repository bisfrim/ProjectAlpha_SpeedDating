package com.bizzy.projectalpha.speeddating.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.activities.RegisterActivity;
import com.bizzy.projectalpha.speeddating.models.User;
import com.bizzy.projectalpha.speeddating.viewholder.PersonViewHolder;
import com.bumptech.glide.Glide;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by androiddev on 24.11.15.
 */
public class UsernearmeParseAdapter extends ParseQueryAdapter<User> {
    private User mCurrentUser;
    public static final int TYPE_MALE = 0;
    public static final int TYPE_FEMALE = 1;
    public static final int TYPE_BOTH = 2;

    //protected ParseRelation<User> mContactRelation;

    public UsernearmeParseAdapter(final Context context) {

        super(context, new QueryFactory<User>() {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            double min, max;
            Set<String> AgeRange = settings.getStringSet("age_range", null);
            int searchDistance = settings.getInt("search_distance_radio", 100); //default search radius
            @Override
            public ParseQuery<User> create() {
                User currentUser = (User) User.getCurrentUser();
                ParseQuery<User> query = User.getUserQuery();
                Boolean male = settings.getBoolean("switch_male_pref", false);
                Log.d("GenderSelectMale: ", String.valueOf(male));
                Boolean female = settings.getBoolean("switch_female_pref", true);
                Log.d("GenderSelectFemale: ", String.valueOf(female));

                if(AgeRange != null)
                {
                    List list = new ArrayList(AgeRange);
                    double val1 = Integer.parseInt(list.get(0).toString());
                    double val2 = Integer.parseInt(list.get(1).toString());
                    min = Math.min(val1, val2);
                    max = Math.max(val1, val2);
                }
                else{
                    min = 18;
                    max = 100;
                }
                Log.d("AgeRange",  "Minimum: " + min + ", Max: "+max);


                //check for changes in our search radius and parse it to our query
                if(searchDistance > 0){
                    int currentSearch = searchDistance;
                    query.whereWithinKilometers(User.COL_GEO_POINT, currentUser.getGeoPoint(), currentSearch);
                    Log.d("CurrentSearch: " , String.valueOf(currentSearch));
                }

                //Query age range
                query.whereGreaterThanOrEqualTo(User.USER_AGE, min);
                query.whereLessThanOrEqualTo(User.USER_AGE, max);

                //Query by gender selection
                if(female && !male){
                    query.whereEqualTo(User.USER_IS_MALE, User.TYPE_FALSE);
                }else if(!female && male){
                    query.whereEqualTo(User.USER_IS_MALE, User.TYPE_TRUE);
                }

                //query.whereWithinKilometers(User.COL_GEO_POINT, currentUser.getGeoPoint(), radius * User.METERS_PER_FEET / User.METERS_PER_KILOMETER);
                query.whereExists(User.COL_GEO_POINT); //query users with geopoints
                query.orderByAscending(User.KEY_USERNAME);
                query.whereNotEqualTo(User.USER_ID, currentUser.getObjectId());
                query.setLimit(1000);
                return query;
            }
        });
        mCurrentUser = (User) User.getCurrentUser();
    }


    @Override
    public View getNextPageView(View v, ViewGroup parent) {
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_more, null);
            v.measure(v.getMeasuredWidth(), v.getMeasuredWidth());
        }
        return v;
    }

    @Override
    public View getItemView(User object, View v, ViewGroup parent) {
        //ViewHolder viewHolder = null;
        PersonViewHolder personImage;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_person, parent, false);
            //viewHolder = new ViewHolder();
            personImage = new PersonViewHolder(parent);
            personImage.mImg_face = (ImageView) v.findViewById(R.id.person_face);
            personImage.mTv_name = (TextView) v.findViewById(R.id.person_name);
            personImage.onlineStatusImage = (ImageView) v.findViewById(R.id.image_online_status);
            personImage.mTv_distance = (TextView) v.findViewById(R.id.person_sign);
            //personImage.mTv_age = (TextView)v.findViewById(R.id.person_age);
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
        personImage.mTv_name.setText(object.getNickname().concat(",  "+object.getAge()));
        //personImage.mTv_age.setText(object.getAge().toString());
        personImage.mTv_distance.setText(String.format("%.2f km", object.getGeoPoint().distanceInKilometersTo(mCurrentUser.getGeoPoint())));
        if (object.getOnlineStatus().equals("online")) {
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


}
