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

    // UI Components
    private TextView tvCurrentMonth, tvTotalDays, tvPresentDays, tvLeaveDays, tvEmpty;
    private ImageView btnPrevMonth, btnNextMonth, btnBack;
    private RecyclerView recyclerHistory;
    private ImageView glowTop, glowBottom;

    // Data & State
    private AttendanceDAO dao;
    private int empId;
    private Calendar currentCalendar; // මාසය මතක තියාගන්න

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);

        // 1. Initialize
        initViews();
        AnimationHelper.animateBackground(glowTop, glowBottom);

        dao = new AttendanceDAO(this);
        currentCalendar = Calendar.getInstance(); // අද දවස/මාසය ගන්නවා

        // Intent එකෙන් ID එක ගන්නවා
        empId = getIntent().getIntExtra("EMP_ID", -1);

        // 2. Setup RecyclerView
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));

        // 3. Initial Load
        loadHistoryData();

        // 4. Click Listeners
        btnBack.setOnClickListener(v -> finish());

        btnPrevMonth.setOnClickListener(v -> {
            // මාසයක් පස්සට (Minus 1 Month)
            currentCalendar.add(Calendar.MONTH, -1);
            loadHistoryData();
        });

        btnNextMonth.setOnClickListener(v -> {
            // මාසයක් ඉස්සරහට (Plus 1 Month)
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
        // 1. මාසයේ නම UI එකේ පෙන්වීම (Example: November 2025)
        SimpleDateFormat sdfDisplay = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvCurrentMonth.setText(sdfDisplay.format(currentCalendar.getTime()));

        // 2. Database එකට යවන්න ඕන Format එක හදාගැනීම (Example: "2025-11")
        SimpleDateFormat sdfQuery = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String monthPrefix = sdfQuery.format(currentCalendar.getTime());

        // 3. Database එකෙන් Data ගැනීම
        List<Attendance> list = dao.getHistoryForMonth(empId, monthPrefix);

        // 4. Summary ගණනය කිරීම
        int presentCount = 0;
        int leaveCount = 0;

        for (Attendance att : list) {
            if (att.isLeave()) {
                leaveCount++;
            } else {
                presentCount++;
            }
        }

        // UI Update (Cards)
        tvTotalDays.setText(String.valueOf(list.size()));
        tvPresentDays.setText(String.valueOf(presentCount));
        tvLeaveDays.setText(String.valueOf(leaveCount));

        // 5. List එක පෙන්වීම
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