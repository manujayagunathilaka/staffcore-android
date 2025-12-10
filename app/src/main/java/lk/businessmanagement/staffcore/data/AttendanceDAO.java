package lk.businessmanagement.staffcore.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.businessmanagement.staffcore.model.Attendance;
import lk.businessmanagement.staffcore.model.DailyAttendance;

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

    public Attendance getAttendanceByDate(int empId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Attendance att = null;

        String query = "SELECT * FROM " + DatabaseHelper.TABLE_ATTENDANCE +
                " WHERE " + DatabaseHelper.COL_ATT_EMP_ID + " = ? AND " + DatabaseHelper.COL_ATT_DATE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(empId), date});

        if (cursor.moveToFirst()) {
            att = new Attendance();
            att.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ATT_ID)));
            att.setEmpId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ATT_EMP_ID)));
            att.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ATT_DATE)));
            att.setInTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ATT_IN_TIME)));
            att.setOutTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ATT_OUT_TIME)));
            att.setLeave(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ATT_IS_LEAVE)) == 1);
            att.setLeaveReason(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ATT_REASON)));
        }
        cursor.close();
        return att;
    }

    public boolean updateAttendance(Attendance att) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DatabaseHelper.COL_ATT_IN_TIME, att.getInTime());
        cv.put(DatabaseHelper.COL_ATT_OUT_TIME, att.getOutTime());
        cv.put(DatabaseHelper.COL_ATT_IS_LEAVE, att.isLeave() ? 1 : 0);
        cv.put(DatabaseHelper.COL_ATT_REASON, att.getLeaveReason());

        int rows = db.update(DatabaseHelper.TABLE_ATTENDANCE, cv,
                DatabaseHelper.COL_ATT_ID + " = ?", new String[]{String.valueOf(att.getId())});

        return rows > 0;
    }

    public List<Attendance> getHistoryForMonth(int empId, String monthPrefix) {
        List<Attendance> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DatabaseHelper.TABLE_ATTENDANCE +
                " WHERE " + DatabaseHelper.COL_ATT_EMP_ID + " = ? AND " +
                DatabaseHelper.COL_ATT_DATE + " LIKE ? " +
                " ORDER BY " + DatabaseHelper.COL_ATT_DATE + " DESC";

        String[] args = {String.valueOf(empId), monthPrefix + "%"};

        Cursor cursor = db.rawQuery(query, args);

        if (cursor.moveToFirst()) {
            do {
                Attendance att = new Attendance();
                att.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ATT_ID)));
                att.setEmpId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ATT_EMP_ID)));
                att.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ATT_DATE)));
                att.setInTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ATT_IN_TIME)));
                att.setOutTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ATT_OUT_TIME)));
                att.setLeave(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ATT_IS_LEAVE)) == 1);
                att.setLeaveReason(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ATT_REASON)));

                list.add(att);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<DailyAttendance> getDailyAttendanceReport(String date) {
        List<DailyAttendance> reportList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT E." + DatabaseHelper.COL_ID + ", E." + DatabaseHelper.COL_NAME +
                ", E." + DatabaseHelper.COL_PROFILE_PATH + ", E." + DatabaseHelper.COL_JOINED_DATE +
                ", A." + DatabaseHelper.COL_ATT_IN_TIME + ", A." + DatabaseHelper.COL_ATT_OUT_TIME +
                ", A." + DatabaseHelper.COL_ATT_IS_LEAVE +
                " FROM " + DatabaseHelper.TABLE_EMPLOYEES + " E " +
                " LEFT JOIN " + DatabaseHelper.TABLE_ATTENDANCE + " A " +
                " ON E." + DatabaseHelper.COL_ID + " = A." + DatabaseHelper.COL_ATT_EMP_ID +
                " AND A." + DatabaseHelper.COL_ATT_DATE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{date});

        if (cursor.moveToFirst()) {
            do {
                DailyAttendance item = new DailyAttendance();

                item.setEmpId(cursor.getInt(0));
                item.setEmployeeName(cursor.getString(1));
                item.setPhotoPath(cursor.getString(2));

                item.setJoinedDate(cursor.getString(3));

                String inTime = cursor.getString(4);
                String outTime = cursor.getString(5);
                int isLeaveVal = cursor.isNull(6) ? -1 : cursor.getInt(6);

                item.setInTime(inTime);
                item.setOutTime(outTime);

                // Leave Status Logic
                if (isLeaveVal == 1) {
                    item.setLeave(true);
                } else {
                    item.setLeave(false);
                }

                reportList.add(item);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return reportList;
    }
}