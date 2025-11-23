package lk.businessmanagement.staffcore.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.data.AttendanceDAO;
import lk.businessmanagement.staffcore.model.Attendance;
import lk.businessmanagement.staffcore.ui.adapters.AttendanceHistoryAdapter;
import lk.businessmanagement.staffcore.utils.AnimationHelper;

public class AttendanceHistoryActivity extends AppCompatActivity {
    private TextView tvCurrentMonth, tvTotalDays, tvPresentDays, tvLeaveDays, tvEmpty;
    private ImageView btnPrevMonth, btnNextMonth, btnBack;
    private RecyclerView recyclerHistory;
    private ImageView glowTop, glowBottom;
    private AttendanceDAO dao;
    private int empId;
    private Calendar currentCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);

        initViews();
        AnimationHelper.animateBackground(glowTop, glowBottom);

        dao = new AttendanceDAO(this);
        currentCalendar = Calendar.getInstance();

        empId = getIntent().getIntExtra("EMP_ID", -1);

        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));

        loadHistoryData();

        btnBack.setOnClickListener(v -> finish());

        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            loadHistoryData();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            loadHistoryData();
        });
    }

    private void initViews() {
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        tvTotalDays = findViewById(R.id.tvTotalDays);
        tvPresentDays = findViewById(R.id.tvPresentDays);
        tvLeaveDays = findViewById(R.id.tvLeaveDays);
        tvEmpty = findViewById(R.id.tvEmpty);

        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        btnBack = findViewById(R.id.btnBack);
        recyclerHistory = findViewById(R.id.recyclerHistory);

        glowTop = findViewById(R.id.glowTop);
        glowBottom = findViewById(R.id.glowBottom);
    }

    private void loadHistoryData() {
        SimpleDateFormat sdfDisplay = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvCurrentMonth.setText(sdfDisplay.format(currentCalendar.getTime()));

        SimpleDateFormat sdfQuery = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String monthPrefix = sdfQuery.format(currentCalendar.getTime());

        List<Attendance> list = dao.getHistoryForMonth(empId, monthPrefix);

        int presentCount = 0;
        int leaveCount = 0;

        for (Attendance att : list) {
            if (att.isLeave()) {
                leaveCount++;
            } else {
                presentCount++;
            }
        }

        tvTotalDays.setText(String.valueOf(list.size()));
        tvPresentDays.setText(String.valueOf(presentCount));
        tvLeaveDays.setText(String.valueOf(leaveCount));

        if (list.isEmpty()) {
            recyclerHistory.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerHistory.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);

            AttendanceHistoryAdapter adapter = new AttendanceHistoryAdapter(this, list);
            recyclerHistory.setAdapter(adapter);
        }
    }
}