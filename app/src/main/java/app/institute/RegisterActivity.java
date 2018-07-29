package app.institute;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import app.institute.model.User;


public class RegisterActivity extends AppCompatActivity
{

    private static final int READ_SMS_PERMISSION_REQUEST_CODE = 1;
    private int back_pressed = 0;
    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        login_redirect();
        permissionCheckerReadSMS();
    }


    private void login_redirect()
    {
        Fragment fragment = new LoginFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim);
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();

        setTitle("LOGIN");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case android.R.id.home:

                if(back_pressed == 0)
                {
                    back_pressed++;
                    Toast.makeText(getApplicationContext(), "Press Back Button again to Exit", Toast.LENGTH_LONG).show();
                }

                else
                {
                    finish();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed()
    {

        if(back_pressed == 0)
        {
            back_pressed++;
            Toast.makeText(getApplicationContext(), "Press Back Button again to Exit", Toast.LENGTH_LONG).show();
        }

        else
        {
            finish();
        }
    }


    private boolean permissionCheckerReadSMS() {

        if (!checkPermissionReadSMS()) {
            requestPermissionReadSMS();
            return false;
        }

        return true;
    }


    private boolean checkPermissionReadSMS()
    {

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

        if (result == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }

        else
        {
            return false;
        }
    }


    private void requestPermissionReadSMS(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS))
        {
            makeToast("SMS permission allows us to read or receive SMS. Please allow in App Settings for read or receive SMS.");
        }

        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, READ_SMS_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {

        switch (requestCode)
        {

            case READ_SMS_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeToast("Permission Granted");
                } else {
                    makeToast("Permission Denied");
                }

                break;
        }
    }


    private void makeToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}