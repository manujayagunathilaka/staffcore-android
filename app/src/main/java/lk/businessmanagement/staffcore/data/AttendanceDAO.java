package lk.businessmanagement.staffcore.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import lk.businessmanagement.staffcore.model.Attendance;

public class AttendanceDAO {
    private DatabaseHelper dbHelper;

    public AttendanceDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean markAttendance(Attendance att) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DatabaseHelper.COL_ATT_EMP_ID, att.getEmpId());
        cv.put(DatabaseHelper.COL_ATT_DATE, att.getDate());
        cv.put(DatabaseHelper.COL_ATT_IN_TIME, att.getInTime());
        cv.put(DatabaseHelper.COL_ATT_OUT_TIME, att.getOutTime());
        cv.put(DatabaseHelper.COL_ATT_IS_LEAVE, att.isLeave() ? 1 : 0); // true=1, false=0
        cv.put(DatabaseHelper.COL_ATT_REASON, att.getLeaveReason());

        long result = db.insert(DatabaseHelper.TABLE_ATTENDANCE, null, cv);
        return result != -1;
    }

    public int getTodayPresentCount(String todayDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;
        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_ATTENDANCE +
                " WHERE " + DatabaseHelper.COL_ATT_DATE + " = ? AND " + DatabaseHelper.COL_ATT_IS_LEAVE + " = 0";

        Cursor cursor = db.rawQuery(query, new String[]{todayDate});
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

}