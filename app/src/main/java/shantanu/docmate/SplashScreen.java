package shantanu.docmate;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        Animation stethoscopeAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim
                .animate_stethoscope);
        ImageView stethoscope = (ImageView) findViewById(R.id.stethoscope);
        stethoscope.startAnimation(stethoscopeAnimation);

        Animation doctorAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim
                .animate_doctor);
        ImageView doctor = (ImageView) findViewById(R.id.doctor);
//        doctor.startAnimation(doctorAnimation);

        FrameLayout frameLayout = ((FrameLayout) findViewById(R.id.frameLayout));
        frameLayout.setPivotX(frameLayout.getMeasuredWidth() / 2);
        frameLayout.setPivotY(frameLayout.getMeasuredHeight() / 2);
        frameLayout.startAnimation(doctorAnimation);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "lifeline.ttf");
        ((TextView) findViewById(R.id.title1)).setTypeface(typeface);
        ((TextView) findViewById(R.id.title2)).setTypeface(typeface);
        ((TextView) findViewById(R.id.title3)).setTypeface(typeface);
        ((TextView) findViewById(R.id.title4)).setTypeface(typeface);
        ((TextView) findViewById(R.id.title5)).setTypeface(typeface);
        ((TextView) findViewById(R.id.title6)).setTypeface(typeface);
        ((TextView) findViewById(R.id.title7)).setTypeface(typeface);

        ((TextView) findViewById(R.id.title1)).startAnimation(
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animate_title1));
        ((TextView) findViewById(R.id.title2)).startAnimation(
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animate_title2));
        ((TextView) findViewById(R.id.title3)).startAnimation(
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animate_title3));
        ((TextView) findViewById(R.id.title4)).startAnimation(
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animate_title4));
        ((TextView) findViewById(R.id.title5)).startAnimation(
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animate_title5));
        ((TextView) findViewById(R.id.title6)).startAnimation(
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animate_title6));
        ((TextView) findViewById(R.id.title7)).startAnimation(
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animate_title7));

    }
}
