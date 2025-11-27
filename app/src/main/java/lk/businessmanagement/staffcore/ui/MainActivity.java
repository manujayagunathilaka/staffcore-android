package lk.businessmanagement.staffcore.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.data.AttendanceDAO;
import lk.businessmanagement.staffcore.data.EmployeeDAO;
import lk.businessmanagement.staffcore.utils.AnimationHelper;

public class MainActivity extends AppCompatActivity {

    // --- UI Components ---

    // Stats Texts
    private TextView tvCountStaff, tvCountPresent;

    // Stats Progress Bars (Add IDs to XML: pbTotalStaff, pbPresentToday)
    private ProgressBar pbTotalStaff, pbPresentToday;

    // Header Components
    private CircleImageView imgProfile;
    private ImageView btnNotification;

    // QUICK ACTION GRID BLOCKS
    private LinearLayout btnActionAdd, btnActionMark, btnActionReport, btnActionAllStaff;

    // BOTTOM NAVIGATION ITEMS
    private LinearLayout navAttendance, navHistory, navAdd, navAllStaff, navProfile;

    // Background Animation Views
    private ImageView glowTop, glowBottom;

    // --- Database Helpers ---
    private EmployeeDAO empDao;
    private AttendanceDAO attDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize Views & DB
        initViews();
        empDao = new EmployeeDAO(this);
        attDao = new AttendanceDAO(this);

        // 2. Start Background Animation
        AnimationHelper.animateBackground(glowTop, glowBottom);

        // 3. Setup Click Listeners
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh dashboard data every time the screen becomes visible
        updateDashboardStats();
    }

    /**
     * Connects Java objects to XML IDs
     */
    private void initViews() {
        // Stats
        tvCountStaff = findViewById(R.id.tvCountStaff);
        tvCountPresent = findViewById(R.id.tvCountPresent);

        // Progress Bars (Make sure you added these IDs to your XML!)
        pbTotalStaff = findViewById(R.id.pbTotalStaff);     // Add android:id="@+id/pbTotalStaff" to XML
        pbPresentToday = findViewById(R.id.pbPresentToday); // Add android:id="@+id/pbPresentToday" to XML

        // Header
        imgProfile = findViewById(R.id.imgProfile);
        btnNotification = findViewById(R.id.btnNotification);

        // Quick Action Grid
        btnActionAdd = findViewById(R.id.btnActionAdd);
        btnActionMark = findViewById(R.id.btnActionMark);
        btnActionReport = findViewById(R.id.btnActionReport);
        btnActionAllStaff = findViewById(R.id.btnActionAllStaff);

        // Bottom Navigation
        navAttendance = findViewById(R.id.navAttendance); // Daily
        navHistory = findViewById(R.id.navHistory);       // History
        navAdd = findViewById(R.id.navAdd);               // Center FAB
        navAllStaff = findViewById(R.id.navAllStaff);     // Staff List
        navProfile = findViewById(R.id.navProfile);       // Profile

        // Animation Glows
        glowTop = findViewById(R.id.glowTop);
        glowBottom = findViewById(R.id.glowBottom);
    }

    /**
     * Logic for all Buttons
     */
    private void setupClickListeners() {

        // --- Notification ---
        btnNotification.setOnClickListener(v ->
                Toast.makeText(this, "No new notifications", Toast.LENGTH_SHORT).show()
        );

        // --- QUICK ACTIONS ---

        // 1. Add Staff
        btnActionAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEmployeeActivity.class);
            startActivity(intent);
        });

        // 2. Attendance (Mark)
        btnActionMark.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmployeeListActivity.class);
            intent.putExtra("IS_ATTENDANCE_MODE", true); // Enable selection mode
            startActivity(intent);
        });

        // 3. Daily Report
        btnActionReport.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DailyAttendanceActivity.class);
            startActivity(intent);
        });

        // 4. All Staff
        btnActionAllStaff.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmployeeListActivity.class);
            intent.putExtra("IS_ATTENDANCE_MODE", false); // Normal mode
            startActivity(intent);
        });


        // --- BOTTOM NAVIGATION ---

        // Daily (Calendar)
        navAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DailyAttendanceActivity.class);
            startActivity(intent);
        });

        // History
        navHistory.setOnClickListener(v -> {
            // Navigate to employee list to check individual history
            Intent intent = new Intent(MainActivity.this, EmployeeListActivity.class);
            intent.putExtra("IS_ATTENDANCE_MODE", false);
            startActivity(intent);
        });

        // Center Add
        navAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEmployeeActivity.class);
            startActivity(intent);
        });

        // Staff List
        navAllStaff.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmployeeListActivity.class);
            intent.putExtra("IS_ATTENDANCE_MODE", false);
            startActivity(intent);
        });

        // Profile
        navProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Admin Profile Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Fetch Data & Update UI with Animation
     */
    private void updateDashboardStats() {
        // 1. Total Staff Logic
        int totalStaff = empDao.getEmployeeCount();
        tvCountStaff.setText(totalStaff + " Employees");

        // Animate Staff Progress (Just a visual effect, e.g., 70% capacity)
        // You can change 70 to any target value or calculate based on a 'Max Capacity'
        animateProgressBar(pbTotalStaff, 70);

        // 2. Present Today Logic
        Calendar c = Calendar.getInstance();
        String today = String.format(Locale.getDefault(), "%d-%02d-%02d",
                c.get(Calendar.YEAR), (c.get(Calendar.MONTH) + 1), c.get(Calendar.DAY_OF_MONTH));

        int presentCount = attDao.getTodayPresentCount(today);

        // Text: "2 / 5"
        tvCountPresent.setText(presentCount + " / " + totalStaff);

        // Calculate Percentage for Progress Bar
        int attendancePercentage = 0;
        if (totalStaff > 0) {
            attendancePercentage = (presentCount * 100) / totalStaff;
        }

        // Animate Present Progress
        animateProgressBar(pbPresentToday, attendancePercentage);
    }

    /**
     * Smoothly animates a progress bar from 0 to target value
     */
    private void animateProgressBar(ProgressBar progressBar, int targetProgress) {
        if (progressBar == null) return;

        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, targetProgress);
        animation.setDuration(1000); // 1 second animation
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }
}