package app.institute.slider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;

import app.institute.R;
import app.institute.RegisterActivity;
import app.institute.SplashScreenActivity;

/**
 * Created by HP on 10/23/2016.
 */
public class MyIntro extends AppIntro
{
    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        //adding the three slides for introduction app you can ad as many you needed
        addSlide(AppIntroSampleSlider.newInstance(R.layout.app_intro1));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.app_intro2));

        // Show and Hide Skip and Done buttons
        showStatusBar(true);
        showSkipButton(true);

        // Turn vibration on and set intensity
        // You will need to add VIBRATE permission in Manifest file
        setVibrate(false);
        setVibrateIntensity(30);

        //Add animation to the intro slider
        setDepthAnimation();
    }

    @Override
    public void onSkipPressed() {
        // Do something here when users click or tap on Skip button.
        Toast.makeText(getApplicationContext(), "Introduction Skipped", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
        finish();
    }

    @Override
    public void onNextPressed() {
        // Do something here when users click or tap on Next button.
    }

    @Override
    public void onDonePressed() {
        // Do something here when users click or tap tap on Done button.
        startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something here when slide is changed
    }
}