package Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.newprojectmishanxx.Drawer_options_activity;
import com.example.newprojectmishanxx.Model.User;
import com.example.newprojectmishanxx.R;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;


//fragment that shows the admin all resident and volunteers in the app

public class All_app_users_fragment extends Fragment {

    View view;
    private Toolbar settingsToolbar;
    private Button addUserBtn;
    private LinearLayout allAppUsersLinearLayout;
    private RecyclerView allVolunteersRecyclerView;
    private TextView allVolunteersCount;
    private DatabaseReference usersRef;
    Activity mActivity;
    Context mContext;

    //init all needed items and  the firestore
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_all_app_users_fragment, container, false);
        allAppUsersLinearLayout = view.findViewById(R.id.allAppUsersLinearLayout);
        mActivity = getActivity();
        mContext = container.getContext();

        settingsToolbar = view.findViewById(R.id.settingsToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(settingsToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("All Users");
        addUserBtn = view.findViewById(R.id.addUserBtn);

        //OnClickListener for adding new user button
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new Add_user_for_manager());
            }
        });

        allVolunteersCount = view.findViewById(R.id.allVolunteersCount);

        allVolunteersRecyclerView = view.findViewById(R.id.allVolunteersRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        //allInvestorsRecyclerView.setHasFixedSize(true);
        allVolunteersRecyclerView.setLayoutManager(layoutManager);

        //using reference to collection to count number of users to display
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                long numberOfUsers = datasnapshot.getChildrenCount();
                int noOfInvestors = (int) numberOfUsers;
                allVolunteersCount.setText("Total Users: " +noOfInvestors);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mActivity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    //initializing RecyclerView and displaying user's info
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(usersRef, User.class)
                .build();

        FirebaseRecyclerAdapter<User, MyViewHolder> adapter = new FirebaseRecyclerAdapter<User, All_app_users_fragment.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull All_app_users_fragment.MyViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull User model) {

                //retrieving all data for user and displaying it in
                holder.userName.setText(model.getName());
                holder.userEmail.setText(model.getEmail());
                holder.userTypeBtn.setText(model.getType());
                Glide.with(mActivity.getApplicationContext()).load(model.getProfilepictureurl()).into(holder.profileImage);


                //onClickListener when admin clicks on a user
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //get user's id from firestore
                        final String userid = getRef(position).getKey();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userid);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                String usertype = snapshot.child("type").getValue(String.class);
//                                Intent intent = new Intent(mActivity.getApplicationContext(), Drawer_options_activity.class);
//                                intent.putExtra("userid", userid);
//                                intent.putExtra("usertype", usertype);
//                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });



                    }
                });

            }

            @NonNull
            @Override
            public All_app_users_fragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.all_app_user_display_to_admin,parent,false);
                return new All_app_users_fragment.MyViewHolder(view);
            }
        };

        allVolunteersRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }


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

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.drawer_options_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}