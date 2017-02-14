package com.bizzy.projectalpha.speeddating.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.models.User;
import com.bizzy.projectalpha.speeddating.models.UserUploadedPhotos;
import com.bumptech.glide.Glide;
import com.parse.ParseImageView;

import java.util.ArrayList;

import eu.fiskur.simpleviewpager.ImageURLLoader;
import eu.fiskur.simpleviewpager.SimpleViewPager;

public class SingleImageViewActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    String imageUrl;
    ParseImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_swipe_adapter);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(UsersProfileActivity.mUser.concat("'s") + "  - Photo's");

        if (getIntent().getExtras() != null) {
            imageUrl = getIntent().getExtras().getString("URL");
        }

        //image = (ParseImageView) findViewById(R.id.imageViewFull);

        SimpleViewPager simpleViewPager = (SimpleViewPager) findViewById(R.id.simple_view_pager);
        String[] demoUrlArray = new String[]{imageUrl
        };

        simpleViewPager.setImageUrls(demoUrlArray, new ImageURLLoader() {
            @Override
            public void loadImage(ImageView view, String url) {
                Glide.with(SingleImageViewActivity.this).load(url).fitCenter().into(view);
            }
        });
        int indicatorColor = Color.parseColor("#ffffff");
        int selectedIndicatorColor = Color.parseColor("#fff000");
        simpleViewPager.showIndicator(indicatorColor, selectedIndicatorColor);

        //Glide.with(this.getApplicationContext()).load(imageUrl).fitCenter().into(image);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
