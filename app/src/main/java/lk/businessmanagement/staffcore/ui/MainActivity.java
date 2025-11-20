package lk.businessmanagement.staffcore.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.model.Employee;
import lk.businessmanagement.staffcore.data.EmployeeDAO;
import lk.businessmanagement.staffcore.ui.adapters.EmployeeAdapter;
import lk.businessmanagement.staffcore.data.AdvanceDAO;
import lk.businessmanagement.staffcore.data.AttendanceDAO;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private TextView tvEmptyState, tvCountStaff, tvCountPresent, tvTotalAdvances;

    private EmployeeAdapter adapter;
    private List<Employee> employeeList;
    private EmployeeDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewEmployees);
        fabAdd = findViewById(R.id.fabAdd);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        tvCountStaff = findViewById(R.id.tvCountStaff);
        tvCountPresent = findViewById(R.id.tvCountPresent);
        tvTotalAdvances = findViewById(R.id.tvTotalAdvances);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        employeeList = new ArrayList<>();

        dao = new EmployeeDAO(this);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEmployeeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEmployees();
    }

    private void loadEmployees() {
        employeeList = dao.getAllEmployees();

        if (employeeList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new EmployeeAdapter(this, employeeList);
            recyclerView.setAdapter(adapter);
        }


        int staffCount = dao.getEmployeeCount();
        tvCountStaff.setText(String.valueOf(staffCount));

        AttendanceDAO attDao = new AttendanceDAO(this);
        Calendar c = Calendar.getInstance();
        String today = String.format(Locale.getDefault(), "%d-%02d-%02d",
                c.get(Calendar.YEAR), (c.get(Calendar.MONTH) + 1), c.get(Calendar.DAY_OF_MONTH));

        int presentCount = attDao.getTodayPresentCount(today);
        tvCountPresent.setText(String.valueOf(presentCount));

        AdvanceDAO advDao = new AdvanceDAO(this);
        double totalAdv = advDao.getAllTimeTotalAdvance();
        tvTotalAdvances.setText(String.valueOf((int)totalAdv));
    }
}