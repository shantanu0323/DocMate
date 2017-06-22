package shantanu.docmate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.UUID;

import shantanu.docmate.Data.Doctor;
import shantanu.docmate.Data.Patient;

public class EPrescription extends AppCompatActivity {

    private static final String TAG = "EPrescription";
    private static final int TAKE_PICTURE = 10;
    private static final int GALLERY_REQUEST = 20;
    private static final String DEFAULT_PRESCRIPTION_IMAGE = "https://firebasestorage.googleapis.com/v0/b/seeadoc-d731b.appspot.com/o/default_prescription.jpg?alt=media&token=e2797911-3e01-4235-9742-700669573b85";

    private String patientUid;
    private String doctorUid;
    private String profilepic, name;
    private ImageButton bCamera;
    private ImageButton bGallery;
    private ImageButton bWrite;
    private LinearLayout buttonContainer;
    private EditText etPrescription;
    private FloatingActionButton bSend, bDiscard;
    private ImageView ivPrescription;

    private Uri imageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eprescription);

        init();

        setDoctorData();
        setPatientData();

        bCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        bGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickUserImage();
            }
        });

        bWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPrescription.setVisibility(View.VISIBLE);
                bSend.setVisibility(View.VISIBLE);
                bDiscard.setVisibility(View.VISIBLE);
                buttonContainer.setVisibility(View.GONE);
            }
        });

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uploadPrescription();
                } catch (Exception e) {
                    Log.e(TAG, "onClick: ERROR UPLOADING : ", e);
                }
            }
        });

        bDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPrescription.setVisibility(View.GONE);
                ivPrescription.setVisibility(View.GONE);
                buttonContainer.setVisibility(View.VISIBLE);
                bDiscard.setVisibility(View.GONE);
                bSend.setVisibility(View.GONE);
            }
        });

    }

    private void uploadPrescription() {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:530266078999:android:481c4ecf3253701e") // Required for Analytics.
                .setApiKey("AIzaSyBRxOyIj5dJkKgAVPXRLYFkdZwh2Xxq51k") // Required for Auth.
                .setDatabaseUrl("https://seeadoc-d731b.firebaseio.com/") // Required for RTDB.
                .build();
        String patientApp = UUID.randomUUID().toString();
        FirebaseApp patientApplication = FirebaseApp.initializeApp(this, options, patientApp);
        // Retrieve my other app.
//             Get the database for the other app.
        FirebaseDatabase patientDatabase = FirebaseDatabase.getInstance(patientApplication);
        final DatabaseReference databasePrescriptions = patientDatabase.getReference().child("patient")
                .child(patientUid).child("prescriptions");

        FirebaseStorage prescriptionsStorage = FirebaseStorage.getInstance(patientApplication);

        StorageReference storagePrescriptions = prescriptionsStorage.getReferenceFromUrl("gs://seeadoc-d731b.appspot.com/");

        Log.e(TAG, "uploadPrescription: FUNCTION STARTED");
        progressDialog.show();
        final String prescriptionKey = databasePrescriptions.push().getKey();
        if (imageUri != null) {
            Log.e(TAG, "uploadPrescription: URI NOT NULL");
            Log.i(TAG, "uploadPrescription: strorageReports : " + ((storagePrescriptions == null) ? "null" : "NOT null"));
            StorageReference filePath = storagePrescriptions.child("Prescriptions").child(UUID.randomUUID().toString());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    DatabaseReference newPrescription = databasePrescriptions.child(prescriptionKey);
                    newPrescription.child("image").setValue(downloadUrl.toString());
                    newPrescription.child("message").setValue(getMessage());
                    newPrescription.child("profilepic").setValue(profilepic);
                    newPrescription.child("uid").setValue(doctorUid);
                    newPrescription.child("name").setValue(name);

                    Log.e(TAG, "onSuccess: Report Image Added ...");
                    Toast.makeText(getApplicationContext(), "Report Sent Successfully", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to upload report due to : " +
                            e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onFailure: FAILED TO upload report");
                    progressDialog.dismiss();
                }
            });
        } else {
            Log.e(TAG, "uploadPrescription: REPORT IMAGE URI NULL");
            DatabaseReference newPrescription = databasePrescriptions.child(prescriptionKey);
            newPrescription.child("image").setValue(DEFAULT_PRESCRIPTION_IMAGE);
            newPrescription.child("message").setValue(getMessage());
            newPrescription.child("profilepic").setValue(profilepic);
            newPrescription.child("uid").setValue(doctorUid);
            newPrescription.child("name").setValue(name);

            Log.e(TAG, "onSuccess: Report Image Added ...");
            Toast.makeText(getApplicationContext(), "Report Sent Successfully", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            finish();
        }

    }

    private String getMessage() {
        String message = "Please Refer to the provided prescription and take the meds as instructed...";
        if (!TextUtils.isEmpty(etPrescription.getText().toString().trim())) {
            message = etPrescription.getText().toString().trim();
        }
        return message;
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    private void pickUserImage() {
        Log.e(TAG, "pickUserImage: FUNCTION STARTED");
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    if (selectedImage != null) {
                        ivPrescription.setImageURI(selectedImage);
                        buttonContainer.setVisibility(View.GONE);
                        ivPrescription.setVisibility(View.VISIBLE);
                        bSend.setVisibility(View.VISIBLE);
                        bDiscard.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.e(TAG, "onActivityResult: UNSUCESSFULL : resultCode = " + resultCode);
                }
                break;
            case GALLERY_REQUEST:
                if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    ivPrescription.setImageURI(imageUri);
                    buttonContainer.setVisibility(View.GONE);
                    ivPrescription.setVisibility(View.VISIBLE);
                    bSend.setVisibility(View.VISIBLE);
                    bDiscard.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void setDoctorData() {
        DatabaseReference databaseDoctors = FirebaseDatabase.getInstance().getReference().child("doctor").child(doctorUid);
        databaseDoctors.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profilepic = dataSnapshot.child("profilepic").getValue().toString();
                name = dataSnapshot.child("name").getValue().toString();

                Doctor doctor = new Doctor(dataSnapshot.child("profilepic").getValue().toString(),
                        dataSnapshot.child("name").getValue().toString(),
                        dataSnapshot.child("degree").getValue().toString(),
                        dataSnapshot.child("address").getValue().toString(),
                        dataSnapshot.child("phone").getValue().toString(),
                        dataSnapshot.child("specialization").getValue().toString(),
                        dataSnapshot.child("email").getValue().toString());
                ((TextView) findViewById(R.id.tvDoctorName)).setText(doctor.getName());
                ((TextView) findViewById(R.id.tvDegree)).setText(doctor.getDegree());
                ((TextView) findViewById(R.id.tvDoctorAddress)).setText(doctor.getAddress());
                ((TextView) findViewById(R.id.tvDoctorPhone)).setText(doctor.getPhone());
                ((TextView) findViewById(R.id.tvSpecialization)).setText(doctor.getSpecialization());
                ((TextView) findViewById(R.id.tvDoctorEmail)).setText(doctor.getEmail());
                (new AQuery(getApplicationContext())).id((ImageView) findViewById(R.id.ivDoctorProfile)).image(doctor.getProfilepic(), true, true, 60, R.drawable.default_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setPatientData() {
        DatabaseReference selectedPatient = FirebaseDatabase.getInstance().getReference()
                .child("doctor").child(doctorUid).child("patients").child(patientUid);
        selectedPatient.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Patient patient = new Patient(dataSnapshot.child("profilepic").getValue().toString(),
                        dataSnapshot.child("gender").getValue().toString(),
                        dataSnapshot.child("name").getValue().toString(),
                        dataSnapshot.child("age").getValue().toString(),
                        dataSnapshot.child("diseases").getValue().toString(),
                        dataSnapshot.child("bloodgroup").getValue().toString());

                ((TextView) findViewById(R.id.tvPatientName)).setText(patient.getName());
                ((TextView) findViewById(R.id.tvGender)).setText("(" + patient.getGender() + ": ");
                ((TextView) findViewById(R.id.tvAge)).setText(patient.getAge() + " yrs)");
                ((TextView) findViewById(R.id.tvDiseases)).setText("Ailment(s): " + patient.getDiseases());
                ((TextView) findViewById(R.id.tvBloodgroup)).setText(patient.getBloodgroup());
                (new AQuery(getApplicationContext())).id((ImageView) findViewById(R.id.ivPatientProfile)).image(patient.getProfilepic(), true, true, 60, R.drawable.default_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void init() {
        patientUid = getIntent().getStringExtra("patientUid");
        doctorUid = getIntent().getStringExtra("doctorUid");

        bCamera = (ImageButton) findViewById(R.id.bCamera);
        bGallery = (ImageButton) findViewById(R.id.bGallery);
        bWrite = (ImageButton) findViewById(R.id.bWrite);
        buttonContainer = (LinearLayout) findViewById(R.id.buttonContainer);
        etPrescription = (EditText) findViewById(R.id.etPrescription);
        bSend = (FloatingActionButton) findViewById(R.id.bSend);
        bDiscard = (FloatingActionButton) findViewById(R.id.bDiscard);
        ivPrescription = (ImageView) findViewById(R.id.ivPrescription);
        progressDialog = new ProgressDialog(this);
    }
}
