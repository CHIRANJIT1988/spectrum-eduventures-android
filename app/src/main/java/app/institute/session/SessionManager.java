package app.institute.session;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

import app.institute.RegisterActivity;
import app.institute.model.User;

import static android.accounts.AccountManager.KEY_PASSWORD;
import static app.institute.app.Global.MOBILE_NUMBER;
import static app.institute.app.Global.USER_ID;
import static app.institute.app.Global.USER_NAME;

public class SessionManager 
{
	private SharedPreferences pref; // Shared Preferences
	private Editor editor; // Editor for Shared preferences
	private Context _context; // Context
	private int PRIVATE_MODE = 0; // Shared pref mode

	private static final String PREF_NAME = "SpectrumEduventuresPref"; // Shared Pref file name
	private static final String IS_LOGIN = "IsLoggedIn"; // All Shared Preferences Keys

	@SuppressLint("CommitPrefEdits") 
	public SessionManager(Context context)
	{
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void createLoginSession(User user)
	{
		editor.putBoolean(IS_LOGIN, true); // Storing login value as TRUE

		editor.putString(USER_ID, user.getUserId());
		editor.putString(USER_NAME, user.getName());
		editor.putString(KEY_PASSWORD, user.getPhoneNumber());
		editor.putString(MOBILE_NUMBER, user.getPhoneNumber());
		
		editor.commit(); // commit changes
	}

	public boolean checkLogin()
	{
		
		if(!this.isLoggedIn()) // Check login status
		{
			
			/*Intent i = new Intent(_context, MainActivity.class); // user is not logged in redirect him to Login Activity
			
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Closing all the Activities
			
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add new Flag to start new Activity
			
			_context.startActivity(i); // Staring Login Activity*/
			
			return false;
			
		}
		
		return true;
	}

	/**
	 * Get stored session data
	 */
	public HashMap<String, String> getUserDetails()
	{
		HashMap<String, String> user = new HashMap<>();

		user.put(USER_ID, pref.getString(USER_ID, null));
		user.put(USER_NAME, pref.getString(USER_NAME, null));
		user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
		user.put(MOBILE_NUMBER, pref.getString(MOBILE_NUMBER, null));

		return user;
	}

	public String getUserId()
	{
		return pref.getString(USER_ID, null);
	}

	/**
	 * Clear session details
	 */
	public void logoutUser()
	{
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();
		
		// After logout redirect user to Register Activity
		Intent i = new Intent(_context, RegisterActivity.class);

		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Staring Login Activity
		_context.startActivity(i);
	
	}
	
	/**
	 * Quick check for login
	 */
	public boolean isLoggedIn()
	{
		return pref.getBoolean(IS_LOGIN, false);
	}
}