package lk.businessmanagement.staffcore.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import lk.businessmanagement.staffcore.R;
import lk.businessmanagement.staffcore.data.EmployeeDAO;
import lk.businessmanagement.staffcore.model.Employee;
import lk.businessmanagement.staffcore.utils.AnimationHelper;
import lk.businessmanagement.staffcore.utils.ImageUtils;
import lk.businessmanagement.staffcore.utils.InputValidator;

public class AddEmployeeActivity extends AppCompatActivity {

    // --- UI Components ---
    private ImageView btnBack;
    private CircleImageView imgProfile;
    private ImageView imgIdFront, imgIdBack, imgCv;
    private TextInputEditText etName, etNic, etDob, etMobile, etHomePhone, etAddress, etJoinedDate;
    private RadioGroup rgGender, rgMarital;
    private MaterialButton btnSave;

    // --- Animation Views ---
    private ImageView glowTop, glowBottom;

    // --- Data Variables ---
    private int selectedImageType = 0; // 1=Profile, 2=ID_Front, 3=ID_Back, 4=CV
    private String pathProfile = null;
    private String pathIdFront = null;
    private String pathIdBack = null;
    private String pathCv = null;

    // --- Image Picker Launcher ---
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        handleImageSelection(uri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Edge-to-Edge Setup
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_add_employee);

        // 2. Fix System Bar Overlaps
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        initViews();

        // 3. Start Background Animation
        AnimationHelper.animateBackground(glowTop, glowBottom);

        setupClickListeners();
    }

    private void initViews() {
        // Header & Animation
        btnBack = findViewById(R.id.btnBack);
        glowTop = findViewById(R.id.glowTop);
        glowBottom = findViewById(R.id.glowBottom);

        // Images
        imgProfile = findViewById(R.id.imgProfile);
        imgIdFront = findViewById(R.id.imgIdFront);
        imgIdBack = findViewById(R.id.imgIdBack);
        imgCv = findViewById(R.id.imgCv);

        // Text Fields
        etName = findViewById(R.id.etName);
        etNic = findViewById(R.id.etNic);
        etDob = findViewById(R.id.etDob);
        etMobile = findViewById(R.id.etMobile);
        etHomePhone = findViewById(R.id.etHomePhone);
        etAddress = findViewById(R.id.etAddress);
        etJoinedDate = findViewById(R.id.etJoinedDate);

        // Radio Groups
        rgGender = findViewById(R.id.rgGender);
        rgMarital = findViewById(R.id.rgMarital);

        // Button
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Image Selection
        imgProfile.setOnClickListener(v -> openGallery(1));
        imgIdFront.setOnClickListener(v -> openGallery(2));
        imgIdBack.setOnClickListener(v -> openGallery(3));
        imgCv.setOnClickListener(v -> openGallery(4));

        // Date Pickers
        etDob.setOnClickListener(v -> showDatePicker(etDob, true)); // DOB requires past date
        etJoinedDate.setOnClickListener(v -> showDatePicker(etJoinedDate, false)); // Joined date can be recent

        // Save Action
        btnSave.setOnClickListener(v -> validateAndSave());
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

    private void showDatePicker(TextInputEditText targetEditText, boolean limitToPast) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // If field already has a date, use it
        // (Optional enhancement logic could go here)

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, (month1 + 1), dayOfMonth);
                    targetEditText.setText(date);
                },
                year, month, day);

        if (limitToPast) {
            // උපන් දිනය අදට වඩා වැඩි වෙන්න බෑ. (අවුරුදු 18ක් අඩු විය යුතුයි වගේ Logic එකක් ඕන නම් මෙතන දාන්න පුළුවන්)
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }

        datePickerDialog.show();
    }

    private void validateAndSave() {
        // 1. Extract Data
        String name = etName.getText().toString().trim();
        String nic = etNic.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String home = etHomePhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String joined = etJoinedDate.getText().toString().trim();

        // 2. Validate Fields
        boolean isValid = true;

        if (!InputValidator.isValidName(name)) {
            etName.setError("Valid name required (min 3 chars)");
            isValid = false;
        }

        if (!InputValidator.isValidNIC(nic)) {
            etNic.setError("Invalid NIC Format");
            isValid = false;
        }

        if (!InputValidator.isValidPhone(mobile)) {
            etMobile.setError("Invalid Mobile (07xxxxxxxx)");
            isValid = false;
        }

        // Home phone is optional, but if entered, must be valid
        if (!home.isEmpty() && !InputValidator.isValidPhone(home)) {
            etHomePhone.setError("Invalid Phone");
            isValid = false;
        }

        if (dob.isEmpty()) {
            etDob.setError("Required");
            isValid = false;
        }

        if (joined.isEmpty()) {
            etJoinedDate.setError("Required");
            isValid = false;
        }

        if (address.isEmpty()) {
            etAddress.setError("Address required");
            isValid = false;
        }

        // Validate Radio Groups
        String gender = "";
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Please select Gender", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            RadioButton rb = findViewById(selectedGenderId);
            gender = rb.getText().toString();
        }

        String marital = "";
        int selectedMaritalId = rgMarital.getCheckedRadioButtonId();
        if (selectedMaritalId == -1) {
            Toast.makeText(this, "Please select Marital Status", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            RadioButton rb = findViewById(selectedMaritalId);
            marital = rb.getText().toString();
        }

        // Validate Critical Images (Profile Pic Optional? Let's make it mandatory)
        if (pathProfile == null) {
            Toast.makeText(this, "Profile Photo Required", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // --- STOP IF INVALID ---
        if (!isValid) return;

        // 3. Create & Save Object
        Employee newEmp = new Employee(
                name, nic, gender, dob, marital,
                mobile, home, address, joined,
                pathProfile, pathIdFront, pathIdBack, pathCv
        );

        EmployeeDAO dao = new EmployeeDAO(this);
        if (dao.addEmployee(newEmp)) {
            Toast.makeText(this, "Employee Added Successfully!", Toast.LENGTH_LONG).show();
            finish(); // Close activity
        } else {
            Toast.makeText(this, "Database Error: Could not save.", Toast.LENGTH_SHORT).show();
        }
    }
}