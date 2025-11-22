package lk.businessmanagement.staffcore.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.data.EmployeeDAO;
import lk.businessmanagement.staffcore.model.Employee;
import lk.businessmanagement.staffcore.ui.adapters.EmployeeAdapter;
import lk.businessmanagement.staffcore.utils.AnimationHelper;

public class EmployeeListActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView recyclerView;
    private BlurView blurSearchBar;
    private ImageView btnBack;
    private TextView tvEmpty;
    private EditText etSearch;

    // Animation Views (Background Glows)
    private ImageView glowTop, glowBottom;

    // Data & Logic
    private EmployeeDAO dao;
    private EmployeeAdapter adapter;
    private List<Employee> employeeList;

    // Mode Flag (To check if we are here to mark attendance or view profile)
    private boolean isAttendanceMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        // 1. Initialize Views
        initViews();

        // 2. Start Background Animation
        AnimationHelper.animateBackground(glowTop, glowBottom);

        // 3. Setup Glass Blur Effect for Search Bar
        setupBlurView();

        // 4. Check Intent Mode (From Dashboard)
        // If "IS_ATTENDANCE_MODE" is true, it means we need to select an employee for attendance.
        isAttendanceMode = getIntent().getBooleanExtra("IS_ATTENDANCE_MODE", false);

        if (isAttendanceMode) {
            // Update UI to reflect Attendance Mode
            etSearch.setHint("Select employee to mark attendance...");
        }

        // 5. Setup RecyclerView & Database
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dao = new EmployeeDAO(this);

        // 6. Load Data
        loadEmployees();

        // 7. Setup Search Listener (Typing Logic)
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Back Button Logic
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewEmployees);
        blurSearchBar = findViewById(R.id.blurSearchBar);
        btnBack = findViewById(R.id.btnBack);
        tvEmpty = findViewById(R.id.tvEmpty);
        etSearch = findViewById(R.id.etSearch);

        glowTop = findViewById(R.id.glowTop);
        glowBottom = findViewById(R.id.glowBottom);
    }

    private void setupBlurView() {
        float radius = 20f;
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        blurSearchBar.setupWith(rootView, new RenderScriptBlur(this))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius);
    }

    private void loadEmployees() {
        employeeList = dao.getAllEmployees();

        if (employeeList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Pass 'isAttendanceMode' to the Adapter
            adapter = new EmployeeAdapter(this, employeeList, isAttendanceMode);
            recyclerView.setAdapter(adapter);
        }
    }

    // --- SEARCH FILTER LOGIC ---
    private void filterList(String text) {
        List<Employee> filteredList = new ArrayList<>();

        for (Employee emp : employeeList) {
            if (emp.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(emp);
            }
        }

        if (filteredList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("No matches found");
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Pass the filtered list to the adapter
            adapter.setFilteredList(filteredList);
        }
    }
}