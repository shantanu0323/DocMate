package shantanu.docmate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import jp.wasabeef.glide.transformations.BlurTransformation;
import shantanu.docmate.Data.Patient;

public class PatientProfile extends AppCompatActivity {

    private FloatingActionButton bDelete;
    private FloatingActionButton bPrescribe;
    private DatabaseReference currentPatient;
    private String patientUid;
    private String doctorUid;
    private LinearLayout profilepicContainer;
    private TextView tvAddress;
    private TextView tvPhone;
    private TextView tvEmail;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Explode explode = new Explode();
        explode.setDuration(1000);
        getWindow().setEnterTransition(explode);

        setContentView(R.layout.activity_patient_profile);

        init();

        setPatientData();

        tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "tel://" + tvPhone.getText().toString().trim();
                Uri phoneUri = Uri.parse(phone);
                Intent intent = new Intent(Intent.ACTION_CALL, phoneUri);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + tvEmail.getText().toString()));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sample Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Sample Body");
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
            }
        });
        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPatient.removeValue();
                finish();
            }
        });

        bPrescribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EPrescription.class);
                intent.putExtra("patientUid", patientUid);
                intent.putExtra("doctorUid", doctorUid);
                startActivity(intent);
            }
        });
    }

    private void setPatientData() {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:530266078999:android:481c4ecf3253701e") // Required for Analytics.
                .setApiKey("AIzaSyBRxOyIj5dJkKgAVPXRLYFkdZwh2Xxq51k") // Required for Auth.
                .setDatabaseUrl("https://seeadoc-d731b.firebaseio.com/")// Required for RTDB.
                .build();
        String patientApp = UUID.randomUUID().toString();
        FirebaseApp patientApplication = FirebaseApp.initializeApp(this, options, patientApp);
        // Retrieve my other app.
//             Get the database for the other app.
        FirebaseDatabase patientDatabase = FirebaseDatabase.getInstance(patientApplication);

        DatabaseReference selectedPatient = patientDatabase.getReference()
                .child("patient").child(patientUid);
        selectedPatient.keepSynced(true);

        selectedPatient.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Patient patient = new Patient(dataSnapshot.child("profilepic").getValue().toString(),
                        dataSnapshot.child("gender").getValue().toString(),
                        dataSnapshot.child("name").getValue().toString(),
                        dataSnapshot.child("age").getValue().toString(),
                        dataSnapshot.child("diseases").getValue().toString(),
                        dataSnapshot.child("bloodgroup").getValue().toString(),
                        dataSnapshot.child("email").getValue().toString(),
                        dataSnapshot.child("phone").getValue().toString());

                ((TextView) findViewById(R.id.tvName)).setText(patient.getName());
                ((TextView) findViewById(R.id.tvAge)).setText(patient.getAge() + " yrs");
                ((TextView) findViewById(R.id.tvDiseases)).setText("Ailment(s): " + patient.getDiseases());
                ((TextView) findViewById(R.id.tvBloodgroup)).setText(patient.getBloodgroup());
                tvPhone.setText(patient.getPhone());
                tvEmail.setText(patient.getEmail());
                (new AQuery(getApplicationContext())).id((ImageView) findViewById(R.id.profilepic)).image(patient.getProfilepic(), true, true, 60, R.drawable.default_image);
                (new AQuery(getApplicationContext())).id((ImageView) findViewById(R.id.profilePicBG))
                        .image(patient.getProfilepic(), true, true,
                                getWindowManager().getDefaultDisplay().getWidth(), R.drawable.default_image);
                Glide.with(getApplicationContext()).load(((ImageView) findViewById(R.id.profilePicBG)).getDrawingCache())
                        .bitmapTransform(new BlurTransformation(getApplicationContext()))
                        .into((ImageView) findViewById(R.id.profilePicBG));
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PatientProfile.this, "ERROR Loading Profile : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void init() {
        patientUid = getIntent().getStringExtra("patientUid");
        doctorUid = getIntent().getStringExtra("doctorUid");
        bDelete = (FloatingActionButton) findViewById(R.id.bDelete);
        bPrescribe = (FloatingActionButton) findViewById(R.id.bPrescribe);
        currentPatient = FirebaseDatabase.getInstance().getReference().child("doctor").child(doctorUid).child("patients").child(patientUid);
        profilepicContainer = (LinearLayout) findViewById(R.id.profilepicContainer);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        profilepicContainer.startAnimation(animation);
        tvAddress = ((TextView) findViewById(R.id.tvAddress));
        tvPhone = ((TextView) findViewById(R.id.tvPhone));
        tvEmail = ((TextView) findViewById(R.id.tvEmail));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Profile...");
        progressDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
