package app.institute.mysql.receive;

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

import java.util.HashMap;
import java.util.Map;

import app.institute.R;
import app.institute.app.MyApplication;
import app.institute.helper.OnCountCompleted;
import app.institute.model.Branch;

import static app.institute.app.Global.CONNECTIVITY_ERROR;
import static app.institute.app.Global.ERROR_TAG;
import static app.institute.app.Global.JSON_TAG;
import static app.institute.app.Global.KEY_BRANCH_CODE;
import static app.institute.app.Global.KEY_CLASS_CODE;
import static app.institute.app.Global.KEY_SUBJECT_CODE;
import static app.institute.app.Global.MAX_RETRIES;
import static app.institute.app.Global.RESPONSE_TAG;
import static app.institute.app.Global.STATUS_CODE;
import static app.institute.app.Global.TIMEOUT;
import static app.institute.app.Global.TOTAL;

public class CountDPP
{
	private OnCountCompleted listener;
	private Context context;

	public CountDPP(Context context , OnCountCompleted listener)
	{
		this.listener = listener;
		this.context = context;
	}

	public void execute(final Branch branch)
	{
		/**
		 * Server target URL
		 */
		final String URL = context.getResources().getString(R.string.spectrumServerBaseUrl)
				+ context.getResources().getString(R.string.spectrumServerCountDPPUrl);

		final StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

			@Override
			public void onResponse(String response)
			{
				try
				{
					Log.v(RESPONSE_TAG, response);
					JSONObject jsonObj = new JSONObject(response);

					if(jsonObj.getInt(STATUS_CODE) == 200)
					{
						/**
                         * Successful
						 */
						listener.onCountCompleted(200, jsonObj.getInt(TOTAL), "dpp");
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
				listener.onCountCompleted(500, 0, CONNECTIVITY_ERROR);
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

					jsonObject.put(KEY_BRANCH_CODE, branch.branch_code);
					jsonObject.put(KEY_CLASS_CODE, branch._class.class_code);
					jsonObject.put(KEY_SUBJECT_CODE, branch._subject.subject_code);

					params.put(JSON_TAG, jsonObject.toString());
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