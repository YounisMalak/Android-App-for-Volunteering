package Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newprojectmishanxx.Model.User;
import com.example.newprojectmishanxx.R;
import com.example.newprojectmishanxx.Users_Activity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import DB.DatabasehelperTwo;
import de.hdodenhof.circleimageview.CircleImageView;

//fragment that shows admin all volunteers
public class Volunteers_fragment extends Fragment {

    View view;
    private Toolbar settingsToolbar;
    private RecyclerView allVolunteersRecyclerView;
    private TextView allVolunteersCount;
    private LinearLayout volunteersLinearLayout;
    private DatabaseReference usersRef;
    Activity mActivity;
    Context mContext;


    //init all needed items, Recyclerview, firestore and toolbar and resident's count
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_volunteers_fragment, container, false);
        mActivity = getActivity();
        mContext = container.getContext();
        volunteersLinearLayout = view.findViewById(R.id.volunteersLinearLayout);

        settingsToolbar = view.findViewById(R.id.settingsToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(settingsToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("All Volunteers");

        allVolunteersCount = view.findViewById(R.id.allVolunteersCount);

        allVolunteersRecyclerView = view.findViewById(R.id.allVolunteersRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        //allInvestorsRecyclerView.setHasFixedSize(true);
        allVolunteersRecyclerView.setLayoutManager(layoutManager);


        usersRef = FirebaseDatabase.getInstance().getReference("users");
        //querying firestore to receive all users whose type is volunteer
        Query query = usersRef.orderByChild("type").equalTo("volunteer");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                //updating count based on how many volunteers were received
                long numberOfUsers = datasnapshot.getChildrenCount();
                int noOfInvestors = (int) numberOfUsers;
                allVolunteersCount.setText("Total Volunteers: " +noOfInvestors);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mActivity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }



    @Override
    public void onStart() {
        super.onStart();

        //initializing recyclerview with all users retrieved
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(usersRef.orderByChild("type").equalTo("volunteer"), User.class)
                .build();

        FirebaseRecyclerAdapter<User, MyViewHolder> adapter = new FirebaseRecyclerAdapter<User, Volunteers_fragment.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Volunteers_fragment.MyViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull User model) {

                //setting the data to be displayed by getting user's details
                holder.userName.setText(model.getName());
                holder.userEmail.setText(model.getEmail());
                holder.userTypeBtn.setText(model.getType());
                Glide.with(mContext).load(model.getProfilepictureurl()).into(holder.profileImage);

                //onClickListener for deleting a volunteer
                holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setMessage(getString(R.string.do_you_want_to_delete_this_volunteer))
                                .setCancelable(false)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        //getting volunteer id from firestore
                                        final String userid = getRef(position).getKey();
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userid);
                                        ref.removeValue();
                                        //retrieves volunteer from firestore and deletes them
                                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                        DocumentReference documentReference = firestore.collection("users").document(userid);
                                        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Snackbar.make(volunteersLinearLayout, getString(R.string.deleted_successfully), Snackbar.LENGTH_SHORT).show();
//                                                    DatabasehelperTwo databasehelperTwo = new DatabasehelperTwo(mContext);
//                                                    String name = model.getName();
//                                                    String id = model.getId();
//                                                    databasehelperTwo.deleteVolunteer(Integer.valueOf(id), name);
                                                }
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String userid = getRef(position).getKey();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userid);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                                Intent intent = new Intent(mActivity, Users_Activity.class);
//                                intent.putExtra("userid", userid);
//                                intent.putExtra("usertype", "volunteer");
//                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

            }

            //show all data in recyclerView and display in all_users_display_layout
            @NonNull
            @Override
            public Volunteers_fragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.all_users_display_layout,parent,false);
                return new Volunteers_fragment.MyViewHolder(view);
            }
        };

        //setting adapter
        allVolunteersRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    /*view holder class that initializes all items that exist in the RecyclerView in all_users_display_layout
      to prepare them for view holder methods (bindViewHolder etc)
    */
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView userName, userEmail;
        CircleImageView profileImage;
        Button userTypeBtn;
        ImageView deleteIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userEmail = itemView.findViewById(R.id.user_email);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            userTypeBtn = itemView.findViewById(R.id.userTypeBtn);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mActivity.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}