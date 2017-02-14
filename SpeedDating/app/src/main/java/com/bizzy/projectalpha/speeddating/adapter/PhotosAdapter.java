package com.bizzy.projectalpha.speeddating.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.models.Photo;
import com.bizzy.projectalpha.speeddating.models.UserUploadedPhotos;
import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erik on 12/11/2015.
 */
public class PhotosAdapter extends ArrayAdapter<Photo> {
    Context mContext;
    int mResource;
    List<Photo> mObjects;
    String thisUser;
    ArrayList<Photo> photos = new ArrayList<>();
    //Album a;
    Photo p;
    ParseFile image;
    View mConvertView;
    boolean isFull = false;


    public PhotosAdapter(Context context, int resource, List<Photo> objects, String userName) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.mObjects = objects;
        this.thisUser = userName;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
        mConvertView = convertView;
        Log.d("parseDemo", "USername : " + thisUser);
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (mConvertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mConvertView = inflater.inflate(mResource, parent, false);
        }
        p = mObjects.get(position);

        final ImageView iv = (ImageView) mConvertView.findViewById(R.id.imagePreview);
        Glide.with(mContext).load(p.getImageUrl()).into(iv);

        return mConvertView;
    }
}
