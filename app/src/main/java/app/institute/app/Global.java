package app.institute.app;

/**
 * Created by CHIRANJIT on 12/6/2016.
 */

public class Global
{
    /**
     * Application Tag
     */
    public static final String JSON_TAG = "responseJSON";
    public static final String ERROR_TAG = "json_error";
    public static final String RESPONSE_TAG = "json_response";
    public static final String CONNECTIVITY_ERROR = "Internet Connection Failure";

    /**
     * Retry Policy
     */
    public static final int MAX_RETRIES = 5;
    public static final int TIMEOUT = 3000;

    /**
     * Preference and JSON Tag Name
     */
    static final String PREF = "spectrum.eduventures.pref";
    public static final String DIRECTORY_NAME = "Spectrum Files";
    public static final String FIRST_RUN = "first_run";
    public static final String KEY = "key";

    public static final String USER_ID = "user_id";
    public static final String USER = "user";
    public static final String NAME = "name";

    public static final String EMAIL = "email";
    public static final String LOCATION = "location";
    public static final String SCHOOL_NAME = "school_name";
    public static final String STATE = "state";
    public static final String GENDER = "gender";
    public static final String DOB = "dob";
    public static final String SCHOOL_DETAILS = "school_details";
    public static final String PROFILE_DETAILS = "profile_details";

    public static final String USER_NAME = "user_name";
    public static final String REG_ID = "reg_id";
    public static final String CODE = "code";
    public static final String MOBILE_NUMBER = "mobile_no";
    public static final String PASSWORD = "password";
    public static final String DEVICE_ID = "device_id";
    public static final String STATUS_CODE = "status_code";
    public static final String MESSAGE = "message";

    public static final String MOCK_TEST_ID = "mock_test_id";
    public static final String MOCK_TEST_NAME = "mock_test_name";
    public static final String CORRECT = "count_correct";
    public static final String WRONG = "count_wrong";
    public static final String NOT_ATTEMPT = "count_not_attempt";
    public static final String POSITIVE_SCORE = "positive_score";
    public static final String NEGATIVE_SCORE = "negative_score";
    public static final String DURATION = "duration";
    public static final String TOTAL_MARKS = "total_marks";
    public static final String TEST_NAME = "test_name";
    public static final String TEST_ID = "test_id";
    public static final String ATTEMPTED_ON = "attempted_on";
    public static final String QUESTION_ID = "question_id";
    public static final String OPTION_ID = "option_id";
    public static final String OPTIONS = "options";
    public static final String OPTION_DETAILS = "option_details";
    public static final String IS_CORRECT = "is_correct";
    public static final String QUESTION = "question";
    public static final String DIAGRAM = "diagram";
    public static final String POSITIVE_MARKS = "positive_marks";
    public static final String NEGATIVE_MARKS = "negative_marks";
    public static final String TOTAL = "total";
    public static final String PAPER_CODE = "paper_code";
    public static final String PAPER_NAME = "paper_name";
    public static final String PAPER_DATE = "paper_date";
    public static final String DAILY_PRACTICE_PAPER = "daily_practice_paper";
    public static final String SYNC_STATUS = "sync_status";

    /**
     * Database Name and Version
     */
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "spectrum.db";
    /**
     * Table Name
     */
    public static final String TABLE_BRANCH = "branch";
    public static final String TABLE_INBOX = "inbox";
    public static final String TABLE_SCORE = "score";
    /**
     * Column Name
     */
    public static final String KEY_ID = "id";
    public static final String KEY_UNIT_ID = "unit_id";
    public static final String KEY_UNIT_NAME = "unit_name";
    public static final String KEY_TOPIC_ID = "topic_id";
    public static final String KEY_TOPIC_NAME = "topic_name";
    public static final String KEY_BRANCH_CODE = "branch_code";
    public static final String KEY_BRANCH_NAME = "branch_name";
    public static final String KEY_SUBJECT_CODE = "subject_code";
    public static final String KEY_SUBJECT_NAME = "subject_name";
    public static final String KEY_CLASS_CODE = "class_code";
    public static final String KEY_CLASS_NAME = "class_name";
    public static final String KEY_READ_STATUS = "read_status";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_MESSAGE = "message";
}