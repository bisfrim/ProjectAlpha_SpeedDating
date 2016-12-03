package com.bizzy.projectalpha.speeddating.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.entites.Person;
import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * Created by Mr.Jude on 2015/2/22.
 */
public class PersonViewHolder extends BaseViewHolder<Person> {
    public TextView mTv_name;
    public ImageView mImg_face;
    public TextView mTv_distance;
    //public TextView mTv_age;
    public ImageView onlineStatusImage;


    public PersonViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_person);
        mTv_name = $(R.id.person_name);
        mTv_distance = $(R.id.person_sign);
        mImg_face = $(R.id.person_face);
        //mTv_age = $(R.id.person_age);
        onlineStatusImage =  $(R.id.image_online_status);
    }

    @Override
    public void setData(final Person person){
        mTv_name.setText(person.getName());
        mTv_distance.setText(person.getSign());
        Glide.with(getContext())
                .load(person.getFace())
                .placeholder(R.drawable.default_image)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(mImg_face);

        Glide.with(getContext())
                .load(person.getFace())
                .placeholder(R.drawable.default_image)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(onlineStatusImage);
    }
}
