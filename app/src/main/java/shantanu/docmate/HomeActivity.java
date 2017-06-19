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
import android.widget.TextView;

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
    private RecyclerView patientList;

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

        final TextView tvResult = (TextView) findViewById(R.id.tvResult);
        databasePatients = patientDatabase.getReference().child("patient");
        databasePatients.keepSynced(true);

        databasePatients.child("8ieQjVcHTtR7R7ct2DZANMopt0z1")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("name")) {
                            Log.i(TAG, "onDataChange: Retrieved");
                            tvResult.setText(dataSnapshot.child("name").getValue().toString());
                        } else {
                            Log.e(TAG, "onDataChange: DATA NOT FOUND");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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
        patientList = (RecyclerView) findViewById(R.id.patientList);
        databasePatients = FirebaseDatabase.getInstance().getReference().child("patients");
        patientList.setHasFixedSize(true);
        patientList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

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

        auth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Log.e(TAG, "onAuthStateChanged: NO USER LOGGED IN");
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent, ActivityOptionsCompat
                            .makeSceneTransitionAnimation(HomeActivity.this, null).toBundle());
                    finish();
                }
            }
        };
        progressDialog = new ProgressDialog(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);

        FirebaseRecyclerAdapter<Patient, PatientViewHolder> adapter = new FirebaseRecyclerAdapter<Patient, PatientViewHolder>(
                Patient.class,
                R.layout.row_patient,
                PatientViewHolder.class,
                databasePatients
        ) {
            @Override
            protected void populateViewHolder(final PatientViewHolder viewHolder, final Patient model, final int position) {
                Log.i(TAG, "populateViewHolder: Started");
                String PatientKey = getRef(position).getKey().toString();


                Log.i(TAG, "populateViewHolder: Name : " + model.getName());
                Log.i(TAG, "populateViewHolder: Age : " + model.getAge());
                viewHolder.setName(model.getName());
                viewHolder.setAge(model.getAge());

                databasePatients.child(model.getUid()).child("profilepic")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    Log.i(TAG, "onDataChange: profilePic");
                                    viewHolder.setProfilePic(getApplicationContext(), dataSnapshot
                                            .getValue().toString());
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

                    }
                });


            }
        };

        adapter.notifyDataSetChanged();
        patientList.setAdapter(adapter);
//        if (flag) {
//            progressDialog.dismiss();
//            flag = false;
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
