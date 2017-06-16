package shantanu.docmate;

import android.support.annotation.IdRes;
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

import shantanu.docmate.Data.Doctor;

public class DoctorRegistration extends AppCompatActivity {

    private static final String TAG = "DoctorRegistration";
    private static final String DEFAULT_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/docmate-d670e.appspot.com/o/default_image.png?alt=media&token=5431adb3-2946-41e5-9b67-4cf07b4aaa78";
    private ImageButton bAddImage;
    private RadioGroup rgGender;
    private EditText etName, etEmail, etDegree, etAddress, etPhone, etSpecialization, etPassword;
    private TextInputLayout nameLabel, emailLabel, degreeLabel, addressLabel, phoneLabel,
            specilizationLabel, passwordLabel;
    private Button bRegister;

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
                Toast.makeText(DoctorRegistration.this, "clicked", Toast.LENGTH_SHORT).show();
                Doctor doctor = null;
                try {
                    doctor = new Doctor(DEFAULT_IMAGE_URL,
                            getGender(),
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


    }

    private String getGender() {
        Log.i(TAG, "getGender: Started");
        Toast.makeText(this, "getGender", Toast.LENGTH_SHORT).show();
        final String[] gender = new String[1];
        if (rgGender != null) {
            rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    switch (checkedId) {
                        case R.id.rbMale:
                            gender[0] = "Male";
                            break;
                        case R.id.rbFemale:
                            gender[0] = "Female";
                            break;
                    }
                }
            });
        } else {
            Log.i(TAG, "getGender: rgGender == null");
        }
        return gender[0];
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


    }
}
