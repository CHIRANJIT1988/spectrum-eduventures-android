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

import java.util.HashMap;
import java.util.Map;

import app.institute.R;
import app.institute.app.MyApplication;
import app.institute.helper.OnTaskCompleted;
import app.institute.helper.Security;
import app.institute.model.Branch;
import app.institute.model.MockTest;

import static app.institute.app.Global.ATTEMPTED_ON;
import static app.institute.app.Global.CONNECTIVITY_ERROR;
import static app.institute.app.Global.DURATION;
import static app.institute.app.Global.ERROR_TAG;
import static app.institute.app.Global.JSON_TAG;
import static app.institute.app.Global.KEY_BRANCH_CODE;
import static app.institute.app.Global.KEY_CLASS_CODE;
import static app.institute.app.Global.MAX_RETRIES;
import static app.institute.app.Global.NEGATIVE_SCORE;
import static app.institute.app.Global.POSITIVE_SCORE;
import static app.institute.app.Global.RESPONSE_TAG;
import static app.institute.app.Global.TEST_ID;
import static app.institute.app.Global.TEST_NAME;
import static app.institute.app.Global.TIMEOUT;
import static app.institute.app.Global.TOTAL_MARKS;
import static app.institute.app.Global.USER_ID;
import static app.institute.app.MyApplication.SECRET_KEY;
import static app.institute.app.MyApplication.getInstance;

public class ReceiveTestPapers
{
	private OnTaskCompleted listener;
	private Context context;

	public ReceiveTestPapers(Context context , OnTaskCompleted listener)
	{
		this.listener = listener;
		this.context = context;
	}

	public void execute(final Branch branch, final String user_id)
	{
		/**
		 * Server target URL
		 */
		final String URL = context.getResources().getString(R.string.spectrumServerBaseUrl)
				+ context.getResources().getString(R.string.spectrumServerTestPaperUrl);

		final StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

			@Override
			public void onResponse(String response)
			{
				try
				{
					Log.v(RESPONSE_TAG, response);

					JSONArray arr = new JSONArray(response);
					JSONObject jsonObj;

					getInstance().testPaperList.clear();

					if(arr.length() > 0)
					{
						for (int i = 0; i < arr.length(); i++)
						{
							jsonObj = arr.getJSONObject(i);

							int test_id = jsonObj.getInt(TEST_ID);
							String test_name = jsonObj.getString(TEST_NAME);
							int total_marks = jsonObj.getInt(TOTAL_MARKS);
							int duration = jsonObj.getInt(DURATION);
							int positive_score = jsonObj.getInt(POSITIVE_SCORE);
							int negative_store = jsonObj.getInt(NEGATIVE_SCORE);
							String attempted_on = jsonObj.getString(ATTEMPTED_ON);

							int percentage = ((positive_score - negative_store) * 100)/ total_marks;

							MockTest test = new MockTest(test_id, test_name, total_marks, duration, attempted_on, percentage);
							getInstance().testPaperList.add(test);
						}

						listener.onTaskCompleted(true, 200, "success");
						return;
					}

					listener.onTaskCompleted(false, 200, "No Test Available");
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

					jsonObject.put(KEY_BRANCH_CODE, branch.branch_code);
					jsonObject.put(KEY_CLASS_CODE, branch._class.class_code);
					jsonObject.put(USER_ID, user_id);

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