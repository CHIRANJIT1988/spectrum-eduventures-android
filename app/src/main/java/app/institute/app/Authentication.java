package app.institute.app;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import static app.institute.app.MyApplication.getInstance;

/**
 * This class is deigned to authenticate
 * key
 */

class Authentication
{

    private static final String TAG = "Authentication";

    /**
     * Call to this method to get authentication key
     * @return authentication key
     */
    static String auth_key()
    {

        try
        {
            ApplicationInfo ai = getInstance().getPackageManager().getApplicationInfo(getInstance().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            Log.e(TAG, "Auth Key: " + bundle.getString("spectrum_eduventures.KEY"));
            return bundle.getString("spectrum_eduventures.KEY");
        }

        catch (PackageManager.NameNotFoundException e)
        {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        }
        catch (NullPointerException e)
        {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }

        return null;
    }
}