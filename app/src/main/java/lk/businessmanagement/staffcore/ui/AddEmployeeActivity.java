package lk.businessmanagement.staffcore.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.Calendar;

import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.model.Employee;
import lk.businessmanagement.staffcore.data.EmployeeDAO;
import lk.businessmanagement.staffcore.utils.ImageUtils;
import lk.businessmanagement.staffcore.utils.InputValidator;

public class AddEmployeeActivity extends AppCompatActivity {

    private ImageView imgProfile, imgIdFront, imgIdBack, imgCv;
    private TextInputEditText etName, etNic, etDob, etMobile, etHomePhone, etAddress, etJoinedDate;
    private RadioGroup rgGender, rgMarital;
    private Button btnSave;

    private int selectedImageType = 0;

    private String pathProfile = null;
    private String pathIdFront = null;
    private String pathIdBack = null;
    private String pathCv = null;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            handleImageSelection(uri);
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        imgProfile = findViewById(R.id.imgProfile);
        imgIdFront = findViewById(R.id.imgIdFront);
        imgIdBack = findViewById(R.id.imgIdBack);
        imgCv = findViewById(R.id.imgCv);

        etName = findViewById(R.id.etName);
        etNic = findViewById(R.id.etNic);
        etDob = findViewById(R.id.etDob);
        etMobile = findViewById(R.id.etMobile);
        etHomePhone = findViewById(R.id.etHomePhone);
        etAddress = findViewById(R.id.etAddress);
        etJoinedDate = findViewById(R.id.etJoinedDate);

        rgGender = findViewById(R.id.rgGender);
        rgMarital = findViewById(R.id.rgMarital);

        btnSave = findViewById(R.id.btnSave);
    }

    private void setupClickListeners() {
        imgProfile.setOnClickListener(v -> openGallery(1));
        imgIdFront.setOnClickListener(v -> openGallery(2));
        imgIdBack.setOnClickListener(v -> openGallery(3));
        imgCv.setOnClickListener(v -> openGallery(4));

        etDob.setOnClickListener(v -> showDatePicker(etDob));
        etJoinedDate.setOnClickListener(v -> showDatePicker(etJoinedDate));

        btnSave.setOnClickListener(v -> saveEmployee());
    }

    private void openGallery(int type) {
        selectedImageType = type;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void handleImageSelection(Uri sourceUri) {
        String localPath = ImageUtils.copyImageToAppStorage(this, sourceUri);

        if (localPath != null) {
            File imgFile = new File(localPath);
            Uri displayUri = Uri.fromFile(imgFile);

            switch (selectedImageType) {
                case 1:
                    pathProfile = localPath;
                    imgProfile.setImageURI(displayUri);
                    break;
                case 2:
                    pathIdFront = localPath;
                    imgIdFront.setImageURI(displayUri);
                    break;
                case 3:
                    pathIdBack = localPath;
                    imgIdBack.setImageURI(displayUri);
                    break;
                case 4:
                    pathCv = localPath;
                    imgCv.setImageURI(displayUri);
                    break;
            }
        } else {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePicker(TextInputEditText targetEditText) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    // Format: YYYY-MM-DD
                    String date = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    targetEditText.setText(date);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void saveEmployee() {
        String name = etName.getText().toString().trim();
        String nic = etNic.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String home = etHomePhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String joined = etJoinedDate.getText().toString().trim();

        String gender = "";
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId != -1) {
            RadioButton rb = findViewById(selectedGenderId);
            gender = rb.getText().toString();
        }

        String marital = "";
        int selectedMaritalId = rgMarital.getCheckedRadioButtonId();
        if (selectedMaritalId != -1) {
            RadioButton rb = findViewById(selectedMaritalId);
            marital = rb.getText().toString();
        }

        if (!InputValidator.isValidName(name)) {
            etName.setError("Name required");
            return;
        }
        if (!InputValidator.isValidPhone(mobile)) {
            etMobile.setError("Invalid Mobile");
            return;
        }
        if (gender.isEmpty()) {
            Toast.makeText(this, "Select Gender", Toast.LENGTH_SHORT).show();
            return;
        }

        Employee newEmp = new Employee(
                name, nic, gender, dob, marital,
                mobile, home, address, joined,
                pathProfile, pathIdFront, pathIdBack, pathCv
        );

        EmployeeDAO dao = new EmployeeDAO(this);
        boolean success = dao.addEmployee(newEmp);

        if (success) {
            Toast.makeText(this, "Employee Saved Successfully!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show();
        }
    }
}