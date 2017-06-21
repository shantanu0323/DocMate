package shantanu.docmate;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PatientProfile extends AppCompatActivity {

    private FloatingActionButton bDelete;
    private DatabaseReference currentPatient;
    private String patientUid;
    private String doctorUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Explode explode = new Explode();
        explode.setDuration(1000);
        getWindow().setEnterTransition(explode);

        setContentView(R.layout.activity_patient_profile);

        init();

        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPatient.removeValue();
                finish();
            }
        });
    }

    private void init() {
        patientUid = getIntent().getStringExtra("patientUid");
        doctorUid = getIntent().getStringExtra("doctorUid");
        bDelete = (FloatingActionButton) findViewById(R.id.bDelete);
        currentPatient = FirebaseDatabase.getInstance().getReference().child("doctor").child(doctorUid).child("patients").child(patientUid);
    }
}
