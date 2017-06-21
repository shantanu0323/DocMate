package shantanu.docmate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.UUID;

import shantanu.docmate.Data.Patient;
import shantanu.docmate.Data.Report;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";
    private static ProgressDialog progressDialog;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databasePatients;
    private DatabaseReference databasePendingAppointments;
    private DatabaseReference databaseReports;
    private DatabaseReference databaseDoctors;
    private DatabaseReference databaseAcceptedPatients;
    private DatabaseReference databaseRejectedPatients;

    private RecyclerView patientList;
    private boolean flag = true;
    private boolean loadingDone = false;
    private RecyclerView pendingAppointmentsList;
    private TextView appointmentsLabel, patientsLabel, reportsLabel;
    private RecyclerView reportsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Explode explode = new Explode();
        explode.setDuration(1000);
        getWindow().setEnterTransition(explode);

        setContentView(R.layout.activity_home);

        init();
        Log.i(TAG, "onCreate: TOKEN : " + FirebaseInstanceId.getInstance().getToken());

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:530266078999:android:481c4ecf3253701e") // Required for Analytics.
                .setApiKey("AIzaSyBRxOyIj5dJkKgAVPXRLYFkdZwh2Xxq51k") // Required for Auth.
                .setDatabaseUrl("https://seeadoc-d731b.firebaseio.com/") // Required for RTDB.
                .build();
        String patientApp = UUID.randomUUID().toString();
        FirebaseApp.initializeApp(this, options, patientApp);

        // Retrieve my other app.
        FirebaseApp patientAppliction = FirebaseApp.getInstance(patientApp);
// Get the database for the other app.
        FirebaseDatabase patientDatabase = FirebaseDatabase.getInstance(patientAppliction);

        databasePatients = patientDatabase.getReference().child("patient");
        databasePatients.keepSynced(true);

        databaseRejectedPatients = patientDatabase.getReference().child("rejected");
        databaseRejectedPatients.keepSynced(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    private void init() {
        appointmentsLabel = (TextView) findViewById(R.id.appointmentsLabel);
        patientsLabel = (TextView) findViewById(R.id.patientLabel);
        reportsLabel = (TextView) findViewById(R.id.reportsLabel);

        appointmentsLabel.setVisibility(View.GONE);
        patientsLabel.setVisibility(View.GONE);
        reportsLabel.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Log.i(TAG, "onAuthStateChanged: NO USER LOGGED IN");
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent, ActivityOptionsCompat
                            .makeSceneTransitionAnimation(HomeActivity.this, null).toBundle());
                    finish();
                } else {
                    Log.i(TAG, "onAuthStateChanged: User Logged in");
                }
            }
        };

//        auth.addAuthStateListener(authStateListener);

        patientList = (RecyclerView) findViewById(R.id.patientList);
        patientList.setHasFixedSize(true);
        patientList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        pendingAppointmentsList = (RecyclerView) findViewById(R.id.pendingAppointmentList);
        pendingAppointmentsList.setHasFixedSize(true);
        pendingAppointmentsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        reportsList = (RecyclerView) findViewById(R.id.reportsList);
        reportsList.setHasFixedSize(true);
        reportsList.setItemViewCacheSize(20);
        reportsList.setDrawingCacheEnabled(true);
        reportsList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        reportsList.setLayoutManager(new LinearLayoutManager(this));

        databaseReports = FirebaseDatabase.getInstance().getReference().child("doctor").child(auth.getCurrentUser().getUid()).child("reports");
        databaseReports.keepSynced(true);

        databasePendingAppointments = FirebaseDatabase.getInstance().getReference().child("appointments");
        databasePendingAppointments.keepSynced(true);

        databaseDoctors = FirebaseDatabase.getInstance().getReference().child("doctor");
        databaseDoctors.keepSynced(true);

//        Log.i(TAG, "init: UID : " + (auth.getCurrentUser() == null));
        databaseAcceptedPatients = FirebaseDatabase.getInstance().getReference().child("doctor")
                .child(auth.getCurrentUser().getUid()).child("patients");
        databaseAcceptedPatients.keepSynced(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_background));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        progressDialog = new ProgressDialog(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
//        startService(new Intent(getApplicationContext(), MyFirebaseInstanceIDService.class));
//        startService(new Intent(getApplicationContext(), MyFirebaseMessagingService.class));

//        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onStart: CALLED");
        auth.addAuthStateListener(authStateListener);
        Log.i(TAG, "onStart: authStatelistener Added");
        if (!loadingDone) {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        updateLabels();

        final FirebaseRecyclerAdapter<Report, ReportViewHolder> reportsAdapter = new FirebaseRecyclerAdapter<Report, ReportViewHolder>(
                Report.class,
                R.layout.reports_item_layout,
                ReportViewHolder.class,
                databaseReports
        ) {
            @Override
            protected void populateViewHolder(final ReportViewHolder viewHolder, final Report model, final int position) {
                final String reportKey = getRef(position).getKey().toString();

                Log.i(TAG, "populateViewHolder: name : " + model.getName());
                Log.i(TAG, "populateViewHolder: time : " + model.getTime());
                Log.i(TAG, "populateViewHolder: image : " + model.getImage());
                Log.i(TAG, "populateViewHolder: message : " + model.getMessage());
                Log.i(TAG, "populateViewHolder: profilepic : " + model.getProfilepic());

                viewHolder.setName(model.getName());
                viewHolder.setTime(model.getTime());
                viewHolder.setImage(getApplicationContext(), model.getImage(), getWindowManager().getDefaultDisplay().getWidth());
                viewHolder.setMessage(model.getMessage());
                viewHolder.setProfilepic(getApplicationContext(), model.getProfilepic());

                // Adding OnClickListener() to the entire Card
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(HomeActivity.this, "Report : " + reportKey, Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getApplicationContext(), PatientProfile.class);
//                        intent.putExtra("patientUid", reportKey);
//                        intent.putExtra("doctorUid", auth.getCurrentUser().getUid());
//                        startActivity(intent);
                    }
                });

            }
        };

        reportsAdapter.notifyDataSetChanged();
        reportsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = reportsAdapter.getItemCount();
                int lastVisiblePosition =
                        ((LinearLayoutManager) reportsList.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    reportsList.scrollToPosition(positionStart);
                }
            }
        });
        reportsList.setAdapter(reportsAdapter);

        FirebaseRecyclerAdapter<Patient, PatientViewHolder> patientsAdapter = new FirebaseRecyclerAdapter<Patient, PatientViewHolder>(
                Patient.class,
                R.layout.patient_item_layout,
                PatientViewHolder.class,
                databaseAcceptedPatients
        ) {
            @Override
            protected void populateViewHolder(final PatientViewHolder viewHolder, final Patient model, final int position) {
                final String patientKey = getRef(position).getKey().toString();

                viewHolder.setName(model.getName());
                viewHolder.setAge(model.getAge() + " yrs");

                databasePatients.child(model.getUid()).child("profilepic")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    Log.i(TAG, "onDataChange: profilePic");
                                    viewHolder.setProfilePic(getApplicationContext(), dataSnapshot
                                            .getValue().toString());
                                    if (flag) {
                                        progressDialog.dismiss();
//                                        checkIfPatientsEmpty();
                                        flag = false;
                                        loadingDone = true;
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                // Adding OnClickListener() to the entire Card
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(HomeActivity.this, "Patient : " + patientKey, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), PatientProfile.class);
                        intent.putExtra("patientUid", patientKey);
                        intent.putExtra("doctorUid", auth.getCurrentUser().getUid());
                        startActivity(intent);
                    }
                });

            }
        };


        patientsAdapter.notifyDataSetChanged();
        patientList.setAdapter(patientsAdapter);


        FirebaseRecyclerAdapter<Patient, PendingAppointmentsViewHolder> pendingAppointmentsAdapter = new FirebaseRecyclerAdapter<Patient, PendingAppointmentsViewHolder>(
                Patient.class,
                R.layout.pending_appointment_item_layout,
                PendingAppointmentsViewHolder.class,
                databasePendingAppointments.child(auth.getCurrentUser().getUid())
        ) {
            @Override
            protected void populateViewHolder(final PendingAppointmentsViewHolder viewHolder, final Patient model, final int position) {
                final String patientKey = getRef(position).getKey().toString();

                viewHolder.setName(model.getName());
                viewHolder.setAge(model.getAge() + " yrs");

                databasePatients.child(model.getUid()).child("profilepic")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    Log.i(TAG, "onDataChange: profilePic");
                                    viewHolder.setProfilePic(getApplicationContext(), dataSnapshot
                                            .getValue().toString());
                                    if (flag) {
                                        progressDialog.dismiss();
//                                        checkIfAppointmentsEmpty();
                                        flag = false;
                                        loadingDone = true;
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                // Adding OnClickListener() to the entire Card
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(HomeActivity.this, "Patient : " + patientKey, Toast.LENGTH_SHORT).show();
                    }
                });

                // Adding OnClickLitener() to the Accept button
                viewHolder.bAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "onClick: Accepted");
                        databasePatients.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(patientKey)) {
                                    databaseDoctors.child(auth.getCurrentUser().getUid()).child("patients")
                                            .child(patientKey).setValue(dataSnapshot.child(patientKey).getValue());
                                    databasePendingAppointments.child(auth.getCurrentUser().getUid())
                                            .child(patientKey).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                // Adding OnClickLitener() to the Accept button
                viewHolder.bReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "onClick: Rejected");
                        databasePendingAppointments.child(auth.getCurrentUser().getUid()).child(patientKey).removeValue();
                        databaseRejectedPatients.child(auth.getCurrentUser().getUid()).child(patientKey).setValue("rejected");
                        pendingAppointmentsList.notify();
                    }
                });

            }
        };

        pendingAppointmentsAdapter.notifyDataSetChanged();
        pendingAppointmentsList.setAdapter(pendingAppointmentsAdapter);

    }

    private void updateLabels() {

        databaseAcceptedPatients.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    patientsLabel.setVisibility(View.VISIBLE);
                    patientsLabel.setText("My Patients");
                    patientsLabel.setTextColor(Color.rgb(0, 0, 0));
                } else {
                    patientsLabel.setVisibility(View.VISIBLE);
                    patientsLabel.setText("No Patients registered...");
                    patientsLabel.setTextColor(Color.rgb(150, 150, 150));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databasePendingAppointments.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Log.i(TAG, "checkIfAppointmentsEmpty: NOT EMPTY");
                    appointmentsLabel.setVisibility(View.VISIBLE);
                    appointmentsLabel.setText("Pending Appointments");
                    appointmentsLabel.setTextColor(Color.rgb(0, 0, 0));
                } else {
                    Log.i(TAG, "checkIfAppointmentsEmpty: EMPTY");
                    appointmentsLabel.setVisibility(View.VISIBLE);
                    appointmentsLabel.setText("No Pending Appointments ...");
                    appointmentsLabel.setTextColor(Color.rgb(150, 150, 150));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReports.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Log.i(TAG, "checkIfReportstsEmpty: NOT EMPTY");
                    reportsLabel.setVisibility(View.VISIBLE);
                    reportsLabel.setText("Reports");
                    reportsLabel.setTextColor(Color.rgb(0, 0, 0));
                } else {
                    Log.i(TAG, "checkIfReportsEmpty: EMPTY");
                    reportsLabel.setVisibility(View.VISIBLE);
                    reportsLabel.setText("No Reports ...");
                    reportsLabel.setTextColor(Color.rgb(150, 150, 150));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        auth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:

                break;
            case R.id.action_logout:
                auth.signOut();
                break;
        }

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.action_profile) {
            Intent intent = new Intent(getApplicationContext(), DoctorProfile.class);
            startActivity(intent, ActivityOptionsCompat
                    .makeSceneTransitionAnimation(HomeActivity.this, null).toBundle());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
