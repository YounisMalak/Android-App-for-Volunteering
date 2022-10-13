package com.example.newprojectmishanxx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newprojectmishanxx.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;



// This fragment used to allow the admin to assign volunteer to the resident's requests.
public class Assign_Volunteer_To_Resident_activity extends AppCompatActivity {

    private Toolbar settingsToolbar;
    private RecyclerView allInvestorsRecyclerView;
    private TextView allInvestorsCount;
    String publisherid = "";
    String postid = "";
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_volunteer_to_resident);


        // init all the needed data for the items that used in this fragment, some of these items
        // exists in the XML layout.

        settingsToolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Connecting doctor to patient");

        publisherid = getIntent().getStringExtra("publisherid").toString();
        postid = getIntent().getStringExtra("postid").toString();

        loader = new ProgressDialog(this);

        allInvestorsCount = findViewById(R.id.allInvestorsCount);
        allInvestorsRecyclerView = findViewById(R.id.allInvestorsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        allInvestorsRecyclerView.setLayoutManager(layoutManager);

    }

    //actions to be done once is started or restarted.
    @Override
    public void onStart() {
        super.onStart();

        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("users");

        // this line of code retrieve all the volunteers in the database so the admin can see them, then he can assign one of them to
        // resident's request.
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(reference.orderByChild("type").equalTo("volunteer"), User.class)
                .build();

        FirebaseRecyclerAdapter<User, Assign_Volunteer_To_Resident_activity.MyViewHolder> adapter = new FirebaseRecyclerAdapter<User, Assign_Volunteer_To_Resident_activity.MyViewHolder>(options) {
            @Override
            // this method called by recycler view to display data at the specified position.
            protected void onBindViewHolder(@NonNull Assign_Volunteer_To_Resident_activity.MyViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final User model) {

                holder.userName.setText(model.getName());
                holder.userEmail.setText(model.getEmail());
                holder.userTypeBtn.setText(model.getType());
                Glide.with(getApplicationContext()).load(model.getProfilepictureurl()).into(holder.profileImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    // choosing volunteer to assign for resident's request
                    @Override
                    public void onClick(View view) {
                        final String userid = getRef(position).getKey();

                        AlertDialog.Builder myDialog = new AlertDialog.Builder(Assign_Volunteer_To_Resident_activity.this);
                        LayoutInflater inflater = LayoutInflater.from(Assign_Volunteer_To_Resident_activity.this);

                        View myView = inflater.inflate(R.layout.assigment_layout, null);

                        myDialog.setView(myView);

                        final AlertDialog dialog = myDialog.create();
                        dialog.setCancelable(false);

                        Button cancelBtn = myView.findViewById(R.id.btnCancel);
                        Button assignIdeaBtn = myView.findViewById(R.id.btnAssignIdea);
                        TextView item = myView.findViewById(R.id.item);

                        item.setText("Are You Sure You Want To Assign volunteer To resident " +model.getName()+"?");

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        assignIdeaBtn.setOnClickListener(new View.OnClickListener() {
                            // after assigning the volunteer the volunteer gets notification that he assigned to request, same for the apprpriate resident.
                            @Override
                            public void onClick(View view) {
                                loader.setMessage("Making the assignments");
                                loader.setCanceledOnTouchOutside(false);
                                loader.show();
                                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("requests").child(postid);
                                reference2.child("assignedto").setValue(userid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            final DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("users").child(userid);
                                            reference3.child("assignedresident").setValue(postid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(Assign_Volunteer_To_Resident_activity.this, "Successfully made the assignments", Toast.LENGTH_SHORT).show();


                                                        String text3 = "Your Booking was successful.";
                                                        addNotifications(postid, userid, text3);

                                                        String text = "Check out your new resident!";
                                                        addNotificationsForInvestorOrPartner(userid, postid, text);

                                                        loader.dismiss();
                                                    }
                                                }
                                            });
                                        }else {
                                            Toast.makeText(Assign_Volunteer_To_Resident_activity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            loader.dismiss();
                                        }
                                    }

                                });
                                dialog.dismiss();
                            }

                        });

                        dialog.show();

                    }
                });

            }


            // displaying layout in particular type, all the items created displayed like it created in all_app_user_display_to_admin,
            // meaning you can see all the items in the shape that exist in this layout.
            @NonNull
            @Override
            public Assign_Volunteer_To_Resident_activity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(Assign_Volunteer_To_Resident_activity.this).inflate(R.layout.all_app_user_display_to_admin,parent,false);
                return new Assign_Volunteer_To_Resident_activity.MyViewHolder(view);
            }
        };


        // setting the adapter to the recycle view
        allInvestorsRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }

// VH inner class to handel and init all the data that exist in the all users layout, using this class
    // to init these values (images, texts) to use them after that in the method up, like on bind view.
    // this helps us to set true values to these items.
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView userName, userEmail;
        CircleImageView profileImage;
        Button userTypeBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userEmail = itemView.findViewById(R.id.user_email);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            userTypeBtn = itemView.findViewById(R.id.userTypeBtn);
        }
    }

    // this method creates notification to the person who created this request in the firebase, sets all the data (childs) to the appropriate notification.
    private void addNotifications(String postid, String userid,  String text){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("requests").child(publisherid);
        HashMap<String, Object> hashMap = new HashMap<>();
        String mDate = DateFormat.getDateInstance().format(new Date());
        hashMap.put("text", text);
        hashMap.put("userid", userid);
        hashMap.put("postid", postid);
        hashMap.put("ispost",true);
        hashMap.put("date", mDate);

        reference.push().setValue(hashMap);
        firestore.document("requests").set(hashMap);
    }

    // this method creates notification for all request that have been made in the firebase, sets all the data (childs) to the appropriate one.
    private void addNotificationsForInvestorOrPartner(String userid, String postid, String text){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("requests").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        String mDate = DateFormat.getDateInstance().format(new Date());
        hashMap.put("text", text);
        hashMap.put("userid", userid);
        hashMap.put("postid", postid);
        hashMap.put("ispost",true);
        hashMap.put("date", mDate);

        reference.push().setValue(hashMap);
        firestore.document("requests").set(hashMap);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}