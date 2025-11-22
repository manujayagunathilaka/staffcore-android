package lk.businessmanagement.staffcore.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.data.AdvanceDAO;
import lk.businessmanagement.staffcore.data.EmployeeDAO;
import lk.businessmanagement.staffcore.model.Advance;
import lk.businessmanagement.staffcore.model.Employee;
import lk.businessmanagement.staffcore.utils.AnimationHelper;

public class EmployeeProfileActivity extends AppCompatActivity {

    // UI Components
    private ImageView imgProfile, imgIdFront, imgIdBack, imgCv;
    private TextView tvName, tvNic, tvGender, tvDob, tvMarital, tvJoined;
    private TextView tvMobile, tvHome, tvAddress, tvTotalAdvance;
    private Button btnHistory, btnAddAdvance;
    private ImageView btnBack;
    private FloatingActionButton fabEdit;

    // Animation Views
    private ImageView glowTop, glowBottom;

    private EmployeeDAO empDao;
    private AdvanceDAO advDao;
    private int employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);

        // Data Initialization
        empDao = new EmployeeDAO(this);
        advDao = new AdvanceDAO(this);
        employeeId = getIntent().getIntExtra("EMP_ID", -1);

        if (employeeId == -1) {
            Toast.makeText(this, "Error loading profile!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();

        // Start Animation
        AnimationHelper.animateBackground(glowTop, glowBottom);

        // Click Listeners
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning (e.g. after editing)
        loadEmployeeData();
        updateTotalAdvance();
    }

    private void initViews() {
        imgProfile = findViewById(R.id.imgProfile);
        imgIdFront = findViewById(R.id.imgIdFront);
        imgIdBack = findViewById(R.id.imgIdBack);
        imgCv = findViewById(R.id.imgCv);

        tvName = findViewById(R.id.tvName);
        tvNic = findViewById(R.id.tvNic);
        tvGender = findViewById(R.id.tvGender);
        tvDob = findViewById(R.id.tvDob);
        tvMarital = findViewById(R.id.tvMarital);
        tvJoined = findViewById(R.id.tvJoined);
        tvMobile = findViewById(R.id.tvMobile);
        tvHome = findViewById(R.id.tvHome);
        tvAddress = findViewById(R.id.tvAddress);
        tvTotalAdvance = findViewById(R.id.tvTotalAdvance);

        btnHistory = findViewById(R.id.btnHistory);
        btnAddAdvance = findViewById(R.id.btnAddAdvance);
        btnBack = findViewById(R.id.btnBack);
        fabEdit = findViewById(R.id.fabEdit);

        glowTop = findViewById(R.id.glowTop);
        glowBottom = findViewById(R.id.glowBottom);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeProfileActivity.this, AttendanceHistoryActivity.class);
            intent.putExtra("EMP_ID", employeeId);
            startActivity(intent);
        });

        btnAddAdvance.setOnClickListener(v -> showAddAdvanceDialog());

        fabEdit.setOnClickListener(v -> {
            Toast.makeText(this, "Edit Feature Coming Soon!", Toast.LENGTH_SHORT).show();
            // Future: Intent to Edit Activity
        });
    }

    private void loadEmployeeData() {
        Employee emp = empDao.getEmployeeById(employeeId);

        if (emp != null) {
            tvName.setText(emp.getName());
            tvNic.setText("NIC: " + emp.getNic());
            tvGender.setText("Gender: " + emp.getGender());
            tvDob.setText("DOB: " + emp.getDob());
            tvMarital.setText("Status: " + emp.getMaritalStatus());
            tvJoined.setText("Joined: " + emp.getJoinedDate());
            tvMobile.setText("Mobile: " + emp.getMobileNumber());
            tvHome.setText("Home: " + emp.getHomeNumber());
            tvAddress.setText("Address: " + emp.getAddress());

            // Load Images using Glide (Better & Faster)
            loadImagesWithGlide(imgProfile, emp.getProfilePhotoPath());
            loadImagesWithGlide(imgIdFront, emp.getIdFrontPath());
            loadImagesWithGlide(imgIdBack, emp.getIdBackPath());
            loadImagesWithGlide(imgCv, emp.getCvPath());
        }
    }

    private void loadImagesWithGlide(ImageView imageView, String path) {
        if (path != null && !path.isEmpty()) {
            Glide.with(this)
                    .load(path)
                    .placeholder(android.R.drawable.ic_menu_gallery) // Loading image
                    .error(android.R.drawable.stat_notify_error)     // Error image
                    .into(imageView);
        }
    }

    // --- Advance Logic (Same as before) ---
    private void showAddAdvanceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Advance Amount");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Enter Amount (Rs.)");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String amountStr = input.getText().toString();
            if (!amountStr.isEmpty()) {
                saveAdvance(Double.parseDouble(amountStr));
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveAdvance(double amount) {
        Calendar c = Calendar.getInstance();
        String today = String.format(Locale.getDefault(), "%d-%02d-%02d",
                c.get(Calendar.YEAR), (c.get(Calendar.MONTH) + 1), c.get(Calendar.DAY_OF_MONTH));

        Advance adv = new Advance(employeeId, today, amount);

        if (advDao.addAdvance(adv)) {
            Toast.makeText(this, "Advance Added!", Toast.LENGTH_SHORT).show();
            updateTotalAdvance();
        } else {
            Toast.makeText(this, "Error adding advance", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTotalAdvance() {
        double total = advDao.getTotalAdvanceForEmployee(employeeId);
        tvTotalAdvance.setText(String.format("Total Advances: Rs. %.2f", total));
    }
}