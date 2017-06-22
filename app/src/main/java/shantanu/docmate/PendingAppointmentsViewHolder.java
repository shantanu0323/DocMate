package shantanu.docmate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class PendingAppointmentsViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "PatientViewHolder";
    View view;
    ImageView bProfilePic;
    Button bAccept;
    Button bReject;

    FirebaseAuth mAuth;
    DatabaseReference mDatabaseLikes;

    public PendingAppointmentsViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        bProfilePic = (ImageView) view.findViewById(R.id.profilepic);
        bAccept = (Button) view.findViewById(R.id.bAccept);
        bReject = (Button) view.findViewById(R.id.bReject);

        mAuth = FirebaseAuth.getInstance();
    }

    public void setName(String name) {
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        tvName.setText(name);
        Log.i(TAG, "setName: " + name);
    }

    public void setAge(String age) {
        TextView tvAge = (TextView) view.findViewById(R.id.tvAge);
        tvAge.setText(age);
        Log.i(TAG, "setAge: " + age);
    }

    public void setProfilePic(final Context context, final String profilePicUrl) {
        final ImageView profilePic = (ImageView) view.findViewById(R.id.profilepic);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        Picasso.with(context).load(profilePicUrl).networkPolicy(NetworkPolicy.OFFLINE).into(profilePic,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        Picasso.with(context).load(profilePicUrl).into(profilePic);
                        progressBar.setVisibility(View.GONE);
                    }
                });

    }

}