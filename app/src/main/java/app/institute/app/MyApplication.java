package app.institute.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import app.institute.model.DailyPracticePaper;
import app.institute.model.MockTest;
import app.institute.model.Topic;
import app.institute.model.Unit;

import static app.institute.app.Global.PREF;

/**
 * Created by Ravi on 13/05/15.
 */

public class MyApplication extends MultiDexApplication {

    public List<MockTest> testPaperList = new ArrayList<>();
    public List<MockTest> testList = new ArrayList<>();
    public List<Unit> unitList = new ArrayList<>();
    public List<Topic> topicList = new ArrayList<>();
    public List<DailyPracticePaper> dppList = new ArrayList<>();

    public static final String TAG = MyApplication.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static MyApplication mInstance;
    public static SharedPreferences prefs;
    public static String SECRET_KEY;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance = this;
        MyApplication.prefs = getSharedPreferences(PREF, MODE_PRIVATE);
        MyApplication.SECRET_KEY = Authentication.auth_key();

        /**
         * Synchronize data
         */
        SyncReport.sync(mInstance);
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized MyApplication getInstance()
    {
        return mInstance;
    }

    public RequestQueue getRequestQueue()
    {
        if (mRequestQueue == null)
        {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag)
    {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag)
    {

        if (mRequestQueue != null)
        {
            mRequestQueue.cancelAll(tag);
        }
    }
}