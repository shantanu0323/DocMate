package shantanu.docmate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

public class PatientRegistration extends AppCompatActivity {

    private ImageButton bAddImage;
    private RadioGroup rgGender;
    private EditText etName,etAge, etDisease, etPhone, etBloodGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Explode explode = new Explode();
        explode.setDuration(1000);
        getWindow().setEnterTransition(explode);

        setContentView(R.layout.activity_patient_registration);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_background));
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();
    }

    private void init() {
        bAddImage = (ImageButton) findViewById(R.id.bAddImage);
        rgGender = (RadioGroup) findViewById(R.id.rgGender);
        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);
        etDisease = (EditText) findViewById(R.id.etDiseases);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etBloodGroup = (EditText) findViewById(R.id.etBloodGroup);

    }
}
