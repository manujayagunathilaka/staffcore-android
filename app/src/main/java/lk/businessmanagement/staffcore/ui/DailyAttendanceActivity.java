package lk.businessmanagement.staffcore.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.data.AttendanceDAO;
import lk.businessmanagement.staffcore.model.DailyAttendance;
import lk.businessmanagement.staffcore.ui.adapters.DailyReportAdapter;
import lk.businessmanagement.staffcore.utils.AnimationHelper;

public class DailyAttendanceActivity extends AppCompatActivity {

    private TextView tvSelectedDate, tvTotal, tvPresent, tvAbsent;
    private LinearLayout dateCard;
    private RecyclerView recyclerDaily;
    private ImageView btnBack, glowTop, glowBottom;

    private AttendanceDAO dao;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_attendance);

        initViews();
        AnimationHelper.animateBackground(glowTop, glowBottom);

        dao = new AttendanceDAO(this);
        calendar = Calendar.getInstance(); // අද දිනය

        recyclerDaily.setLayoutManager(new LinearLayoutManager(this));

        // 1. මුලින්ම අද දවසේ Data පෙන්වන්න
        updateDateAndLoadData();

        // 2. Date Click කළාම Date Picker එක පෙන්වන්න
        dateCard.setOnClickListener(v -> showDatePicker());

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvTotal = findViewById(R.id.tvTotal);
        tvPresent = findViewById(R.id.tvPresent);
        tvAbsent = findViewById(R.id.tvAbsent);
        dateCard = findViewById(R.id.dateCard);
        recyclerDaily = findViewById(R.id.recyclerDaily);
        btnBack = findViewById(R.id.btnBack);

        glowTop = findViewById(R.id.glowTop);
        glowBottom = findViewById(R.id.glowBottom);
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            updateDateAndLoadData();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateAndLoadData() {
        // දිනය Format කර පෙන්වීම
        SimpleDateFormat sdfDisplay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateStr = sdfDisplay.format(calendar.getTime());
        tvSelectedDate.setText(dateStr);

        // Database එකෙන් Data ගැනීම
        loadData(dateStr);
    }

    private void loadData(String date) {
        List<DailyAttendance> list = dao.getDailyAttendanceReport(date);

        // Summary ගණනය කිරීම
        int present = 0;
        int absent = 0; // Leave + Not Marked

        for (DailyAttendance item : list) {
            if (item.getStatus() == 1) {
                present++;
            } else {
                absent++;
            }
        }

        // Update Summary UI
        tvTotal.setText(String.valueOf(list.size()));
        tvPresent.setText(String.valueOf(present));
        tvAbsent.setText(String.valueOf(absent));

        // Update List
        DailyReportAdapter adapter = new DailyReportAdapter(this, list);
        recyclerDaily.setAdapter(adapter);
    }
}