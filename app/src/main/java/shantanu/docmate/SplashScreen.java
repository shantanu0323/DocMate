package shantanu.docmate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Animation stethoscopeAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim
                .animate_stethoscope);
        ImageView stethoscope = (ImageView) findViewById(R.id.stethoscope);
        stethoscope.startAnimation(stethoscopeAnimation);

        Animation doctorAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim
                .animate_doctor);
        ImageView doctor = (ImageView) findViewById(R.id.doctor);
        doctor.startAnimation(doctorAnimation);

    }
}
