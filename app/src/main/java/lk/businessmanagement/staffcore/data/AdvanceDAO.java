package lk.businessmanagement.staffcore.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import lk.businessmanagement.staffcore.model.Advance;

public class AdvanceDAO {
    private DatabaseHelper dbHelper;

    public AdvanceDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean addAdvance(Advance advance) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_ADV_EMP_ID, advance.getEmpId());
        cv.put(DatabaseHelper.COL_ADV_DATE, advance.getDate());
        cv.put(DatabaseHelper.COL_ADV_AMOUNT, advance.getAmount());

        long result = db.insert(DatabaseHelper.TABLE_ADVANCES, null, cv);
        return result != -1;
    }

    public double getTotalAdvanceForEmployee(int empId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;

        // SQL: SELECT SUM(amount) FROM advances WHERE emp_id = ?
        String query = "SELECT SUM(" + DatabaseHelper.COL_ADV_AMOUNT + ") FROM " + DatabaseHelper.TABLE_ADVANCES +
                " WHERE " + DatabaseHelper.COL_ADV_EMP_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(empId)});
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public double getAllTimeTotalAdvance() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(" + DatabaseHelper.COL_ADV_AMOUNT + ") FROM " + DatabaseHelper.TABLE_ADVANCES, null);
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

}