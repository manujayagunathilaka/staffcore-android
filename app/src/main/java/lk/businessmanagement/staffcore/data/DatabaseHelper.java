package lk.businessmanagement.staffcore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "myshop_v2.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper instance;

    // Table & Columns
    public static final String TABLE_EMPLOYEES = "employees";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_NIC = "nic";
    public static final String COL_GENDER = "gender";
    public static final String COL_DOB = "dob";
    public static final String COL_MARITAL = "marital_status";
    public static final String COL_MOBILE = "mobile_number";
    public static final String COL_HOME = "home_number";
    public static final String COL_ADDRESS = "address";
    public static final String COL_JOINED_DATE = "joined_date";

    // Image Columns
    public static final String COL_PROFILE_PATH = "profile_path";
    public static final String COL_ID_FRONT_PATH = "id_front_path";
    public static final String COL_ID_BACK_PATH = "id_back_path";
    public static final String COL_CV_PATH = "cv_path";

    // Attendance Table Constants
    public static final String TABLE_ATTENDANCE = "attendance";
    public static final String COL_ATT_ID = "att_id";
    public static final String COL_ATT_EMP_ID = "emp_id";
    public static final String COL_ATT_DATE = "date";
    public static final String COL_ATT_IN_TIME = "in_time";
    public static final String COL_ATT_OUT_TIME = "out_time";
    public static final String COL_ATT_IS_LEAVE = "is_leave"; // 0 or 1
    public static final String COL_ATT_REASON = "reason";

    // Advance Table
    public static final String TABLE_ADVANCES = "advances";
    public static final String COL_ADV_ID = "adv_id";
    public static final String COL_ADV_EMP_ID = "emp_id";
    public static final String COL_ADV_DATE = "date";
    public static final String COL_ADV_AMOUNT = "amount";

    private DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_EMPLOYEES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_NIC + " TEXT, " +
                COL_GENDER + " TEXT, " +
                COL_DOB + " TEXT, " +
                COL_MARITAL + " TEXT, " +
                COL_MOBILE + " TEXT, " +
                COL_HOME + " TEXT, " +
                COL_ADDRESS + " TEXT, " +
                COL_JOINED_DATE + " TEXT, " +
                COL_PROFILE_PATH + " TEXT, " +
                COL_ID_FRONT_PATH + " TEXT, " +
                COL_ID_BACK_PATH + " TEXT, " +
                COL_CV_PATH + " TEXT)";

        db.execSQL(createTable);

        String createTableAtt = "CREATE TABLE " + TABLE_ATTENDANCE + " (" +
                COL_ATT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ATT_EMP_ID + " INTEGER, " +
                COL_ATT_DATE + " TEXT, " +
                COL_ATT_IN_TIME + " TEXT, " +
                COL_ATT_OUT_TIME + " TEXT, " +
                COL_ATT_IS_LEAVE + " INTEGER, " +
                COL_ATT_REASON + " TEXT, " +
                "FOREIGN KEY(" + COL_ATT_EMP_ID + ") REFERENCES " + TABLE_EMPLOYEES + "(" + COL_ID + "))";

        db.execSQL(createTableAtt);

        String createTableAdv = "CREATE TABLE " + TABLE_ADVANCES + " (" +
                COL_ADV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ADV_EMP_ID + " INTEGER, " +
                COL_ADV_DATE + " TEXT, " +
                COL_ADV_AMOUNT + " REAL, " +
                "FOREIGN KEY(" + COL_ADV_EMP_ID + ") REFERENCES " + TABLE_EMPLOYEES + "(" + COL_ID + "))";

        db.execSQL(createTableAdv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADVANCES);
        onCreate(db);
    }
}