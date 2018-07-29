package app.institute.mysql.receive;

import org.json.JSONArray;
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
import app.institute.model.Branch;
import app.institute.model.Class;
import app.institute.model.Subject;
import app.institute.sqlite.SQLiteDB;

import static app.institute.app.Global.CONNECTIVITY_ERROR;
import static app.institute.app.Global.ERROR_TAG;
import static app.institute.app.Global.KEY_BRANCH_CODE;
import static app.institute.app.Global.KEY_BRANCH_NAME;
import static app.institute.app.Global.KEY_CLASS_CODE;
import static app.institute.app.Global.KEY_CLASS_NAME;
import static app.institute.app.Global.KEY_SUBJECT_CODE;
import static app.institute.app.Global.KEY_SUBJECT_NAME;
import static app.institute.app.Global.MAX_RETRIES;
import static app.institute.app.Global.RESPONSE_TAG;
import static app.institute.app.Global.TIMEOUT;

public class ReceiveDefaultData
{
	private OnTaskCompleted listener;
	private Context context;
	private SQLiteDB helper;

	public ReceiveDefaultData(Context context , OnTaskCompleted listener)
	{
		this.listener = listener;
		this.context = context;
		this.helper = new SQLiteDB(context);
	}

	public void execute()
	{
		/**
		 * Server target URL
		 */
		final String URL = context.getResources().getString(R.string.spectrumServerBaseUrl)
				+ context.getResources().getString(R.string.spectrumServerDefaultDataUrl);

		final StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

			@Override
			public void onResponse(String response)
			{
				try
				{
					Log.v(RESPONSE_TAG, response);

					JSONArray arr = new JSONArray(response);
					JSONObject jsonObj;

					if(arr.length() > 0)
					{
						for (int i = 0; i < arr.length(); i++)
						{
							jsonObj = arr.getJSONObject(i);

							String branch_code = jsonObj.getString(KEY_BRANCH_CODE);
							String branch_name = jsonObj.getString(KEY_BRANCH_NAME);
							String subject_code = jsonObj.getString(KEY_SUBJECT_CODE);
							String subject_name = jsonObj.getString(KEY_SUBJECT_NAME);
							String class_code = jsonObj.getString(KEY_CLASS_CODE);
							String class_name = jsonObj.getString(KEY_CLASS_NAME);

							Class _class = new Class(class_code, class_name);
							Subject _subject = new Subject(subject_code, subject_name);

							Branch _branch = new Branch(_subject, _class, branch_code, branch_name);

							helper.insert(_branch);
						}

						listener.onTaskCompleted(true, 200, "Synchronization Successful");
						return;
					}

					listener.onTaskCompleted(false, 200, "No Data Found");
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
		});

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