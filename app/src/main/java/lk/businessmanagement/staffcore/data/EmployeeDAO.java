package lk.businessmanagement.staffcore.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.businessmanagement.staffcore.model.Employee;

public class EmployeeDAO {

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public EmployeeDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean addEmployee(Employee emp) {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DatabaseHelper.COL_NAME, emp.getName());
        cv.put(DatabaseHelper.COL_NIC, emp.getNic());
        cv.put(DatabaseHelper.COL_GENDER, emp.getGender());
        cv.put(DatabaseHelper.COL_DOB, emp.getDob());
        cv.put(DatabaseHelper.COL_MARITAL, emp.getMaritalStatus());
        cv.put(DatabaseHelper.COL_MOBILE, emp.getMobileNumber());
        cv.put(DatabaseHelper.COL_HOME, emp.getHomeNumber());
        cv.put(DatabaseHelper.COL_ADDRESS, emp.getAddress());
        cv.put(DatabaseHelper.COL_JOINED_DATE, emp.getJoinedDate());

        // Images
        cv.put(DatabaseHelper.COL_PROFILE_PATH, emp.getProfilePhotoPath());
        cv.put(DatabaseHelper.COL_ID_FRONT_PATH, emp.getIdFrontPath());
        cv.put(DatabaseHelper.COL_ID_BACK_PATH, emp.getIdBackPath());
        cv.put(DatabaseHelper.COL_CV_PATH, emp.getCvPath());

        long result = db.insert(DatabaseHelper.TABLE_EMPLOYEES, null, cv);
        return result != -1;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employeeList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query: SELECT * FROM employees ORDER BY id DESC
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_EMPLOYEES + " ORDER BY " + DatabaseHelper.COL_ID + " DESC";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Employee emp = new Employee();
                // Column Index එක හොයාගෙන Data set කරනවා
                emp.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
                emp.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
                emp.setNic(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NIC)));
                emp.setMobileNumber(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOBILE)));
                emp.setProfilePhotoPath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROFILE_PATH)));

                employeeList.add(emp);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return employeeList;
    }

    public Employee getEmployeeById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Employee emp = null;

        // Query: SELECT * FROM employees WHERE id = ?
        Cursor cursor = db.query(DatabaseHelper.TABLE_EMPLOYEES, null,
                DatabaseHelper.COL_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            emp = new Employee();
            emp.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
            emp.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
            emp.setNic(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NIC)));
            emp.setGender(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GENDER)));
            emp.setDob(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DOB)));
            emp.setMaritalStatus(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MARITAL)));
            emp.setMobileNumber(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOBILE)));
            emp.setHomeNumber(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_HOME)));
            emp.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ADDRESS)));
            emp.setJoinedDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_JOINED_DATE)));

            // Images Paths
            emp.setProfilePhotoPath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROFILE_PATH)));
            emp.setIdFrontPath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID_FRONT_PATH)));
            emp.setIdBackPath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID_BACK_PATH)));
            emp.setCvPath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CV_PATH)));

            cursor.close();
        }
        return emp;
    }

    public int getEmployeeCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_EMPLOYEES, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

}