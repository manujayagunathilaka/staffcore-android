package lk.businessmanagement.staffcore.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.yalantis.ucrop.UCrop;

import java.io.File;
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
    private View btnSelectIdFront, btnSelectIdBack, btnSelectCv;
    // Containers
//    private LinearLayout btnSelectIdFront, btnSelectIdBack, btnSelectCv;

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

    // 1. Gallery Launcher (Photo එක තෝරාගැනීමට)
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri sourceUri = result.getData().getData();
                    if (sourceUri != null) {
                        startCrop(sourceUri); // තෝරාගත් පසු Crop කිරීමට යවයි
                    }
                }
            }
    );

    // 2. Crop Launcher (Crop කළ Photo එක ලබාගැනීමට)
    private final ActivityResultLauncher<Intent> cropLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    final Uri resultUri = UCrop.getOutput(result.getData());
                    if (resultUri != null) {
                        handleCropResult(resultUri); // Crop වූ Photo එක Save කරයි
                    }
                } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                    Toast.makeText(this, "Crop Error!", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        initViews();
        AnimationHelper.animateBackground(glowTop, glowBottom);
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        glowTop = findViewById(R.id.glowTop);
        glowBottom = findViewById(R.id.glowBottom);

        imgProfile = findViewById(R.id.imgProfile);
        imgIdFront = findViewById(R.id.imgIdFront);
        imgIdBack = findViewById(R.id.imgIdBack);
        imgCv = findViewById(R.id.imgCv);

        btnSelectIdFront = findViewById(R.id.btnSelectIdFront);
        btnSelectIdBack = findViewById(R.id.btnSelectIdBack);
        btnSelectCv = findViewById(R.id.btnSelectCv);

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
        btnBack.setOnClickListener(v -> finish());

        imgProfile.setOnClickListener(v -> openGallery(1));
        btnSelectIdFront.setOnClickListener(v -> openGallery(2));
        btnSelectIdBack.setOnClickListener(v -> openGallery(3));
        btnSelectCv.setOnClickListener(v -> openGallery(4));

        etDob.setOnClickListener(v -> showDatePicker(etDob));
        etJoinedDate.setOnClickListener(v -> showDatePicker(etJoinedDate));

        btnSave.setOnClickListener(v -> saveEmployee());
    }

    private void openGallery(int type) {
        selectedImageType = type;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    // --- CROP FUNCTION ---
    private void startCrop(Uri sourceUri) {
        // තාවකාලික ගොනුවක් සාදා ගනී (Destination Uri)
        String fileName = "crop_" + System.currentTimeMillis() + ".jpg";
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), fileName));

        UCrop.Options options = new UCrop.Options();

        // Crop UI Colors (අපේ Gold Theme එකට ගැලපෙන්න)
        options.setToolbarColor(ContextCompat.getColor(this, R.color.bg_dark));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.bg_dark));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.neon_gold));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.neon_gold));

        // Profile එකට විතරක් Square Crop (1:1), අනිත් ඒවට Free Crop
        if (selectedImageType == 1) {
            options.withAspectRatio(1, 1);
        } else {
            options.useSourceImageAspectRatio();
        }

        // Start uCrop Activity
        Intent intent = UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .getIntent(this);

        cropLauncher.launch(intent);
    }

    // --- SAVE & DISPLAY ---
    private void handleCropResult(Uri croppedUri) {
        // Crop කළ Image එක අපේ App Storage එකට Copy කිරීම
        String localPath = ImageUtils.copyImageToAppStorage(this, croppedUri);

        if (localPath != null) {
            File imgFile = new File(localPath);

            switch (selectedImageType) {
                case 1:
                    pathProfile = localPath;
                    // Profile එක CircleImageView නිසා Scale Type වෙනස් කරන්න ඕන නෑ
                    Glide.with(this).load(imgFile)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imgProfile);
                    break;
                case 2:
                    pathIdFront = localPath;
                    loadImageToView(imgIdFront, imgFile); // Helper Method එක යොදාගැනීම
                    break;
                case 3:
                    pathIdBack = localPath;
                    loadImageToView(imgIdBack, imgFile);
                    break;
                case 4:
                    pathCv = localPath;
                    loadImageToView(imgCv, imgFile);
                    break;
            }
        } else {
            Toast.makeText(this, "Save Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    // --- HELPER METHOD TO FIX IMAGE DISPLAY ---
    private void loadImageToView(ImageView view, File imageFile) {
        // 1. Tint එක අයින් කරන්න (නැත්නම් පින්තූරේ අළු පාට වෙලා පේන්නේ)
        view.setColorFilter(null);
        view.setImageTintList(null);

        // 2. Image එක කොටුව පිරෙන්න ලොකු කරන්න
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // 3. Glide වලින් Load කරන්න
        Glide.with(this)
                .load(imageFile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(view);
    }

    private void showDatePicker(TextInputEditText targetEditText) {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, (month + 1), day);
            targetEditText.setText(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showStatusDialog(boolean isSuccess, String title, String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.layout_dialog_glass, null);
        builder.setView(view);
        android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageView icon = view.findViewById(R.id.dialogIcon);
        TextView tvTitle = view.findViewById(R.id.dialogTitle);
        TextView tvMessage = view.findViewById(R.id.dialogMessage);
        MaterialButton btnOk = view.findViewById(R.id.dialogButton);

        tvTitle.setText(title);
        tvMessage.setText(message);

        if (isSuccess) {
            icon.setImageResource(android.R.drawable.checkbox_on_background);
            icon.setColorFilter(getColor(R.color.status_green));
            btnOk.setBackgroundColor(getColor(R.color.status_green));
            btnOk.setText("Great!");
            btnOk.setOnClickListener(v -> {
                dialog.dismiss();
                finish(); // Close activity only on success
            });
        } else {
            icon.setImageResource(android.R.drawable.ic_delete);
            icon.setColorFilter(getColor(R.color.status_red));
            btnOk.setBackgroundColor(getColor(R.color.status_red));
            btnOk.setText("Try Again");
            btnOk.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }

    private void saveEmployee() {
        // 1. Get Data
        String name = etName.getText().toString().trim();
        String nic = etNic.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String home = etHomePhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String joined = etJoinedDate.getText().toString().trim();

        // Radio Groups
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

        // --- VALIDATION (TOP TO BOTTOM) ---

        // 1. Photo Validation
//        if (pathProfile == null) {
//            showStatusDialog(false, "Photo Required", "Please upload a profile photo.");
//            return;
//        }

        // 2. Name
        if (!InputValidator.isValidName(name)) {
            etName.setError("Valid name required (min 3 chars)");
            etName.requestFocus();
            return;
        }

        // 3. NIC
        if (!InputValidator.isValidNIC(nic)) {
            etNic.setError("Invalid NIC number");
            etNic.requestFocus();
            return;
        }

        // 4. Gender
        if (gender.isEmpty()) {
            showStatusDialog(false, "Gender Missing", "Please select gender.");
            rgGender.requestFocus();
            return;
        }

        // 5. DOB
        if (dob.isEmpty()) {
            etDob.setError("Date of Birth required");
            showStatusDialog(false, "Date Missing", "Please select Date of Birth.");
            return;
        }

        // 6. Marital Status
        if (marital.isEmpty()) {
            showStatusDialog(false, "Status Missing", "Please select Marital Status.");
            rgMarital.requestFocus();
            return;
        }

        // 7. Mobile (Length check is handled by XML, but good to check format)
        if (!InputValidator.isValidPhone(mobile)) {
            etMobile.setError("Invalid Mobile Number (07...)");
            etMobile.requestFocus();
            return;
        }

        // 8. Address
        if (address.isEmpty()) {
            etAddress.setError("Address required");
            etAddress.requestFocus();
            return;
        }

        // 9. Joined Date
        if (joined.isEmpty()) {
            etJoinedDate.setError("Joined Date required");
            showStatusDialog(false, "Date Missing", "Please select Joined Date.");
            return;
        }

        // If All Good -> Save
        Employee newEmp = new Employee(
                name, nic, gender, dob, marital,
                mobile, home, address, joined,
                pathProfile, pathIdFront, pathIdBack, pathCv
        );

        EmployeeDAO dao = new EmployeeDAO(this);
        if (dao.addEmployee(newEmp)) {
            showStatusDialog(true, "Success!", "Employee profile created successfully.");
        } else {
            showStatusDialog(false, "Database Error", "Failed to save data. Please try again.");
        }
    }
}