package app.institute;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import app.institute.helper.Helper;
import app.institute.helper.OnTaskCompleted;
import app.institute.model.School;
import app.institute.model.User;
import app.institute.mysql.send.ChangePassword;
import app.institute.mysql.send.UpdateSchoolDetails;
import app.institute.session.SessionManager;

import static app.institute.app.Global.DOB;
import static app.institute.app.Global.EMAIL;
import static app.institute.app.Global.GENDER;
import static app.institute.app.Global.LOCATION;
import static app.institute.app.Global.MOBILE_NUMBER;
import static app.institute.app.Global.NAME;
import static app.institute.app.Global.PROFILE_DETAILS;
import static app.institute.app.Global.SCHOOL_DETAILS;
import static app.institute.app.Global.SCHOOL_NAME;
import static app.institute.app.Global.STATE;
import static app.institute.app.MyApplication.prefs;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener,
        OnTaskCompleted, AddSchoolFragment.AddSchoolFragmentListener, NewPasswordFragment.AddSchoolFragmentListener
{

    private Button button_new_password, button_edit_school, button_edit_profile;
    private TextView tv_mobile_number, tv_student_name, tv_student_email, tv_student_location, tv_student_gender, tv_student_dob, tv_school_name, tv_school_location, tv_progress;
    private ProgressBar profile_progress;
    private School schoolObject;

    private ProgressDialog pDialog;
    private SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("My Profile");

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById();
        setListener();

        this.pDialog = new ProgressDialog(this);
        this.session = new SessionManager(this);
    }

    private void initProgressDialog(String message)
    {
        pDialog.setMessage(message);
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(false);
        pDialog.show();
    }

    private void setListener()
    {
        button_edit_school.setOnClickListener(this);
        button_edit_profile.setOnClickListener(this);
        button_new_password.setOnClickListener(this);
    }

    private void findViewById()
    {

        button_edit_school = (Button) findViewById(R.id.button_edit_school);
        button_edit_profile = (Button) findViewById(R.id.button_edit_profile);
        button_new_password = (Button) findViewById(R.id.button_new_password);

        tv_mobile_number = (TextView) findViewById(R.id.mobile_number);
        tv_student_name = (TextView) findViewById(R.id.student_name);
        tv_student_email = (TextView) findViewById(R.id.student_email);
        tv_student_location = (TextView) findViewById(R.id.student_location);
        tv_student_gender = (TextView) findViewById(R.id.student_gender);
        tv_student_dob = (TextView) findViewById(R.id.student_dob);

        tv_school_name = (TextView) findViewById(R.id.school_name);
        tv_school_location = (TextView) findViewById(R.id.school_location);

        tv_progress = (TextView) findViewById(R.id.tv_progress);
        profile_progress = (ProgressBar) findViewById(R.id.profile_progress);
    }


    @Override
    public void onResume()
    {

        super.onResume();
        setProfileData(prefs.getString(PROFILE_DETAILS, ""), prefs.getString(SCHOOL_DETAILS, ""));
    }


    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {

            case R.id.button_new_password:

                FragmentManager fm = getSupportFragmentManager();

                NewPasswordFragment dialogFragment = new NewPasswordFragment();
                dialogFragment.setListener(ProfileActivity.this);
                dialogFragment.setRetainInstance(true);
                dialogFragment.show(fm, "NewPasswordFragment");

                break;

            case R.id.button_edit_school:

                FragmentManager fm1 = getSupportFragmentManager();

                AddSchoolFragment dialogFragment1 = new AddSchoolFragment(getApplicationContext());
                dialogFragment1.setListener(ProfileActivity.this);
                dialogFragment1.setRetainInstance(true);
                dialogFragment1.show(fm1, "AddSchoolFragment");

                break;

            case R.id.button_edit_profile:

                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("MOBILE", tv_mobile_number.getText().toString().split("-")[1]);
                startActivity(intent);
                break;

        }
    }


    @Override
    public void onDialogPositiveClick(android.support.v4.app.DialogFragment dialog, School school)
    {

        initProgressDialog("Updating School ...");

        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put(SCHOOL_NAME, school.school_name);
            jsonObject.put(STATE, school.state);

            schoolObject = school;

            new UpdateSchoolDetails(getApplicationContext(), this).execute(jsonObject.toString(), session.getUserId());
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onDialogPositiveClick(android.support.v4.app.DialogFragment dialog, String new_password)
    {
        initProgressDialog("Changing Password ...");
        new ChangePassword(getApplicationContext(), this).execute(session.getUserId(), new_password);
    }


    @Override
    public void onDialogNegativeClick(android.support.v4.app.DialogFragment dialog)
    {
        // Do nothing
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case R.id.action_logout:

                session.logoutUser();
                finish();
                break;

            case android.R.id.home:
            {
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void setProfileData(String profile_json_data, String school_json_data)
    {
        try
        {
            String name = "", email = "", location = "", dob = "", gender = "", mobile_number;
            String school_name = "", school_location = "";

            JSONObject jsonObj = new JSONObject(profile_json_data);

            if(jsonObj.has(NAME))
            {
                name = jsonObj.getString(NAME);
                tv_student_name.setText(Helper.toCamelCase(name));
            }

            if(jsonObj.has(EMAIL))
            {
                email = jsonObj.getString(EMAIL);
                tv_student_email.setText(email);
            }

            if(jsonObj.has(LOCATION))
            {
                location = jsonObj.getString(LOCATION);
                tv_student_location.setText(Helper.toCamelCase(location));
            }

            if(jsonObj.has(DOB))
            {
                dob = jsonObj.getString(DOB);
                tv_student_dob.setText(Helper.dateTimeFormat(dob));
            }

            if(jsonObj.has(GENDER))
            {
                gender = jsonObj.getString(GENDER);
                tv_student_gender.setText(Helper.toCamelCase(gender));
            }

            if (jsonObj.has(MOBILE_NUMBER))
            {
                mobile_number = jsonObj.getString(MOBILE_NUMBER);
                tv_mobile_number.setText(String.valueOf("+91-" + mobile_number));
            }

            jsonObj = new JSONObject(school_json_data);

            if(jsonObj.has(SCHOOL_NAME))
            {
                school_name = jsonObj.getString(SCHOOL_NAME);
                tv_school_name.setText(school_name.toUpperCase());
            }

            if(jsonObj.has(STATE))
            {
                school_location = jsonObj.getString(STATE);
                tv_school_location.setText(Helper.toCamelCase(school_location));
            }

            User userObj = new User(name, email, location, dob, gender);
            School schoolObj = new School(school_name, school_location);
            profile_progress(userObj, schoolObj);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    private void profile_progress(User userObj, School schoolObj)
    {

        int progress = 0;

        if(!userObj.getEmail().trim().isEmpty())
        {
            progress += (100/5);
        }

        if(!userObj.getLocation().trim().isEmpty())
        {
            progress += (100/5);
        }

        if(!userObj.getDateOfBirth().trim().isEmpty())
        {
            progress += (100/5);
        }

        if(!userObj.getGender().trim().isEmpty())
        {
            progress += (100/5);
        }

        if(!schoolObj.school_name.trim().isEmpty())
        {
            progress += (100/5);
        }

        profile_progress.setProgress(progress);
        tv_progress.setText(String.valueOf(progress + "%"));
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {
        try
        {
            if(code == 200)
            {
                JSONObject jsonObject = new JSONObject();

                try
                {
                    jsonObject.put(SCHOOL_NAME, schoolObject.school_name);
                    jsonObject.put(STATE, schoolObject.state);

                    prefs.edit().putString(SCHOOL_DETAILS, jsonObject.toString()).apply();
                    setProfileData(prefs.getString(PROFILE_DETAILS, ""), prefs.getString(SCHOOL_DETAILS, ""));
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {

            if(pDialog.isShowing())
            {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}