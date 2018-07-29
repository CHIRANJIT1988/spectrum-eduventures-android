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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.institute.R;
import app.institute.app.MyApplication;
import app.institute.helper.OnTaskCompleted;
import app.institute.helper.Security;
import app.institute.model.MockTest;
import app.institute.model.Option;
import app.institute.model.Question;
import app.institute.session.SessionManager;

import static app.institute.app.Global.CONNECTIVITY_ERROR;
import static app.institute.app.Global.DIAGRAM;
import static app.institute.app.Global.DURATION;
import static app.institute.app.Global.ERROR_TAG;
import static app.institute.app.Global.IS_CORRECT;
import static app.institute.app.Global.JSON_TAG;
import static app.institute.app.Global.KEY;
import static app.institute.app.Global.MAX_RETRIES;
import static app.institute.app.Global.MOCK_TEST_ID;
import static app.institute.app.Global.NEGATIVE_MARKS;
import static app.institute.app.Global.OPTIONS;
import static app.institute.app.Global.OPTION_DETAILS;
import static app.institute.app.Global.OPTION_ID;
import static app.institute.app.Global.POSITIVE_MARKS;
import static app.institute.app.Global.QUESTION;
import static app.institute.app.Global.QUESTION_ID;
import static app.institute.app.Global.RESPONSE_TAG;
import static app.institute.app.Global.TEST_ID;
import static app.institute.app.Global.TIMEOUT;
import static app.institute.app.Global.TOTAL_MARKS;
import static app.institute.app.Global.USER_ID;
import static app.institute.app.MyApplication.SECRET_KEY;
import static app.institute.app.MyApplication.getInstance;
import static app.institute.app.MyApplication.prefs;


public class ReceiveTest
{
	private OnTaskCompleted listener;
	private Context context;

	public ReceiveTest(Context context , OnTaskCompleted listener)
	{
		this.listener = listener;
		this.context = context;
	}

	public void execute(final int test_id)
	{
		/**
		 * Server target URL
		 */
		final String URL = context.getResources().getString(R.string.spectrumServerBaseUrl)
				+ context.getResources().getString(R.string.spectrumServerTestUrl);

		final StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

			@Override
			public void onResponse(String response)
			{
				try
				{
					Log.v(RESPONSE_TAG, response);

					String json_data = Security.decrypt(response, prefs.getString(KEY, ""));

					JSONArray arr = new JSONArray(json_data);
					JSONObject jsonObj;

					if(arr.length() > 0)
					{
						for (int i = 0; i < arr.length(); i++)
						{
							jsonObj = arr.getJSONObject(i);

							int mock_test_id = jsonObj.getInt(MOCK_TEST_ID);
							int question_id = jsonObj.getInt(QUESTION_ID);
							String question = jsonObj.getString(QUESTION);
							String diagram = jsonObj.getString(DIAGRAM);
							int total_marks = jsonObj.getInt(TOTAL_MARKS);
							int positive_marks = jsonObj.getInt(POSITIVE_MARKS);
							int negative_marks = jsonObj.getInt(NEGATIVE_MARKS);
							int duration = jsonObj.getInt(DURATION);


							JSONArray option_array = new JSONArray(jsonObj.getString(OPTIONS));

							List<Option> optionList = new ArrayList<>();

							for (int j = 0; j < option_array.length(); j++)
							{

								jsonObj = (JSONObject) option_array.get(j);

								int option_id = jsonObj.getInt(OPTION_ID);
								String option = jsonObj.getString(OPTION_DETAILS);
								int is_correct = jsonObj.getInt(IS_CORRECT);

								optionList.add(new Option(option_id, option, is_correct));

								Log.v(OPTION_DETAILS, option);
							}

							Question questionObj = new Question(question_id, question, diagram, positive_marks, negative_marks, optionList);
							getInstance().testList.add(new MockTest(mock_test_id, total_marks, duration, questionObj));
						}

						listener.onTaskCompleted(true, 200, "success");
						return;
					}

					listener.onTaskCompleted(false, 200, "Questions Not Available");
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
					params.put(TEST_ID, Security.encrypt(String.valueOf(test_id), SECRET_KEY));
					params.put(USER_ID, Security.encrypt(new SessionManager(context).getUserId(), SECRET_KEY));
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