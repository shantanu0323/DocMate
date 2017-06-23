package shantanu.docmate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jp.wasabeef.glide.transformations.BlurTransformation;
import shantanu.docmate.Data.Doctor;

public class DoctorProfile extends AppCompatActivity {

    private static final String TAG = "DoctorProfile";

    FirebaseAuth auth;
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

        setContentView(R.layout.activity_doctor_profile);

        init();
        setDoctorData();

        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + tvAddress.getText().toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

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
    }

    private void init() {
        auth = FirebaseAuth.getInstance();
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

    private void setDoctorData() {
        DatabaseReference databaseDoctors = FirebaseDatabase.getInstance().getReference().child("doctor")
                .child(auth.getCurrentUser().getUid());
        databaseDoctors.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Doctor doctor = new Doctor(dataSnapshot.child("profilepic").getValue().toString(),
                        dataSnapshot.child("name").getValue().toString(),
                        dataSnapshot.child("degree").getValue().toString(),
                        dataSnapshot.child("address").getValue().toString(),
                        dataSnapshot.child("phone").getValue().toString(),
                        dataSnapshot.child("specialization").getValue().toString(),
                        dataSnapshot.child("email").getValue().toString());
                ((TextView) findViewById(R.id.tvName)).setText(doctor.getName());
                ((TextView) findViewById(R.id.tvDegree)).setText(doctor.getDegree());
                tvAddress.setText(doctor.getAddress());
                tvPhone.setText(doctor.getPhone());
                ((TextView) findViewById(R.id.tvSpecialization)).setText(doctor.getSpecialization());
                tvEmail.setText(doctor.getEmail());
                (new AQuery(getApplicationContext())).id((ImageView) findViewById(R.id.profilepic)).image(doctor.getProfilepic(), true, true, 60, R.drawable.default_image);
                (new AQuery(getApplicationContext())).id((ImageView) findViewById(R.id.profilePicBG))
                        .image(doctor.getProfilepic(), true, true,
                                getWindowManager().getDefaultDisplay().getWidth(), R.drawable.default_image);
                Glide.with(getApplicationContext()).load(((ImageView) findViewById(R.id.profilePicBG)).getDrawingCache())
                        .bitmapTransform(new BlurTransformation(getApplicationContext()))
                        .into((ImageView) findViewById(R.id.profilePicBG));
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
