package app.institute;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import app.institute.session.SessionManager;
import app.institute.slider.MyIntro;

import static app.institute.app.Global.FIRST_RUN;
import static app.institute.app.MyApplication.prefs;


public class SplashScreenActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (prefs.getBoolean(FIRST_RUN, true))
        {
            prefs.edit().putBoolean(FIRST_RUN, false).apply();
            startActivity(new Intent(SplashScreenActivity.this, MyIntro.class));
            finish();
            return;
        }

        new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    // This method will be executed once the timer is over
                    // Start your app main activity


                    if(new SessionManager(getApplicationContext()).isLoggedIn())
                    {
                        startActivity(new Intent(SplashScreenActivity.this, DashboardActivity.class));
                    }

                    else
                    {
                        startActivity(new Intent(SplashScreenActivity.this, RegisterActivity.class));
                    }

                    finish();
                }

            }, 3000);
    }


    /** Called when the activity is about to become visible. */
    @Override
    protected void onStart()
    {

        super.onStart();
        Log.d("Inside : ", "onStart() event");
    }


    /** Called when the activity has become visible. */
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("Inside : ", "onResume() event");
    }


    /** Called when another activity is taking focus. */
    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d("Inside : ", "onPause() event");
    }


    /** Called when the activity is no longer visible. */
    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d("Inside : ", "onStop() event");
    }


    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d("Inside : ", "onDestroy() event");
    }


    @Override
    public void onBackPressed()
    {

    }
}