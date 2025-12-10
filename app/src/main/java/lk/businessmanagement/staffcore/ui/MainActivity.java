package lk.businessmanagement.staffcore.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

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
    private TextView tvCountStaff, tvCountPresent, tvGreeting;

    // Stats Progress Bars
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
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        initViews();
        empDao = new EmployeeDAO(this);
        attDao = new AttendanceDAO(this);

        // 3. Fix System Bar Overlaps (Padding for Status/Nav bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainRoot), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, insets.bottom);
            return windowInsets;
        });

        AnimationHelper.animateBackground(glowTop, glowBottom);

        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh dashboard data & Greeting every time screen is visible
        updateDashboardStats();
    }

    private void initViews() {
        // Stats
        tvCountStaff = findViewById(R.id.tvCountStaff);
        tvCountPresent = findViewById(R.id.tvCountPresent);

        // Greeting Text
        tvGreeting = findViewById(R.id.tvGreeting);

        // Progress Bars
        pbTotalStaff = findViewById(R.id.pbTotalStaff);
        pbPresentToday = findViewById(R.id.pbPresentToday);

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

    private void setupClickListeners() {

        // --- Notification ---
        btnNotification.setOnClickListener(v ->
                Toast.makeText(this, "No new notifications", Toast.LENGTH_SHORT).show()
        );

        // --- QUICK ACTIONS (GRID) ---

        // 1. Add Staff
        btnActionAdd.setOnClickListener(v -> animateButton(v, () -> {
            Intent intent = new Intent(MainActivity.this, AddEmployeeActivity.class);
            startActivity(intent);
        }));

        // 2. Attendance (Mark)
        btnActionMark.setOnClickListener(v -> animateButton(v, () -> {
            Intent intent = new Intent(MainActivity.this, EmployeeListActivity.class);
            intent.putExtra("IS_ATTENDANCE_MODE", true); // Enable selection mode
            startActivity(intent);
        }));

        // 3. Daily Report
        btnActionReport.setOnClickListener(v -> animateButton(v, () -> {
            Intent intent = new Intent(MainActivity.this, DailyAttendanceActivity.class);
            startActivity(intent);
        }));

        // 4. All Staff
        btnActionAllStaff.setOnClickListener(v -> animateButton(v, () -> {
            Intent intent = new Intent(MainActivity.this, EmployeeListActivity.class);
            intent.putExtra("IS_ATTENDANCE_MODE", false); // Normal mode
            startActivity(intent);
        }));


        // --- BOTTOM NAVIGATION ---

        // Daily (Calendar Icon) -> Daily Report
        navAttendance.setOnClickListener(v -> animateButton(v, () -> {
            Intent intent = new Intent(MainActivity.this, DailyAttendanceActivity.class);
            startActivity(intent);
        }));

        // History (Clock Icon) -> යන්න ඕන Calendar එකට (ඔයා ඉල්ලපු විදිහට)
        navHistory.setOnClickListener(v -> animateButton(v, () -> {
            // History බැලීමට Daily Report (Calendar) එකට යවමු
            Intent intent = new Intent(MainActivity.this, DailyAttendanceActivity.class);
            startActivity(intent);
        }));

        // Center Add
        navAdd.setOnClickListener(v -> animateButton(v, () -> {
            Intent intent = new Intent(MainActivity.this, AddEmployeeActivity.class);
            startActivity(intent);
        }));

        // Staff List (Person Icon) -> Employee List එකට
        navAllStaff.setOnClickListener(v -> animateButton(v, () -> {
            Intent intent = new Intent(MainActivity.this, EmployeeListActivity.class);
            intent.putExtra("IS_ATTENDANCE_MODE", false);
            startActivity(intent);
        }));

        // Profile
        navProfile.setOnClickListener(v -> animateButton(v, () -> {
            Toast.makeText(this, "Admin Profile Coming Soon", Toast.LENGTH_SHORT).show();
        }));
    }

    /**
     * Fetch Data & Update UI (Greeting + Stats)
     */
    private void updateDashboardStats() {
        // 1. Set Dynamic Greeting ☀️🌙
        setDynamicGreeting();

        // 2. Total Staff Logic
        int totalStaff = empDao.getEmployeeCount();
        tvCountStaff.setText(totalStaff + " Employees");
        animateProgressBar(pbTotalStaff, 70); // Visual fill

        // 3. Present Today Logic
        Calendar c = Calendar.getInstance();
        String today = String.format(Locale.getDefault(), "%d-%02d-%02d",
                c.get(Calendar.YEAR), (c.get(Calendar.MONTH) + 1), c.get(Calendar.DAY_OF_MONTH));

        int presentCount = attDao.getTodayPresentCount(today);
        tvCountPresent.setText(presentCount + " / " + totalStaff);

        // Calculate Percentage
        int attendancePercentage = 0;
        if (totalStaff > 0) {
            attendancePercentage = (presentCount * 100) / totalStaff;
        }
        animateProgressBar(pbPresentToday, attendancePercentage);
    }

    /**
     * Set Greeting based on Time of Day
     */
    private void setDynamicGreeting() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        String greeting;

        if (hour >= 0 && hour < 12) {
            greeting = "Good Morning,";
        } else if (hour >= 12 && hour < 17) {
            greeting = "Good Afternoon,";
        } else {
            greeting = "Good Evening,";
        }

        if (tvGreeting != null) {
            tvGreeting.setText(greeting);
        }
    }

    private void animateProgressBar(ProgressBar progressBar, int targetProgress) {
        if (progressBar == null) return;
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, targetProgress);
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void animateButton(View view, Runnable action) {
        view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
            view.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(action).start();
        }).start();
    }
}