package shantanu.docmate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;

public class DoctorProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Explode explode = new Explode();
        explode.setDuration(1000);
        getWindow().setEnterTransition(explode);

        setContentView(R.layout.activity_doctor_profile);

        init();
    }

    private void init() {

    }
}
