package shantanu.docmate;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import shantanu.docmate.Data.Doctor;
import shantanu.docmate.Data.Patient;

/**
 * Created by SHAAN on 16-06-17.
 */

public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";

    private FirebaseAuth auth;
    private DatabaseReference databaseDoctor;
    private DatabaseReference databasePatient;

    public FirebaseHelper() {
        auth = FirebaseAuth.getInstance();
        databaseDoctor = FirebaseDatabase.getInstance().getReference().child("doctor");
        databasePatient = FirebaseDatabase.getInstance().getReference().child("patient");

    }

    public void registerDoctor(Doctor doctor) {
        startRegister(doctor);
    }

    public void registerPatient(Patient patient) {

    }

    private void startRegister(final Doctor doctor) {
        Log.e(TAG, "startRegister: REGISTERING USER...");
        auth.createUserWithEmailAndPassword(doctor.getEmail(),doctor.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "onComplete: REGISTERING USER SUCCESSFULL");
                            String userId = auth.getCurrentUser().getUid();

//                                uploadImage();
                            DatabaseReference currentUser = databaseDoctor.child(userId);

                            currentUser.child("name").setValue(doctor.getName());
                            currentUser.child("email").setValue(doctor.getEmail());
                            currentUser.child("password").setValue(doctor.getPassword());
                            currentUser.child("address").setValue(doctor.getAddress());
                            currentUser.child("profilepic").setValue(doctor.getProfilePic());
                            currentUser.child("degree").setValue(doctor.getDegree());
                            currentUser.child("gender").setValue(doctor.getGender());
                            currentUser.child("phone").setValue(doctor.getPhone());
                            currentUser.child("specialization").setValue(doctor.getSpecializtion());

//                                Log.e(TAG, "onComplete: Redirecting to LoginActivity");
//                                Intent intent = new Intent(getApplicationContext(), LoginActivity
//                                        .class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: REGISTRATION FAILED due to : " + e.getMessage());
            }
        });
    }

}
