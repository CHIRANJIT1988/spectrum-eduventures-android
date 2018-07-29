package app.institute.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.institute.model.Branch;
import app.institute.model.Class;
import app.institute.model.Message;
import app.institute.model.MockTest;
import app.institute.model.Question;
import app.institute.model.Subject;


import static app.institute.app.Global.CORRECT;
import static app.institute.app.Global.DATABASE_NAME;
import static app.institute.app.Global.DATABASE_VERSION;
import static app.institute.app.Global.DURATION;
import static app.institute.app.Global.KEY_BRANCH_CODE;
import static app.institute.app.Global.KEY_BRANCH_NAME;
import static app.institute.app.Global.KEY_CLASS_CODE;
import static app.institute.app.Global.KEY_CLASS_NAME;
import static app.institute.app.Global.KEY_ID;
import static app.institute.app.Global.KEY_MESSAGE;
import static app.institute.app.Global.KEY_READ_STATUS;
import static app.institute.app.Global.KEY_SUBJECT_CODE;
import static app.institute.app.Global.KEY_SUBJECT_NAME;
import static app.institute.app.Global.KEY_TIMESTAMP;
import static app.institute.app.Global.MOCK_TEST_ID;
import static app.institute.app.Global.MOCK_TEST_NAME;
import static app.institute.app.Global.NEGATIVE_SCORE;
import static app.institute.app.Global.NOT_ATTEMPT;
import static app.institute.app.Global.POSITIVE_SCORE;
import static app.institute.app.Global.SYNC_STATUS;
import static app.institute.app.Global.TABLE_BRANCH;
import static app.institute.app.Global.TABLE_INBOX;
import static app.institute.app.Global.TABLE_SCORE;
import static app.institute.app.Global.TOTAL_MARKS;
import static app.institute.app.Global.USER_ID;
import static app.institute.app.Global.WRONG;

/**
 * Created by CHIRANJIT on 7/24/2016.
 */
public class SQLiteDB extends SQLiteOpenHelper
{
    private Context context;

    /**
     * Branch table create statement
     */
    private static final String CREATE_TABLE_BRANCH = "CREATE TABLE "
            + TABLE_BRANCH + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_BRANCH_CODE + " INTEGER," + KEY_BRANCH_NAME + " TEXT,"
            + KEY_SUBJECT_CODE + " INTEGER," + KEY_SUBJECT_NAME + " TEXT," + KEY_CLASS_CODE + " INTEGER," + KEY_CLASS_NAME + " TEXT)";

    /**
     * Inbox table create statement
     */
   private static final String CREATE_TABLE_INBOX = "CREATE TABLE "
            + TABLE_INBOX + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_MESSAGE + " TEXT, " + KEY_TIMESTAMP + " TEXT," + KEY_READ_STATUS + " INTEGER DEFAULT 0)" ;

    /**
     * Score table create statement
     */
    private static final String CREATE_TABLE_SCORE = "CREATE TABLE "
            + TABLE_SCORE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + MOCK_TEST_ID + " INTEGER, " + MOCK_TEST_NAME + " TEXT," + TOTAL_MARKS + " INTEGER,"
            + DURATION + " INTEGER," + CORRECT + " INTEGER," + WRONG + " INTEGER," + NOT_ATTEMPT + " INTEGER," + POSITIVE_SCORE + " INTEGER," + NEGATIVE_SCORE + " INTEGER,"
            + KEY_TIMESTAMP + " TEXT," + SYNC_STATUS + " INTEGER DEFAULT 0)";


    public SQLiteDB(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase database)
    {

        database.execSQL(CREATE_TABLE_BRANCH);
        database.execSQL(CREATE_TABLE_INBOX);
        database.execSQL(CREATE_TABLE_SCORE);

        Log.v("CREATE TABLE: ", "Inside onCreate()");
    }


    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version)
    {

        database.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANCH);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_INBOX);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE);
        onCreate(database);
        Log.v("UPGRADE TABLE: ", "Inside onUpgrade()");
    }


    public boolean insert(Message message)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_MESSAGE, message.message);
        values.put(KEY_TIMESTAMP, message.timestamp);

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_INBOX, null, values) > 0;

        Log.v("createSuccessful ", String.valueOf(createSuccessful));
        Log.v("createSuccessful ", String.valueOf(message.message));

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public boolean insert(MockTest test, MockTest result)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(MOCK_TEST_ID, test.test_id);
        values.put(MOCK_TEST_NAME, test.test_name);
        values.put(TOTAL_MARKS, test.total_marks);
        values.put(DURATION, test.duration);
        values.put(CORRECT, result.count_correct);
        values.put(WRONG, result.count_wrong);
        values.put(NOT_ATTEMPT, result.count_not_attempt);
        values.put(POSITIVE_SCORE, result.question.positive_marks);
        values.put(NEGATIVE_SCORE, result.question.negative_marks);
        values.put(KEY_TIMESTAMP, result.attempted_on);

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_SCORE, null, values) > 0;

        Log.v("createSuccessful ", String.valueOf(createSuccessful));

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public boolean insert(Branch branch)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_BRANCH_CODE, branch.branch_code);
        values.put(KEY_BRANCH_NAME, branch.branch_name);
        values.put(KEY_SUBJECT_CODE, branch._subject.subject_code);
        values.put(KEY_SUBJECT_NAME, branch._subject.subject_name);
        values.put(KEY_CLASS_CODE, branch._class.class_code);
        values.put(KEY_CLASS_NAME, branch._class.class_name);

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_BRANCH, null, values) > 0;

        Log.v("createSuccessful", " " + createSuccessful );

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public void getAllBranch()
    {

        String selectQuery = "SELECT DISTINCT " + KEY_BRANCH_CODE + "," + KEY_BRANCH_NAME + " FROM " + TABLE_BRANCH
                + " ORDER BY " + KEY_BRANCH_NAME + " ASC";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {

            Branch.list.clear();

            do
            {

                Branch branch = new Branch();

                branch.branch_code = cursor.getString(0);
                branch.branch_name = cursor.getString(1);

                Branch.list.add(branch);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();
    }


    public List<Class> getAllClass(String branch_code, String subject_code)
    {

        List<Class> list = new ArrayList<>();

        String selectQuery = "SELECT DISTINCT " + KEY_CLASS_CODE + "," + KEY_CLASS_NAME + " FROM " + TABLE_BRANCH
                + " WHERE " + KEY_BRANCH_CODE + "='" + branch_code + "' AND " + KEY_SUBJECT_CODE + "='" + subject_code
                + "' ORDER BY " + KEY_SUBJECT_NAME + " DESC";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                Class _class = new Class();

                _class.class_code = cursor.getString(0);
                _class.class_name = cursor.getString(1);

                list.add(_class);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return list;
    }


    public List<Subject> getAllSubject(String branch_code)
    {

        List<Subject> list = new ArrayList<>();

        String selectQuery = "SELECT DISTINCT " + KEY_SUBJECT_CODE + "," + KEY_SUBJECT_NAME + " FROM " + TABLE_BRANCH
                + " WHERE " + KEY_BRANCH_CODE + "='" + branch_code + "'" + " ORDER BY " + KEY_SUBJECT_NAME + " ASC";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                Subject subject = new Subject();

                subject.subject_code = cursor.getString(0);
                subject.subject_name = cursor.getString(1);

                list.add(subject);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return list;
    }


    public ArrayList<Message> getAllMessage(int x, int y)
    {

        ArrayList<Message> messagesList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_INBOX + " ORDER BY " + KEY_ID + " DESC LIMIT " + x + "," + y;

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                Message message = new Message();

                message.message_id = cursor.getInt(0);
                message.message = cursor.getString(1);
                message.timestamp = cursor.getString(2);
                message.read_status = cursor.getInt(3);

                messagesList.add(message);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return messagesList;
    }


    public ArrayList<MockTest> getAllScore(int x, int y)
    {

        ArrayList<MockTest> scoreList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_SCORE + " ORDER BY " + KEY_ID + " DESC LIMIT " + x + "," + y;

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                MockTest score = new MockTest();

                score.test_id = cursor.getInt(1);
                score.test_name = cursor.getString(2);
                score.total_marks = cursor.getInt(3);
                score.duration = cursor.getInt(4);
                score.count_correct = cursor.getInt(5);
                score.count_wrong = cursor.getInt(6);
                score.count_not_attempt = cursor.getInt(7);
                score.question = new Question(cursor.getInt(8), cursor.getInt(9));
                score.attempted_on = cursor.getString(10);

                scoreList.add(score);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return scoreList;
    }


    public String scoreJSONData(String user_id)
    {

        ArrayList<HashMap<String, String>> wordList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_SCORE + " WHERE " + SYNC_STATUS + " = '0'";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                HashMap<String, String> map = new HashMap<>();

                map.put(KEY_ID, cursor.getString(0));
                map.put(MOCK_TEST_ID, cursor.getString(1));
                map.put(MOCK_TEST_NAME, cursor.getString(2));
                map.put(TOTAL_MARKS, cursor.getString(3));
                map.put(DURATION, cursor.getString(4));
                map.put(CORRECT, cursor.getString(5));
                map.put(WRONG, cursor.getString(6));
                map.put(NOT_ATTEMPT, cursor.getString(7));
                map.put(POSITIVE_SCORE, cursor.getString(8));
                map.put(NEGATIVE_SCORE, cursor.getString(9));
                map.put(KEY_TIMESTAMP, cursor.getString(10));
                map.put(USER_ID, user_id);

                wordList.add(map);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        Gson gson = new GsonBuilder().create();

        // Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }


    public int dbRowCount(String TABLE_NAME)
    {
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }


    public int setAsRead()
    {
        String selectQuery = "UPDATE " + TABLE_INBOX + " SET " + KEY_READ_STATUS + "='1' WHERE " + KEY_READ_STATUS + "='0'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }

    public int update(String table, int id, int status)
    {
        String selectQuery = "UPDATE " + table + " SET " + SYNC_STATUS + "='" + status + "' WHERE " + KEY_ID + "='" + id + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }

    /*public void delete(int message_id)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_INBOX + " WHERE " + KEY_ID + " ='" + message_id + "'";
        Log.d("query", deleteQuery);
        database.execSQL("PRAGMA foreign_keys=ON");
        database.execSQL(deleteQuery);
        database.close();
    }*/

    public void deleteAllRow(String TABLE_NAME)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_NAME;
        Log.d("query", deleteQuery);
        database.execSQL("PRAGMA foreign_keys=ON");
        database.execSQL(deleteQuery);
        database.close();
    }

    public int unreadMessageCount()
    {
        String selectQuery = "SELECT * FROM " + TABLE_INBOX + " WHERE " + KEY_READ_STATUS + "='0'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }

    public int sync_count(String table)
    {
        String selectQuery = "SELECT * FROM " + table + " WHERE " + SYNC_STATUS + "='0'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }
}