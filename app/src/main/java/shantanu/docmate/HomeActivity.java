package shantanu.docmate;

import android.app.ProgressDialog;
import android.content.Intent;
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

import java.util.UUID;

import shantanu.docmate.Data.Patient;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";
    private static ProgressDialog progressDialog;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databasePatients;
    private DatabaseReference databasePendingAppointments;
    private DatabaseReference databaseDoctors;
    private DatabaseReference databaseAcceptedPatients;
    private DatabaseReference databaseRejectedPatients;

    private RecyclerView patientList;
    private boolean flag = true;
    private boolean loadingDone = false;
    private RecyclerView pendingAppointmentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Explode explode = new Explode();
        explode.setDuration(1000);
        getWindow().setEnterTransition(explode);

        setContentView(R.layout.activity_home);

        init();

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

        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onStart: CALLED");
        auth.addAuthStateListener(authStateListener);
        Log.i(TAG, "onStart: authStatelistener Added");
        if (!loadingDone) {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }


        FirebaseRecyclerAdapter<Patient, PatientViewHolder> patientsAdapter = new FirebaseRecyclerAdapter<Patient, PatientViewHolder>(
                Patient.class,
                R.layout.patient_item_layout,
                PatientViewHolder.class,
                databaseAcceptedPatients
        ) {
            @Override
            protected void populateViewHolder(final PatientViewHolder viewHolder, final Patient model, final int position) {
                Log.i(TAG, "populateViewHolder: Started");
                final String patientKey = getRef(position).getKey().toString();


                Log.i(TAG, "populateViewHolder: Name : " + model.getName());
                Log.i(TAG, "populateViewHolder: Age : " + model.getAge());
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
                Log.i(TAG, "populateViewHolder: Started");
                final String patientKey = getRef(position).getKey().toString();


                Log.i(TAG, "populateViewHolder: Name : " + model.getName());
                Log.i(TAG, "populateViewHolder: Age : " + model.getAge());
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
                    }
                });

            }
        };

        pendingAppointmentsAdapter.notifyDataSetChanged();
        pendingAppointmentsList.setAdapter(pendingAppointmentsAdapter);
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
