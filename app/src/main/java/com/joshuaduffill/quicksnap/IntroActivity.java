package com.joshuaduffill.quicksnap;

import android.*;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Joshua on 29/04/2017.
 */

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
//        addSlide(firstFragment);
//        addSlide(secondFragment);
//        addSlide(thirdFragment);
//        addSlide(fourthFragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("QuickSnap!", "QuickSnap is a photo editor that allows you to capture and share your best moments with friends and family", R.mipmap.quicksnap_logo, getColor(R.color.primary_dark)));
        addSlide(AppIntroFragment.newInstance("Snap, Edit & Share!", "In order for the app to work effectively we require storage and camera permissions", R.mipmap.quicksnap_logo, getColor(R.color.primary_dark)));


        // Ask for CAMERA permission on the second slide
        askForPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1); // OR

        // This will ask for the camera permission AND the contacts permission on the same slide.
        // Ensure your slide talks about both so as not to confuse the user.
        askForPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 2);

        // OPTIONAL METHODS
        // Override bar/separator color.
//        setBarColor(Color.parseColor("#3F51B5"));
//        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
//        showSkipButton(false);
//        setProgressButtonEnabled(false);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
