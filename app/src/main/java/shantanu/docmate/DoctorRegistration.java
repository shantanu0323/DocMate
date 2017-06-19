package shantanu.docmate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import shantanu.docmate.Data.Doctor;

public class DoctorRegistration extends AppCompatActivity {

    private static final String TAG = "DoctorRegistration";
    private static final String DEFAULT_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/docmate-d670e.appspot.com/o/default_image.png?alt=media&token=5431adb3-2946-41e5-9b67-4cf07b4aaa78";
    private static final int GALLERY_REQUEST = 10;
    private static ProgressDialog progressDialog;
    private ImageButton bAddImage;
    private RadioGroup rgGender;
    private EditText etName, etEmail, etDegree, etAddress, etPhone, etSpecialization, etPassword;
    private TextInputLayout nameLabel, emailLabel, degreeLabel, addressLabel, phoneLabel,
            specilizationLabel, passwordLabel;
    private Button bRegister;
    private Activity activity = this;
    private String gender = "Male";
    private Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Explode explode = new Explode();
        explode.setDuration(1000);
        getWindow().setEnterTransition(explode);

        setContentView(R.layout.activity_doctor_registration);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_background));
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Doctor doctor = null;
                try {
                    doctor = new Doctor(DEFAULT_IMAGE_URL,
                            gender,
                            etName.getText().toString().trim(),
                            etDegree.getText().toString().trim(),
                            etAddress.getText().toString().trim(),
                            etPhone.getText().toString().trim(),
                            etSpecialization.getText().toString().trim(),
                            etEmail.getText().toString().trim(),
                            etPassword.getText().toString().trim());
                } catch (Exception e) {
                    Log.e(TAG, "onClick: ", e);
                    Toast.makeText(DoctorRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                if (isDataValid(doctor)) {
                    FirebaseHelper firebaseHelper = new FirebaseHelper();
                    firebaseHelper.registerDoctor(doctor);
                }
            }
        });

        bAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickUserImage();
            }
        });

        if (rgGender != null) {
            rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    switch (checkedId) {
                        case R.id.rbMale:
                            Log.i(TAG, "onCheckedChanged: Male");
                            gender = "Male";
                            break;
                        case R.id.rbFemale:
                            Log.i(TAG, "onCheckedChanged: Female");
                            gender = "Female";
                            break;
                    }
                }
            });
        } else {
            Log.i(TAG, "getGender: rgGender == null");
        }
    }

    private boolean isDataValid(Doctor doctor) {
        boolean dataValid = true;

        nameLabel.setErrorEnabled(false);
        emailLabel.setErrorEnabled(false);
        degreeLabel.setErrorEnabled(false);
        addressLabel.setErrorEnabled(false);
        phoneLabel.setErrorEnabled(false);
        specilizationLabel.setErrorEnabled(false);
        passwordLabel.setErrorEnabled(false);

        if (TextUtils.isEmpty(doctor.getName())) {
            nameLabel.setError("Field cannot be empty!!!");
            dataValid = false;
        }
        if (TextUtils.isEmpty(doctor.getEmail())) {
            emailLabel.setError("Field cannot be empty!!!");
            dataValid = false;
        } else if (!(doctor.getEmail().contains("@") && !doctor.getEmail().contains(" "))) {
            emailLabel.setError("Please enter a valid Email-ID");
            dataValid = false;
        }
        if (TextUtils.isEmpty(doctor.getDegree())) {
            degreeLabel.setError("Field cannot be empty!!!");
            dataValid = false;
        }
        if (TextUtils.isEmpty(doctor.getSpecializtion())) {
            specilizationLabel.setError("Field cannot be empty!!!");
            dataValid = false;
        }
        if (TextUtils.isEmpty(doctor.getPhone())) {
            phoneLabel.setError("Field cannot be empty!!!");
            dataValid = false;
        } else if (!(doctor.getPhone().matches("[0-9]+") &&
                doctor.getPhone().length() == 10)) {
            phoneLabel.setError("Please enter a valid Phone No");
            dataValid = false;
        }
        if (TextUtils.isEmpty(doctor.getAddress())) {
            addressLabel.setError("Field cannot be empty!!!");
            dataValid = false;
        }
        if (TextUtils.isEmpty(doctor.getPassword())) {
            passwordLabel.setError("Field cannot be empty!!!");
            dataValid = false;
        }

        return dataValid;
    }

    private void init() {
        bAddImage = (ImageButton) findViewById(R.id.bAddImage);
        rgGender = (RadioGroup) findViewById(R.id.rgGender);
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etDegree = (EditText) findViewById(R.id.etDegree);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etSpecialization = (EditText) findViewById(R.id.etSpecialization);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bRegister = (Button) findViewById(R.id.bRegister);

        nameLabel = (TextInputLayout) findViewById(R.id.nameLabel);
        emailLabel = (TextInputLayout) findViewById(R.id.emailLabel);
        degreeLabel = (TextInputLayout) findViewById(R.id.degreeLabel);
        phoneLabel = (TextInputLayout) findViewById(R.id.phoneLabel);
        addressLabel = (TextInputLayout) findViewById(R.id.addresssLabel);
        specilizationLabel = (TextInputLayout) findViewById(R.id.specializationLabel);
        passwordLabel = (TextInputLayout) findViewById(R.id.passwordLabel);

        progressDialog = new ProgressDialog(this);
    }

    private void pickUserImage() {
        Log.e(TAG, "pickUserImage: FUNCTION STARTED");
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            uri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                uri = resultUri;
                bAddImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "CROPPING UNSUCCESSFULL : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public class FirebaseHelper {

        private static final String TAG = "FirebaseHelper";

        private FirebaseAuth auth;
        private DatabaseReference databaseDoctor;
        private DatabaseReference databasePatient;
        private StorageReference storageProfilePics;

        public FirebaseHelper() {
            auth = FirebaseAuth.getInstance();
            databaseDoctor = FirebaseDatabase.getInstance().getReference().child("doctor");
            databasePatient = FirebaseDatabase.getInstance().getReference().child("patient");
            storageProfilePics = FirebaseStorage.getInstance().getReference();
        }

        public void registerDoctor(Doctor doctor) {
            startRegister(doctor);
        }

        private void startRegister(final Doctor doctor) {
            Log.e(TAG, "startRegister: REGISTERING USER...");
            progressDialog.setMessage("Registering User...");
            progressDialog.show();
            auth.createUserWithEmailAndPassword(doctor.getEmail(), doctor.getPassword())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.e(TAG, "onComplete: REGISTERING USER SUCCESSFULL");
                                String userId = auth.getCurrentUser().getUid();

                                uploadImage(doctor.getProfilePic());
                                DatabaseReference currentUser = databaseDoctor.child(userId);

                                currentUser.child("name").setValue(doctor.getName());
                                currentUser.child("email").setValue(doctor.getEmail());
                                currentUser.child("address").setValue(doctor.getAddress());
                                currentUser.child("degree").setValue(doctor.getDegree());
                                currentUser.child("gender").setValue(doctor.getGender());
                                currentUser.child("phone").setValue(doctor.getPhone());
                                currentUser.child("specialization").setValue(doctor.getSpecializtion());
                                currentUser.child("uid").setValue(auth.getCurrentUser().getUid());
                                progressDialog.dismiss();
                                Log.e(TAG, "onComplete: Redirecting to HomeActivity");
                                Toast.makeText(DoctorRegistration.this, "Registration Successfull !!!",
                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity
                                        .class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: REGISTRATION FAILED due to : " + e.getMessage());
                    progressDialog.dismiss();
                    Toast.makeText(DoctorRegistration.this, "REGISTRATION FAILED because : " +
                            e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        private void uploadImage(String profilePicURL) {
            Log.e(TAG, "uploadImage: FUNCTION STARTED");
            progressDialog.show();
            if (uri != null) {
                Log.e(TAG, "uploadImage: URI NOT NULL");
                StorageReference filePath = storageProfilePics.child("ProfilePics").child(uri
                        .getLastPathSegment());
                filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        DatabaseReference newDoctor = databaseDoctor.child(auth.getCurrentUser().getUid());
                        newDoctor.child("profilepic").setValue(downloadUrl.toString());
                        Log.e(TAG, "onSuccess: Image Added ...");
                        progressDialog.dismiss();
//                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to finish setup due to : " +
                                e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailure: FAILED TO FINISH SETUP");
                        progressDialog.dismiss();
                    }
                });
            } else {
                Log.e(TAG, "uploadImage: URI NULL");
                DatabaseReference newDoctor = databaseDoctor.child(auth.getCurrentUser().getUid());
                newDoctor.child("profilepic").setValue(profilePicURL);
                Log.e(TAG, "onSuccess: SETUP DONE ...");
                progressDialog.dismiss();
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }

    }

}
