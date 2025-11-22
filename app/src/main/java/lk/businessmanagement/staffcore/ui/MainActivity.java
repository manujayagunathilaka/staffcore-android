package lk.businessmanagement.staffcore.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Locale;

import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.data.AttendanceDAO;
import lk.businessmanagement.staffcore.data.EmployeeDAO;
import lk.businessmanagement.staffcore.utils.AnimationHelper;

public class MainActivity extends AppCompatActivity {

    // UI Components
    private TextView tvCountStaff, tvCountPresent;
    private MaterialButton btnViewStaff, btnAddStaff;
    private CardView cardAttendance;
    private ImageView glowTop, glowBottom;
    private EmployeeDAO empDao;
    private AttendanceDAO attDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        empDao = new EmployeeDAO(this);
        attDao = new AttendanceDAO(this);

        AnimationHelper.animateBackground(glowTop, glowBottom);

        btnAddStaff.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEmployeeActivity.class);
            startActivity(intent);
        });

        cardAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmployeeListActivity.class);
            intent.putExtra("IS_ATTENDANCE_MODE", true);
            startActivity(intent);
        });

        btnViewStaff.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmployeeListActivity.class);
            intent.putExtra("IS_ATTENDANCE_MODE", false);
            startActivity(intent);
        });

    }

    /**
     * Initializes all UI elements from the XML layout
     */
    private void initViews() {
        // Stats TextViews
        tvCountStaff = findViewById(R.id.tvCountStaff);
        tvCountPresent = findViewById(R.id.tvCountPresent);

        // Action Buttons
        btnViewStaff = findViewById(R.id.btnViewStaff);
        btnAddStaff = findViewById(R.id.btnAddStaff);
        cardAttendance = findViewById(R.id.cardAttendance);

        // Background Images (from included layout)
        glowTop = findViewById(R.id.glowTop);
        glowBottom = findViewById(R.id.glowBottom);
    }

    /**
     * onResume is called when the user returns to this screen.
     * We use this to refresh the dashboard numbers (e.g., after adding a new employee).
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateDashboardStats();
    }

    /**
     * Fetches latest data from Database and updates the Dashboard UI
     */
    private void updateDashboardStats() {
        // 1. Get Total Employee Count
        int count = empDao.getEmployeeCount();
        tvCountStaff.setText(String.valueOf(count));

        // 2. Get Today's Present Count
        // Generate today's date string (YYYY-MM-DD)
        Calendar c = Calendar.getInstance();
        String today = String.format(Locale.getDefault(), "%d-%02d-%02d",
                c.get(Calendar.YEAR), (c.get(Calendar.MONTH) + 1), c.get(Calendar.DAY_OF_MONTH));

        // Fetch count from Database
        int present = attDao.getTodayPresentCount(today);
        tvCountPresent.setText(String.valueOf(present));
    }
}