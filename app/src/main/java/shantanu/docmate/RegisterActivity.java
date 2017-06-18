package shantanu.docmate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {

    private Button bDoctor;
    private Button bPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Explode explode = new Explode();
        explode.setDuration(1000);
        getWindow().setEnterTransition(explode);

        setContentView(R.layout.activity_register);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_background));
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        bDoctor = (Button) findViewById(R.id.bDoctor);
        bPatient = (Button) findViewById(R.id.bPatient);

        bDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DoctorRegistration.class);
                startActivity(intent, ActivityOptionsCompat
                        .makeSceneTransitionAnimation(RegisterActivity.this, null).toBundle());
            }
        });

        bPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PatientRegistration.class);
                startActivity(intent, ActivityOptionsCompat
                        .makeSceneTransitionAnimation(RegisterActivity.this, null).toBundle());
            }
        });
    }


}
