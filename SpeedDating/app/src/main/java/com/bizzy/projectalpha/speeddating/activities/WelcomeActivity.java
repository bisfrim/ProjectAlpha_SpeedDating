package com.bizzy.projectalpha.speeddating.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bizzy.projectalpha.speeddating.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class WelcomeActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_welcome);


        addSlide(AppIntroFragment.newInstance("Welcome!", "We just need some permissions to start. (This is only as an example...this app doesn't utilize any of the perms.)\n", R.drawable.ic_slide1, Color.parseColor("#2196F3")));
        addSlide(AppIntroFragment.newInstance("Camera", "We need to use the camera.\n", R.drawable.ic_slide2, Color.parseColor("#2196F3")));
        addSlide(AppIntroFragment.newInstance("Storage", "We need to save stuff on your device. \n", R.drawable.ic_slide3, Color.parseColor("#2196F3")));
        addSlide(AppIntroFragment.newInstance("All Set!", "Enjoy our app! \n", R.drawable.ic_slide4, Color.parseColor("#2196F3")));
        addSlide(AppIntroFragment.newInstance("Location", "One more permission! We need to locate your device. \n", R.drawable.ic_slide4, Color.parseColor("#2196F3")));
        addSlide(AppIntroFragment.newInstance("All set!", "All done! \n", R.drawable.ic_slide4, Color.parseColor("#2196F3")));

        //showStatusBar(true);
        showSkipButton(false);
        setDepthAnimation();
        

        // Ask Camera permission in the second slide
        askForPermissions(new String[]{Manifest.permission.CAMERA}, 2);

        // Ask Storage permission in the third slide
        askForPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);

        // Ask Location permission in the fifth slide
        askForPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);

    }




    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);

        loadMainActivity();
        Toast.makeText(getApplicationContext(), getString(R.string.skip), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSlideChanged(Fragment oldFragment, Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

        //Toast.makeText(getBaseContext(), "Hi!", Toast.LENGTH_SHORT).show();
    }

    public void getStarted(View v) {
        loadMainActivity();
    }

    private void loadMainActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        loadMainActivity();
    }
}
