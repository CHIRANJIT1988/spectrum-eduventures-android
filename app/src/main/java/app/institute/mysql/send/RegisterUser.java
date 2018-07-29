package app.institute.mysql.send;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


import app.institute.R;
import app.institute.RegisterActivity;
import app.institute.app.MyApplication;
import app.institute.helper.OnTaskCompleted;
import app.institute.helper.Security;
import app.institute.model.User;
import app.institute.session.SessionManager;

import static app.institute.app.Global.CONNECTIVITY_ERROR;
import static app.institute.app.Global.DEVICE_ID;
import static app.institute.app.Global.ERROR_TAG;
import static app.institute.app.Global.JSON_TAG;
import static app.institute.app.Global.KEY;
import static app.institute.app.Global.MAX_RETRIES;
import static app.institute.app.Global.MESSAGE;
import static app.institute.app.Global.MOBILE_NUMBER;
import static app.institute.app.Global.NAME;
import static app.institute.app.Global.PASSWORD;
import static app.institute.app.Global.PROFILE_DETAILS;
import static app.institute.app.Global.REG_ID;
import static app.institute.app.Global.RESPONSE_TAG;
import static app.institute.app.Global.STATUS_CODE;
import static app.institute.app.Global.TIMEOUT;
import static app.institute.app.Global.USER_ID;
import static app.institute.app.Global.USER_NAME;
import static app.institute.app.MyApplication.SECRET_KEY;
import static app.institute.app.MyApplication.prefs;

public class RegisterUser
{
	private OnTaskCompleted listener;

	private Context context;
	
	public RegisterUser(Context _context , OnTaskCompleted listener)
	{
		this.listener = listener;
		this.context = _context;
	}

	public void execute(final User user)
	{
		/**
		 * Server target URL
		 */
		final String URL = context.getResources().getString(R.string.spectrumServerBaseUrl)
				+ context.getResources().getString(R.string.spectrumServerRegisterUrl);

		final StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

			@Override
			public void onResponse(String response)
			{
				try
				{
					Log.v(RESPONSE_TAG, response);

					JSONObject jsonObj = new JSONObject(response);

					int status_code = jsonObj.getInt(STATUS_CODE);
					String message = jsonObj.getString(MESSAGE);

					if (status_code == 200)
					{
						/**
						 * Successful
						 */
						prefs.edit().putString(KEY, Security.decrypt(jsonObj.getString(KEY), SECRET_KEY)).apply();

						SessionManager session = new SessionManager(context);

						String user_id = Security.decrypt(jsonObj.getString(USER_ID), SECRET_KEY);
						String name = Security.decrypt(jsonObj.getString(USER_NAME), SECRET_KEY);

						jsonObj.put(NAME, name);
						jsonObj.put(MOBILE_NUMBER, RegisterActivity.user.phone_number);
						prefs.edit().putString(PROFILE_DETAILS, jsonObj.toString()).apply();

						RegisterActivity.user.setUserId(user_id);
						RegisterActivity.user.setName(name);
						session.createLoginSession(RegisterActivity.user);

						listener.onTaskCompleted(true, status_code, message);
					}

					else
					{
						/**
						 * Unsuccessful
						 */
						listener.onTaskCompleted(false, status_code, message);
					}
				}

				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error)
			{
				/**
				 * if (error instanceof TimeoutError)
				 */
				Log.v(ERROR_TAG, "" + error.getMessage());
				listener.onTaskCompleted(false, 500, CONNECTIVITY_ERROR);
			}
		})

		{
			@Override
			protected Map<String, String> getParams()
			{
				Map<String, String> params = new HashMap<>();

				try
				{
					JSONObject jsonObject = new JSONObject();

					jsonObject.put(REG_ID, user.fcm_reg_id);
					jsonObject.put(NAME, user.name);
					jsonObject.put(MOBILE_NUMBER, user.phone_number);
					jsonObject.put(PASSWORD, user.password);
					jsonObject.put(DEVICE_ID, user.device_id);

					params.put(JSON_TAG, Security.encrypt(jsonObject.toString(), SECRET_KEY));
				}

				catch (JSONException e)
				{
					e.printStackTrace();
				}

				Log.v(JSON_TAG, "" + params);

				return params;
			}
		};

		/**
		 * Retry if Server time out
		 */
		RetryPolicy policy = new DefaultRetryPolicy(TIMEOUT, MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		postRequest.setRetryPolicy(policy);

		/**
		 * Add request to queue
		 */
		MyApplication.getInstance().addToRequestQueue(postRequest);
	}
}