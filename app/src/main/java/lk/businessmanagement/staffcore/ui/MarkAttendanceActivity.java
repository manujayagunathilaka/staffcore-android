package lk.businessmanagement.staffcore.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.data.AttendanceDAO;
import lk.businessmanagement.staffcore.model.Attendance;

public class MarkAttendanceActivity extends AppCompatActivity {

    private TextView tvEmpName;
    private TextInputEditText etDate, etInTime, etOutTime, etReason;
    private RadioButton rbPresent, rbLeave;
    private LinearLayout layoutPresent, layoutLeave;
    private Button btnSave;

    private int empId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        empId = getIntent().getIntExtra("EMP_ID", -1);
        String empName = getIntent().getStringExtra("EMP_NAME");

        initViews();

        if(empName != null) {
            tvEmpName.setText("for " + empName);
        }

        setDefaultDate();

        setupRadioToggle();

        etDate.setOnClickListener(v -> showDatePicker());
        etInTime.setOnClickListener(v -> showTimePicker(etInTime));
        etOutTime.setOnClickListener(v -> showTimePicker(etOutTime));

        btnSave.setOnClickListener(v -> saveAttendance());
    }

    private void initViews() {
        tvEmpName = findViewById(R.id.tvEmpName);
        etDate = findViewById(R.id.etDate);
        etInTime = findViewById(R.id.etInTime);
        etOutTime = findViewById(R.id.etOutTime);
        etReason = findViewById(R.id.etReason);
        rbPresent = findViewById(R.id.rbPresent);
        rbLeave = findViewById(R.id.rbLeave);
        layoutPresent = findViewById(R.id.layoutPresent);
        layoutLeave = findViewById(R.id.layoutLeave);
        btnSave = findViewById(R.id.btnSaveAttendance);
    }

    private void setDefaultDate() {
        Calendar c = Calendar.getInstance();
        String today = String.format(Locale.getDefault(), "%d-%02d-%02d",
                c.get(Calendar.YEAR), (c.get(Calendar.MONTH) + 1), c.get(Calendar.DAY_OF_MONTH));
        etDate.setText(today);

        etInTime.setText("08:00");
        etOutTime.setText("17:00");
    }

    private void setupRadioToggle() {
        rbPresent.setOnClickListener(v -> {
            layoutPresent.setVisibility(View.VISIBLE);
            layoutLeave.setVisibility(View.GONE);
        });

        rbLeave.setOnClickListener(v -> {
            layoutPresent.setVisibility(View.GONE);
            layoutLeave.setVisibility(View.VISIBLE);
        });
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            etDate.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, day));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker(TextInputEditText target) {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            target.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void saveAttendance() {
        String date = etDate.getText().toString();
        boolean isLeave = rbLeave.isChecked();

        Attendance att = new Attendance();
        att.setEmpId(empId);
        att.setDate(date);
        att.setLeave(isLeave);

        if (isLeave) {
            String reason = etReason.getText().toString();
            if (reason.isEmpty()) {
                etReason.setError("Reason required");
                return;
            }
            att.setLeaveReason(reason);
            att.setInTime("");
            att.setOutTime("");
        } else {
            String inTime = etInTime.getText().toString();
            String outTime = etOutTime.getText().toString();

            if (inTime.isEmpty() || outTime.isEmpty()) {
                Toast.makeText(this, "Times are required", Toast.LENGTH_SHORT).show();
                return;
            }
            att.setInTime(inTime);
            att.setOutTime(outTime);
            att.setLeaveReason("");
        }

        AttendanceDAO dao = new AttendanceDAO(this);
        if (dao.markAttendance(att)) {
            Toast.makeText(this, "Attendance Marked!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
        }
    }
}