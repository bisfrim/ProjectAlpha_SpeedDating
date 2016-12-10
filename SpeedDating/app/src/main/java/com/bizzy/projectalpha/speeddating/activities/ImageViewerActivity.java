package com.bizzy.projectalpha.speeddating.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bizzy.projectalpha.speeddating.R;
import com.bumptech.glide.Glide;

public class ImageViewerActivity extends AppCompatActivity {

    public static String EXTRA_IMAGE_URL = "image_url";

    ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        mImage = (ImageView)findViewById(R.id.image);
        String imageUrl = "";
        if(getIntent() != null){
            imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        }
        if(!imageUrl.isEmpty()){
            Glide.with(this).load(imageUrl).into(mImage);
        }
    }
}
