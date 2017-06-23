package shantanu.docmate;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
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
import shantanu.docmate.Data.Report;

public class ViewReport extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private String reportKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);

        init();
        setReportData();
    }

    private void init() {
        reportKey = getIntent().getStringExtra("reportKey");
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Report...");
        progressDialog.show();
    }

    private void setReportData() {
        DatabaseReference databaseReports = FirebaseDatabase.getInstance().getReference()
                .child("doctor").child(auth.getCurrentUser().getUid()).child("reports").child(reportKey);
        databaseReports.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Report report = new Report(dataSnapshot.child("profilepic").getValue().toString(),
                        dataSnapshot.child("name").getValue().toString(),
                        dataSnapshot.child("image").getValue().toString(),
                        dataSnapshot.child("time").getValue().toString(),
                        dataSnapshot.child("message").getValue().toString());
                ((TextView) findViewById(R.id.tvName)).setText(report.getName());
                ((TextView) findViewById(R.id.tvMessage)).setText(report.getMessage());
                ((TextView) findViewById(R.id.tvTime)).setText(report.getTime());
                (new AQuery(getApplicationContext())).id((ImageView) findViewById(R.id.profilepic)).image(report.getProfilepic(), true, true, 60, R.drawable.default_image);
                (new AQuery(getApplicationContext())).id((ImageView) findViewById(R.id.reportImage))
                        .image(report.getImage(), true, true,
                                getWindowManager().getDefaultDisplay().getWidth(), R.drawable.default_image);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
