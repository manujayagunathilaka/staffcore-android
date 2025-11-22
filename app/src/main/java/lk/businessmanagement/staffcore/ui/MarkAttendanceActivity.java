package lk.businessmanagement.staffcore.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.data.AttendanceDAO;
import lk.businessmanagement.staffcore.data.EmployeeDAO;
import lk.businessmanagement.staffcore.model.Attendance;
import lk.businessmanagement.staffcore.model.Employee;
import lk.businessmanagement.staffcore.utils.AnimationHelper;

public class MarkAttendanceActivity extends AppCompatActivity {

    // UI Components
    private CircleImageView imgProfile;
    private TextView tvEmpName, tvEmpId, tvDate;
    private TextView tabPresent, tabLeave;
    private TextView tvInTime, tvOutTime;
    private EditText etReason;
    private MaterialButton btnInTimeNow, btnOutTimeNow, btnSave;
    private ImageView btnBack;

    // Layout Sections
    private LinearLayout layoutPresent, layoutLeave;

    // Animation Views
    private ImageView glowTop, glowBottom; // (Optional: If you added animation includes)

    // Data
    private EmployeeDAO empDao;
    private AttendanceDAO attDao;
    private int empId;
    private Attendance existingAttendance = null; // To store existing record if any

    // State
    private boolean isLeaveMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        initViews();

        // (Optional) Start Animation
        // AnimationHelper.animateBackground(findViewById(R.id.glowTop), findViewById(R.id.glowBottom));

        empDao = new EmployeeDAO(this);
        attDao = new AttendanceDAO(this);

        // Get Data from Intent
        empId = getIntent().getIntExtra("EMP_ID", -1);
        if (empId != -1) {
            loadEmployeeInfo();
        }

        // Default Date (Today)
        setDefaultDate();

        // 1. Setup Click Listeners
        setupListeners();

        // 2. Check if data exists for Today
        checkExistingAttendance(tvDate.getText().toString());
    }

    private void initViews() {
        imgProfile = findViewById(R.id.imgProfile);
        tvEmpName = findViewById(R.id.tvEmpName);
        tvEmpId = findViewById(R.id.tvEmpId);
        tvDate = findViewById(R.id.tvDate);

        tabPresent = findViewById(R.id.tabPresent);
        tabLeave = findViewById(R.id.tabLeave);

        tvInTime = findViewById(R.id.tvInTime);
        tvOutTime = findViewById(R.id.tvOutTime);
        etReason = findViewById(R.id.etReason);

        btnInTimeNow = findViewById(R.id.btnInTimeNow);
        btnOutTimeNow = findViewById(R.id.btnOutTimeNow);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        layoutPresent = findViewById(R.id.layoutPresent);
        layoutLeave = findViewById(R.id.layoutLeave);
    }

    private void loadEmployeeInfo() {
        Employee emp = empDao.getEmployeeById(empId);
        if (emp != null) {
            tvEmpName.setText(emp.getName());
            tvEmpId.setText("ID: " + emp.getNic()); // Or use auto ID

            if (emp.getProfilePhotoPath() != null) {
                Glide.with(this).load(emp.getProfilePhotoPath()).placeholder(android.R.drawable.sym_def_app_icon).into(imgProfile);
            }
        }
    }

    private void setDefaultDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        tvDate.setText(sdf.format(new Date()));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Tab Switching logic
        tabPresent.setOnClickListener(v -> switchTab(false));
        tabLeave.setOnClickListener(v -> switchTab(true));

        // Date Picker
        tvDate.setOnClickListener(v -> showDatePicker());

        // Time Pickers
        tvInTime.setOnClickListener(v -> showTimePicker(tvInTime));
        tvOutTime.setOnClickListener(v -> showTimePicker(tvOutTime));

        // "NOW" Buttons
        btnInTimeNow.setOnClickListener(v -> setTimeNow(tvInTime));
        btnOutTimeNow.setOnClickListener(v -> setTimeNow(tvOutTime));

        // Save Button
        btnSave.setOnClickListener(v -> saveAttendance());
    }

    // --- LOGIC FUNCTIONS ---

    private void switchTab(boolean isLeave) {
        isLeaveMode = isLeave;
        if (isLeave) {
            // Leave Mode
            layoutPresent.setVisibility(View.GONE);
            layoutLeave.setVisibility(View.VISIBLE);

            // Visual Update
            tabLeave.setBackgroundResource(R.drawable.bg_tab_active);
            tabLeave.setTextColor(ContextCompat.getColor(this, R.color.bg_deep_purple));

            tabPresent.setBackgroundResource(R.drawable.bg_glass_card_border);
            tabPresent.setTextColor(ContextCompat.getColor(this, R.color.white_50));
        } else {
            // Present Mode
            layoutPresent.setVisibility(View.VISIBLE);
            layoutLeave.setVisibility(View.GONE);

            // Visual Update
            tabPresent.setBackgroundResource(R.drawable.bg_tab_active);
            tabPresent.setTextColor(ContextCompat.getColor(this, R.color.bg_deep_purple));

            tabLeave.setBackgroundResource(R.drawable.bg_glass_card_border);
            tabLeave.setTextColor(ContextCompat.getColor(this, R.color.white_50));
        }
    }

    private void checkExistingAttendance(String date) {
        existingAttendance = attDao.getAttendanceByDate(empId, date);

        if (existingAttendance != null) {
            if (existingAttendance.isLeave()) {
                switchTab(true);
                etReason.setText(existingAttendance.getLeaveReason());
            } else {
                switchTab(false);
                tvInTime.setText(existingAttendance.getInTime());
                tvOutTime.setText(existingAttendance.getOutTime());
            }
            btnSave.setText("Update Record");
        } else {
            // Data නැත්නම් Clear කරනවා
            tvInTime.setText("");
            tvInTime.setHint("--:--");
            tvOutTime.setText("");
            tvOutTime.setHint("--:--");
            etReason.setText("");
            switchTab(false); // Default Present
            btnSave.setText("Mark Attendance");
        }
    }

    private void setTimeNow(TextView targetView) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        targetView.setText(sdf.format(new Date()));
    }

    private void showTimePicker(TextView targetView) {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            targetView.setText(time);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, day);
            tvDate.setText(date);
            checkExistingAttendance(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveAttendance() {
        String date = tvDate.getText().toString();
        Attendance att = new Attendance();

        if (existingAttendance != null) {
            att.setId(existingAttendance.getId());
        }

        att.setEmpId(empId);
        att.setDate(date);
        att.setLeave(isLeaveMode);

        if (isLeaveMode) {
            String reason = etReason.getText().toString();
            if (reason.isEmpty()) {
                Toast.makeText(this, "Please enter a reason", Toast.LENGTH_SHORT).show();
                return;
            }
            att.setLeaveReason(reason);
            att.setInTime("");
            att.setOutTime("");
        } else {
            String inTime = tvInTime.getText().toString();
            String outTime = tvOutTime.getText().toString();

            if (inTime.isEmpty() && outTime.isEmpty()) {
                Toast.makeText(this, "Please enter In or Out time", Toast.LENGTH_SHORT).show();
                return;
            }
            att.setInTime(inTime);
            att.setOutTime(outTime);
            att.setLeaveReason("");
        }

        boolean success;
        if (existingAttendance != null) {
            success = attDao.updateAttendance(att); // Update
        } else {
            success = attDao.markAttendance(att); // Insert
        }

        if (success) {
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
        }
    }
}