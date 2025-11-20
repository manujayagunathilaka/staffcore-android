package lk.businessmanagement.staffcore.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.data.AdvanceDAO;
import lk.businessmanagement.staffcore.data.EmployeeDAO;
import lk.businessmanagement.staffcore.model.Employee;
import android.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import lk.businessmanagement.staffcore.data.AdvanceDAO;
import lk.businessmanagement.staffcore.model.Advance;
import java.util.Calendar;
import java.util.Locale;

public class EmployeeProfileActivity extends AppCompatActivity {

    private ImageView imgProfile, imgIdFront, imgIdBack, imgCv;
    private TextView tvName, tvNic, tvGender, tvDob, tvMarital, tvJoined, tvMobile, tvHome, tvAddress;

    private EmployeeDAO dao;
    private int employeeId;

    private TextView tvTotalAdvance;
    private Button btnAddAdvance;
    private AdvanceDAO advanceDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);

        employeeId = getIntent().getIntExtra("EMP_ID", -1);

        if (employeeId == -1) {
            Toast.makeText(this, "Error loading profile!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadEmployeeData();

        Button btnMarkAttendance = findViewById(R.id.btnMarkAttendance);

        btnMarkAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeProfileActivity.this, MarkAttendanceActivity.class);
            intent.putExtra("EMP_ID", employeeId);
            intent.putExtra("EMP_NAME", tvName.getText().toString());
            startActivity(intent);
        });

        advanceDAO = new AdvanceDAO(this);
        updateTotalAdvance();

        btnAddAdvance.setOnClickListener(v -> showAddAdvanceDialog());

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
        btnAddAdvance = findViewById(R.id.btnAddAdvance);
    }

    private void loadEmployeeData() {
        dao = new EmployeeDAO(this);
        Employee emp = dao.getEmployeeById(employeeId);

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

            setImage(imgProfile, emp.getProfilePhotoPath());
            setImage(imgIdFront, emp.getIdFrontPath());
            setImage(imgIdBack, emp.getIdBackPath());
            setImage(imgCv, emp.getCvPath());
        }
    }

    private void setImage(ImageView imageView, String path) {
        if (path != null && !path.isEmpty()) {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                imageView.setImageURI(Uri.fromFile(imgFile));
            }
        }
    }

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

        if (advanceDAO.addAdvance(adv)) {
            Toast.makeText(this, "Advance Added!", Toast.LENGTH_SHORT).show();
            updateTotalAdvance();
        } else {
            Toast.makeText(this, "Error adding advance", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTotalAdvance() {
        double total = advanceDAO.getTotalAdvanceForEmployee(employeeId);
        tvTotalAdvance.setText(String.format("Total Advances: Rs. %.2f", total));
    }

}