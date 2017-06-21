package shantanu.docmate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ReportViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "PatientViewHolder";
    View view;
    ImageView bProfilePic;

    FirebaseAuth mAuth;
    DatabaseReference mDatabaseLikes;

    public ReportViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        bProfilePic = (ImageView) view.findViewById(R.id.profilePic);

        mAuth = FirebaseAuth.getInstance();
    }

    public void setName(String name) {
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        tvName.setText(name + " says :");
        Log.i(TAG, "setName: " + name);
    }

    public void setMessage(String message) {
        TextView tvAge = (TextView) view.findViewById(R.id.tvMessage);
        tvAge.setText("\"" + message + "\"");
        Log.i(TAG, "setAge: " + message);
    }

    public void setTime(String time) {
        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
        tvTime.setText(time);
        Log.i(TAG, "setName: " + time);
    }

    public void setProfilepic(final Context context, final String profilePicUrl) {
        final ImageView profilePic = (ImageView) view.findViewById(R.id.profilePic);
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

    public void setImage(final Context context, final String imageUrl, int width) {
        final ImageView image = (ImageView) view.findViewById(R.id.image);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
//        thread.start();
        AQuery androidAQuery=new AQuery(context);
        androidAQuery.id(image).image(imageUrl, true, true, width, R.drawable.default_image);

    }

}