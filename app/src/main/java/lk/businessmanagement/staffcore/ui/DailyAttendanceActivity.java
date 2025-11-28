package lk.businessmanagement.staffcore.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.data.AttendanceDAO;
import lk.businessmanagement.staffcore.model.Attendance;
import lk.businessmanagement.staffcore.model.DailyAttendance;
import lk.businessmanagement.staffcore.ui.adapters.CalendarAdapter;
import lk.businessmanagement.staffcore.ui.adapters.DailyReportAdapter;
import lk.businessmanagement.staffcore.utils.AnimationHelper;

public class DailyAttendanceActivity extends AppCompatActivity {

    // --- UI Components ---
    private TextView tvMonth, tvYear; // New split headers
    private TextView tvTotal, tvPresent, tvAbsent;
    private RecyclerView recyclerCalendar, recyclerDaily;
    private ImageView btnBack, btnPrevMonth, btnNextMonth, glowTop, glowBottom;

    // --- Data & Helpers ---
    private AttendanceDAO dao;
    private Calendar currentCalendar; // Tracks currently viewed month
    private Date selectedDate;        // Tracks selected specific day
    private CalendarAdapter calendarAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_daily_attendance);

        // 1. Initialize Views
        initViews();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_daily_attendance), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // 2. Start Background Animation
        AnimationHelper.animateBackground(glowTop, glowBottom);

        // 3. Initialize Data
        dao = new AttendanceDAO(this);
        currentCalendar = Calendar.getInstance();
        selectedDate = currentCalendar.getTime();

        // 4. Setup List RecyclerView (Bottom List)
        recyclerDaily.setLayoutManager(new LinearLayoutManager(this));

        // 5. Setup Calendar RecyclerView (Top Grid - 7 Columns)
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 7);
        recyclerCalendar.setLayoutManager(gridLayoutManager);

        // 6. Initial Data Load
        updateHeader();
        updateCalendarGrid();
        loadAttendanceData(selectedDate);

        // 7. Setup Click Listeners
        setupListeners();
    }

    private void initViews() {
        // Headers
        tvMonth = findViewById(R.id.tvMonth);
        tvYear = findViewById(R.id.tvYear);

        // Summary Cards
        tvTotal = findViewById(R.id.tvTotal);
        tvPresent = findViewById(R.id.tvPresent);
        tvAbsent = findViewById(R.id.tvAbsent);

        // Recyclers
        recyclerCalendar = findViewById(R.id.recyclerCalendar);
        recyclerDaily = findViewById(R.id.recyclerDaily);

        // Buttons & Graphics
        btnBack = findViewById(R.id.btnBack);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        glowTop = findViewById(R.id.glowTop);
        glowBottom = findViewById(R.id.glowBottom);
    }

    private void setupListeners() {
        // Back Button
        btnBack.setOnClickListener(v -> finish());

        // Previous Month Arrow
        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateHeader();
            updateCalendarGrid();
        });

        // Next Month Arrow
        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateHeader();
            updateCalendarGrid();
        });

        // Open Picker Sheet when clicking Month or Year
        tvMonth.setOnClickListener(v -> showDatePickerSheet());
        tvYear.setOnClickListener(v -> showDatePickerSheet());
    }

    private void updateHeader() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

        tvMonth.setText(monthFormat.format(currentCalendar.getTime()));
        tvYear.setText(yearFormat.format(currentCalendar.getTime()));
    }

    /**
     * Shows a Custom Bottom Sheet to pick Year and Month
     */
    private void showDatePickerSheet() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        // Fix for transparent background
        bottomSheet.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetInternal != null) {
                bottomSheetInternal.setBackgroundResource(android.R.color.transparent);
            }
        });

        View view = getLayoutInflater().inflate(R.layout.layout_date_picker, null);
        bottomSheet.setContentView(view);

        RecyclerView recyclerYears = view.findViewById(R.id.recyclerYears);
        RecyclerView recyclerMonths = view.findViewById(R.id.recyclerMonths);

        // --- Setup Years (2020 - 2030) ---
        List<String> years = new ArrayList<>();
        for (int i = 2020; i <= 2030; i++) years.add(String.valueOf(i));

        recyclerYears.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerYears.setAdapter(new SimpleTextAdapter(years, yearStr -> {
            int year = Integer.parseInt(yearStr);
            currentCalendar.set(Calendar.YEAR, year);
            updateHeader();
            updateCalendarGrid();
            bottomSheet.dismiss();
        }, String.valueOf(currentCalendar.get(Calendar.YEAR))));

        // --- Setup Months (Jan - Dec) ---
        List<String> months = new ArrayList<>();
        String[] shortMonths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for(String m : shortMonths) months.add(m);

        recyclerMonths.setLayoutManager(new GridLayoutManager(this, 4)); // 4 Columns Grid
        recyclerMonths.setAdapter(new SimpleTextAdapter(months, monthName -> {
            int monthIndex = getMonthIndex(monthName);
            currentCalendar.set(Calendar.MONTH, monthIndex);
            updateHeader();
            updateCalendarGrid();
            bottomSheet.dismiss();
        }, shortMonths[currentCalendar.get(Calendar.MONTH)]));

        bottomSheet.show();
    }

    private int getMonthIndex(String name) {
        String[] shortMonths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for(int i=0; i<12; i++) if(shortMonths[i].equals(name)) return i;
        return 0;
    }

    /**
     * Generates the 42-day grid for the calendar view
     */
    private void updateCalendarGrid() {
        List<Date> days = getDatesForMonth(currentCalendar);

        if (calendarAdapter == null) {
            calendarAdapter = new CalendarAdapter(this, days, selectedDate, currentCalendar, date -> {
                selectedDate = date;
                loadAttendanceData(date); // Load data when a date is clicked
            });
            recyclerCalendar.setAdapter(calendarAdapter);
        } else {
            calendarAdapter.updateData(days, selectedDate, currentCalendar);
        }
    }

    /**
     * Helper to generate list of dates for the grid
     */
    private List<Date> getDatesForMonth(Calendar cal) {
        List<Date> dates = new ArrayList<>();
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.DAY_OF_MONTH, 1);

        // Find start of the week (Sunday)
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        c.add(Calendar.DAY_OF_YEAR, -(dayOfWeek - 1));

        // Generate 42 days
        for (int i = 0; i < 42; i++) {
            dates.add(c.getTime());
            c.add(Calendar.DAY_OF_YEAR, 1);
        }
        return dates;
    }

    /**
     * Loads employee list and attendance status for the selected date
     */
    private void loadAttendanceData(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateStr = sdf.format(date);

        List<DailyAttendance> list = dao.getDailyAttendanceReport(dateStr);

        // Update Summary Counts
        int present = 0;
        int absent = 0;
        for (DailyAttendance item : list) {
            if (item.getStatus() == 1) present++;
            else absent++;
        }

        tvTotal.setText(String.valueOf(list.size()));
        tvPresent.setText(String.valueOf(present));
        tvAbsent.setText(String.valueOf(absent));

        // Update List Adapter with Click Listener for Bottom Sheet
        DailyReportAdapter adapter = new DailyReportAdapter(this, list, item -> {
            // Show the action bottom sheet
            showMarkBottomSheet(item, dateStr);
        });
        recyclerDaily.setAdapter(adapter);
    }

    /**
     * Shows the Floating Bottom Sheet to Mark Attendance
     */
    private void showMarkBottomSheet(DailyAttendance item, String dateStr) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);

        bottomSheet.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetInternal != null) {
                bottomSheetInternal.setBackgroundResource(android.R.color.transparent);
            }
        });

        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_mark, null);
        bottomSheet.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tvSheetTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvSheetSubtitle);
        Button btnMainAction = view.findViewById(R.id.btnMainAction);
        Button btnMarkLeave = view.findViewById(R.id.btnMarkLeave);

        tvTitle.setText(item.getEmployeeName());

        Attendance existing = dao.getAttendanceByDate(item.getEmpId(), dateStr);

        if (existing == null) {
            tvSubtitle.setText("No record found. Please Check In.");
            btnMainAction.setText("Check In Now");
            btnMainAction.setBackgroundColor(getColor(R.color.status_green));

            btnMainAction.setOnClickListener(v -> {
                saveOrUpdateAttendance(item.getEmpId(), dateStr, "IN", null);
                bottomSheet.dismiss();
            });

            btnMarkLeave.setVisibility(View.VISIBLE);
            btnMarkLeave.setOnClickListener(v -> {
                saveOrUpdateAttendance(item.getEmpId(), dateStr, "LEAVE", null);
                bottomSheet.dismiss();
            });

        } else if (existing.isLeave()) {
            tvSubtitle.setText("Currently marked as Absent.");
            btnMainAction.setText("Change to Present (Check In)");
            btnMainAction.setBackgroundColor(getColor(R.color.neon_blue));

            btnMainAction.setOnClickListener(v -> {
                saveOrUpdateAttendance(item.getEmpId(), dateStr, "IN", existing);
                bottomSheet.dismiss();
            });
            btnMarkLeave.setVisibility(View.GONE);

        } else if (existing.getOutTime() == null || existing.getOutTime().isEmpty()) {
            tvSubtitle.setText("Checked In at: " + existing.getInTime());
            btnMainAction.setText("Check Out Now");
            btnMainAction.setBackgroundColor(getColor(R.color.status_red));

            btnMainAction.setOnClickListener(v -> {
                saveOrUpdateAttendance(item.getEmpId(), dateStr, "OUT", existing);
                bottomSheet.dismiss();
            });
            btnMarkLeave.setVisibility(View.GONE);

        } else {
            tvSubtitle.setText("Shift: " + existing.getInTime() + " - " + existing.getOutTime());
            btnMainAction.setText("Update Check Out Time");
            btnMainAction.setBackgroundColor(getColor(R.color.neon_gold));

            btnMainAction.setOnClickListener(v -> {
                saveOrUpdateAttendance(item.getEmpId(), dateStr, "OUT", existing);
                bottomSheet.dismiss();
            });
            btnMarkLeave.setVisibility(View.GONE);
        }

        bottomSheet.show();
    }

    private void saveOrUpdateAttendance(int empId, String date, String action, Attendance existingRecord) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = timeFormat.format(new Date());

        Attendance att = (existingRecord != null) ? existingRecord : new Attendance();

        if (existingRecord == null) {
            att.setEmpId(empId);
            att.setDate(date);
        }

        if (action.equals("LEAVE")) {
            att.setLeave(true);
            att.setLeaveReason("Marked via Daily Report");
            att.setInTime("");
            att.setOutTime("");
        }
        else if (action.equals("IN")) {
            att.setLeave(false);
            att.setInTime(currentTime);
            if(existingRecord == null) att.setOutTime("");
        }
        else if (action.equals("OUT")) {
            att.setLeave(false);
            att.setOutTime(currentTime);
        }

        boolean success;
        if (existingRecord != null) {
            success = dao.updateAttendance(att);
        } else {
            success = dao.markAttendance(att);
        }

        if (success) {
            String msg = action.equals("LEAVE") ? "Marked Absent" : "Updated Successfully";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            loadAttendanceData(selectedDate);
        } else {
            Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show();
        }
    }

    // --- INNER CLASS: Simple Adapter ---
    // Added 'static' to fix language level error
    private static class SimpleTextAdapter extends RecyclerView.Adapter<SimpleTextAdapter.ViewHolder> {

        private List<String> items;
        private OnItemClick onItemClick;
        private String selectedItem;

        // Interface defined inside static class
        public interface OnItemClick {
            void onClick(String text);
        }

        public SimpleTextAdapter(List<String> items, OnItemClick onItemClick, String selectedItem) {
            this.items = items;
            this.onItemClick = onItemClick;
            this.selectedItem = selectedItem;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Use parent.getContext() for layout inflater
            return new ViewHolder(android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_text_chip, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String text = items.get(position);
            holder.tvChip.setText(text);

            Context context = holder.itemView.getContext(); // Get context from view

            if (text.equals(selectedItem)) {
                holder.tvChip.setBackgroundResource(R.drawable.bg_gradient_gold_btn);
                holder.tvChip.setTextColor(ContextCompat.getColor(context, R.color.black));
            } else {
                holder.tvChip.setBackgroundResource(R.drawable.bg_glass_card_border);
                holder.tvChip.setTextColor(ContextCompat.getColor(context, R.color.white));
            }

            holder.tvChip.setOnClickListener(v -> onItemClick.onClick(text));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvChip;
            ViewHolder(View itemView) {
                super(itemView);
                tvChip = itemView.findViewById(R.id.tvChip);
            }
        }
    }
}