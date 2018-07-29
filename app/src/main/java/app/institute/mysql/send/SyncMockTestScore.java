package app.institute.mysql.send;

import java.util.HashMap;
import java.util.Map;

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
import app.institute.helper.Security;
import app.institute.sqlite.SQLiteDB;

import static app.institute.app.Global.ERROR_TAG;
import static app.institute.app.Global.JSON_TAG;
import static app.institute.app.Global.MAX_RETRIES;
import static app.institute.app.Global.RESPONSE_TAG;
import static app.institute.app.Global.TABLE_SCORE;
import static app.institute.app.Global.TIMEOUT;
import static app.institute.app.MyApplication.SECRET_KEY;

public class SyncMockTestScore
{
	private Context context;
	private SQLiteDB helper;
	
	public SyncMockTestScore(Context context)
	{
		this.context = context;
		this.helper = new SQLiteDB(context);
	}


	public void execute(final String user_id)
	{
		/**
		 * Server target URL
		 */
		final String URL = context.getResources().getString(R.string.spectrumServerBaseUrl)
				+ context.getResources().getString(R.string.spectrumServerScoreUrl);

		final StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

			@Override
			public void onResponse(String response)
			{
				try
				{
					Log.v(RESPONSE_TAG, response);

					JSONArray jsonArray = new JSONArray(response);
					JSONObject jsonObject;

					for(int i=0; i<jsonArray.length(); i++)
					{
						jsonObject = jsonArray.getJSONObject(i);
						helper.update(TABLE_SCORE, jsonObject.getInt("id"), jsonObject.getInt("sync_status"));
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
				//listener.onTaskCompleted(false, 500, CONNECTIVITY_ERROR);
			}
		})

		{
			@Override
			protected Map<String, String> getParams()
			{
				Map<String, String> params = new HashMap<>();

				try
				{
					/*JSONObject jsonObject = new JSONObject();

					jsonObject.put(USER_ID, user_id);
					jsonObject.put(MOCK_TEST_ID, mock_test.test_id);
					jsonObject.put(CORRECT, mock_test.count_correct);
					jsonObject.put(WRONG, mock_test.count_wrong);
					jsonObject.put(NOT_ATTEMPT, mock_test.count_not_attempt);
					jsonObject.put(POSITIVE_SCORE, mock_test.question.positive_marks);
					jsonObject.put(NEGATIVE_SCORE, mock_test.question.negative_marks);*/

					params.put(JSON_TAG, Security.encrypt(helper.scoreJSONData(user_id), SECRET_KEY));
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