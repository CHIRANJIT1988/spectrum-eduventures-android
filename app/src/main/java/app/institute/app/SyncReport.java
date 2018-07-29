package app.institute.app;

import android.content.Context;

import app.institute.mysql.send.SyncMockTestScore;
import app.institute.session.SessionManager;
import app.institute.sqlite.SQLiteDB;

import static app.institute.app.Global.TABLE_SCORE;

/**
 * This class is designed to check synchronization
 */
class SyncReport {


    /**
     * Call to this method to check data for sync
     * @param context Pass application context
     */
    static void sync(Context context)
    {
        /**
         * Instantiate SQLiteDatabase
         * Check for synchronization
         * Synchronize Data
         */
        SQLiteDB helper = new SQLiteDB(context);
        SessionManager session = new SessionManager(context);

        if(session.isLoggedIn())
        {
            if(helper.sync_count(TABLE_SCORE) > 0)
            {
                new SyncMockTestScore(context).execute(session.getUserId());
            }
        }
    }
}