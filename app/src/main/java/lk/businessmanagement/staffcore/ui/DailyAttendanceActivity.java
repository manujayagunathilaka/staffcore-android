package lk.businessmanagement.staffcore.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.data.AttendanceDAO;
import lk.businessmanagement.staffcore.model.DailyAttendance;
import lk.businessmanagement.staffcore.ui.adapters.CalendarAdapter;
import lk.businessmanagement.staffcore.ui.adapters.DailyReportAdapter;
import lk.businessmanagement.staffcore.utils.AnimationHelper;

public class DailyAttendanceActivity extends AppCompatActivity {

    // UI Components
    private TextView tvMonthName, tvTotal, tvPresent, tvAbsent;
    private RecyclerView recyclerCalendar, recyclerDaily;
    private ImageView btnBack, btnPrevMonth, btnNextMonth, glowTop, glowBottom;

    // Data & Helpers
    private AttendanceDAO dao;
    private Calendar currentCalendar; // Tracks the currently viewed month
    private Date selectedDate;        // Tracks the specifically selected day
    private CalendarAdapter calendarAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_attendance);

        // 1. Initialize UI and Animation
        initViews();
        AnimationHelper.animateBackground(glowTop, glowBottom);

        // 2. Initialize Data Objects
        dao = new AttendanceDAO(this);
        currentCalendar = Calendar.getInstance(); // Starts with today
        selectedDate = currentCalendar.getTime();

        // 3. Setup List RecyclerView (Bottom List)
        recyclerDaily.setLayoutManager(new LinearLayoutManager(this));

        // 4. Setup Calendar RecyclerView (Top Grid)
        // Use GridLayoutManager with span count 7 (for 7 days a week)
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 7);
        recyclerCalendar.setLayoutManager(gridLayoutManager);

        // 5. Initial Data Load
        updateCalendarGrid(); // Build the calendar
        loadAttendanceData(selectedDate); // Load list for today

        // 6. Setup Click Listeners
        setupListeners();
    }

    private void initViews() {
        tvMonthName = findViewById(R.id.tvMonthName);
        tvTotal = findViewById(R.id.tvTotal);
        tvPresent = findViewById(R.id.tvPresent);
        tvAbsent = findViewById(R.id.tvAbsent);

        recyclerCalendar = findViewById(R.id.recyclerCalendar);
        recyclerDaily = findViewById(R.id.recyclerDaily);

        btnBack = findViewById(R.id.btnBack);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);

        // Background Animation Views (included layout)
        glowTop = findViewById(R.id.glowTop);
        glowBottom = findViewById(R.id.glowBottom);
    }

    private void setupListeners() {
        // Go Back
        btnBack.setOnClickListener(v -> finish());

        // Go to Previous Month
        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendarGrid();
        });

        // Go to Next Month
        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendarGrid();
        });
    }

    /**
     * Generates the list of days for the current month view and updates the adapter.
     */
    private void updateCalendarGrid() {
        // 1. Update Month Title (e.g., "November 2025")
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthName.setText(sdf.format(currentCalendar.getTime()));

        // 2. Generate the 42-day list
        List<Date> days = new ArrayList<>();
        Calendar c = (Calendar) currentCalendar.clone();

        // Set to the 1st day of the month
        c.set(Calendar.DAY_OF_MONTH, 1);

        // Determine which day of the week the 1st falls on (Sun=1, Mon=2...)
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        // Backtrack to the start of the week (Sunday)
        // If 1st is Tuesday (3), we need to go back 2 days (Sunday, Monday)
        c.add(Calendar.DAY_OF_YEAR, -(dayOfWeek - 1));

        // Loop to create 42 cells (6 rows x 7 columns)
        for (int i = 0; i < 42; i++) {
            days.add(c.getTime());
            c.add(Calendar.DAY_OF_YEAR, 1); // Move to next day
        }

        // 3. Set or Update Adapter
        if (calendarAdapter == null) {
            calendarAdapter = new CalendarAdapter(this, days, selectedDate, currentCalendar, date -> {
                selectedDate = date;
                loadAttendanceData(date); // Load list when a day is clicked
            });
            recyclerCalendar.setAdapter(calendarAdapter);
        } else {
            // Update existing adapter with new month data
            calendarAdapter.updateData(days, selectedDate, currentCalendar);
        }
    }

    /**
     * Fetches attendance data for the selected date from the database
     */
    private void loadAttendanceData(Date date) {
        // Format date for DB query (YYYY-MM-DD)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateStr = sdf.format(date);

        // Fetch data
        List<DailyAttendance> list = dao.getDailyAttendanceReport(dateStr);

        // Calculate Summary
        int present = 0;
        int absent = 0;
        for (DailyAttendance item : list) {
            if (item.getStatus() == 1) present++; // 1 = Present
            else absent++; // Leave or Not Marked
        }

        // Update UI Summary Cards
        tvTotal.setText(String.valueOf(list.size()));
        tvPresent.setText(String.valueOf(present));
        tvAbsent.setText(String.valueOf(absent));

        // Update Bottom List Adapter
        DailyReportAdapter adapter = new DailyReportAdapter(this, list);
        recyclerDaily.setAdapter(adapter);
    }
}