package app.institute.mysql.send;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import app.institute.R;
import app.institute.app.MyApplication;
import app.institute.helper.OnTaskCompleted;
import app.institute.helper.Security;


import static app.institute.app.Global.CONNECTIVITY_ERROR;
import static app.institute.app.Global.ERROR_TAG;
import static app.institute.app.Global.JSON_TAG;
import static app.institute.app.Global.KEY;
import static app.institute.app.Global.MAX_RETRIES;
import static app.institute.app.Global.MESSAGE;
import static app.institute.app.Global.RESPONSE_TAG;
import static app.institute.app.Global.STATUS_CODE;
import static app.institute.app.Global.TIMEOUT;
import static app.institute.app.Global.USER;
import static app.institute.app.MyApplication.SECRET_KEY;
import static app.institute.app.MyApplication.prefs;

public class UpdateProfileDetails
{
	private OnTaskCompleted listener;
	private Context context;

	public UpdateProfileDetails(Context context , OnTaskCompleted listener)
	{
		this.listener = listener;
		this.context = context;
	}

	public void execute(final String json_data, final String user_id)
	{
		/**
		 * Server target URL
		 */
		final String URL = context.getResources().getString(R.string.spectrumServerBaseUrl)
				+ context.getResources().getString(R.string.spectrumServerUpdateProfileUrl);

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
					params.put(JSON_TAG, Security.encrypt(json_data, prefs.getString(KEY, "")));
					params.put(USER, Security.encrypt(user_id, SECRET_KEY));
				}

				catch (Exception e)
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