package com.example.newprojectmishanxx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.newprojectmishanxx.Adapter.AppointmentAdapter;
import com.example.newprojectmishanxx.Adapter.NotificationAdapter;
import com.example.newprojectmishanxx.Model.Appointment;
import com.example.newprojectmishanxx.Model.Notification;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Fragments.All_app_users_fragment;
import Fragments.Dashboard_fragment;
import Fragments.Login_fragment;
import Fragments.Profile_fragment;
import Fragments.Residents_fragment;
import Fragments.Services_fragment;
import Fragments.Volunteers_fragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class Users_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    Activity mActivity;
    private TextView mNavHeaderName,mNavHeaderEmail,mNavHeaderType;
    private CircleImageView nav_header_user_image;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference reference, userRef;
    private RecyclerView recyclerViewId;
    private FloatingActionButton fab;
    private TextView postIdeaTextView;
    private ProgressBar progress_circular;
    LinearLayout usersLayoutLinear;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;

// init all the needed items that used in this activity, some of these items exists in the activity's XML layout.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);;
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        usersLayoutLinear = findViewById(R.id.usersLayoutLinear);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mishan System");
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        fab = findViewById(R.id.fab);
        postIdeaTextView = findViewById(R.id.postIdeaTextView);

        // handling the action bar.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(Users_Activity.this, drawerLayout, toolbar,
                R.string.opennavigationdrawer, R.string.closenavigationdrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


        reference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                navigationView =findViewById(R.id.nav_view);
                Menu nav_Menu = navigationView.getMenu();

                // displaying drawer items the admin can see, using menu layout.
                String type = snapshot.child("type").getValue().toString();
                if (type.equals("admin")){
                    nav_Menu.findItem(R.id.nav_allUsers).setVisible(true);
                    nav_Menu.findItem(R.id.nav_residents).setVisible(true);
                    nav_Menu.findItem(R.id.nav_volunteers).setVisible(true);
                    nav_Menu.findItem(R.id.nav_services).setVisible(true);
                    nav_Menu.findItem(R.id.nav_dashboard).setVisible(true);
                    nav_Menu.findItem(R.id.nav_requests).setTitle("All Requests");

                    // this method reads all the appointments that residents have booked.
                    readAllBookingAppointments();

                    // displaying drawer items the resident can see, using menu layout.
                }else if (type.equals("resident")){
                    fab.setVisibility(View.VISIBLE);
                    postIdeaTextView.setVisibility(View.VISIBLE);

                    // this method reads all the appointments that were booked by this resident.
                    readMyBookings();
                }
                // displaying drawer items the volunteer can see, using menu layout.
                else {
                    nav_Menu.findItem(R.id.nav_requests).setTitle("Resident Requests");

                    // this method reads all the appointments that assigned to volunteer by the admin.
                    readAssignedAppointments();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Users_Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // here all the appropriate data that belongs to the connected user is handled.
        // in the header of the drawer where the image, name, type of user exists the appropriate data for the connected user
        // is shown.
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        mNavHeaderName =navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_name);
        mNavHeaderEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_email);
        nav_header_user_image = navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_image);
        mNavHeaderType = navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_type);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String name = dataSnapshot.child("name").getValue().toString();
                    mNavHeaderName.setText(name);

                    String email = dataSnapshot.child("email").getValue().toString();
                    mNavHeaderEmail.setText(email);

                    String type = dataSnapshot.child("type").getValue().toString();
                    mNavHeaderType.setText("Type: "+type);

                    String image = dataSnapshot.child("profilepictureurl").getValue(String.class);
                    Glide.with(getApplication()).load(image).into(nav_header_user_image);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Users_Activity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // on click listener for the floating button that used to book appointment by resident.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent makeAppointmentIntent = new Intent(Users_Activity.this, Drawer_options_activity.class);
                makeAppointmentIntent.putExtra("FRAGMENT_NAME", "make_appointment");
                startActivity(makeAppointmentIntent);
            }
        });

        // the loader is handled here.
        progress_circular = findViewById(R.id.progress_circular);
        recyclerViewId = findViewById(R.id.recyclerViewId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerViewId.setLayoutManager(layoutManager);

        // setting the adaptyer for the appointment's list.
        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(Users_Activity.this, appointmentList);
        recyclerViewId.setAdapter(appointmentAdapter);
    }


    // this method handled the items in the drawer that sellected by user, using putExtra to send
    // fragment name to the Drawer_activity that used to change the fragment.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.nav_dashboard:
                Intent myFeedIntent = new Intent(Users_Activity.this, Drawer_options_activity.class);
                myFeedIntent.putExtra("FRAGMENT_NAME", "dashboard");
                startActivity(myFeedIntent);
                break;

//                case R.id.nav_feed:
//                Intent myFeedIntent = new Intent(MainActivity.this, MyQuestionsActivity.class);
//                startActivity(myFeedIntent);
//                break;*/

            case R.id.nav_profile:
                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                editor.apply();
                Intent profileIntent = new Intent(Users_Activity.this, Drawer_options_activity.class);
                String profile = "profile";
                profileIntent.putExtra("FRAGMENT_NAME", profile);
                startActivity(profileIntent);
                break;


            case R.id.nav_notifications:
                Intent notificationIntent = new Intent(Users_Activity.this, Drawer_options_activity.class);
                notificationIntent.putExtra("FRAGMENT_NAME", "notifications");
                startActivity(notificationIntent);
                break;

            case R.id.nav_services:
                Intent servicesIntent = new Intent(Users_Activity.this, Drawer_options_activity.class);
                servicesIntent.putExtra("FRAGMENT_NAME", "services");
                startActivity(servicesIntent);
                break;


            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent logoutIntent = new Intent(Users_Activity.this, MainActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
                finish();
                break;

            case R.id.nav_allUsers:
                Intent usersIntent = new Intent(Users_Activity.this, Drawer_options_activity.class);
                usersIntent.putExtra("FRAGMENT_NAME", "all_users");
                startActivity(usersIntent);
                break;

            case R.id.nav_residents:
                Intent residentsIntent = new Intent(Users_Activity.this, Drawer_options_activity.class);
                residentsIntent.putExtra("FRAGMENT_NAME", "residents");
                startActivity(residentsIntent);
                break;

            case R.id.nav_volunteers:
                Intent volunteersIntent = new Intent(Users_Activity.this, Drawer_options_activity.class);
                volunteersIntent.putExtra("FRAGMENT_NAME", "volunteers");
                startActivity(volunteersIntent);
                break;

//            case R.id.nav_about:
////                Intent intent2 = new Intent(Users_Activity.this, AboutAppActivity.class);
////                startActivity(intent2);
//                break;

            /*case R.id.nav_rate:
                launchMarket();
                break;
            case R.id.nav_share:
                shareIt();
                break;*/
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // this method reads all the appointments that were booked by this resident.
    private void readMyBookings(){
        String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("requests");
        Query query = reference.orderByChild("publisher").equalTo(userid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    appointmentList.add(appointment);

                }
                appointmentAdapter.notifyDataSetChanged();
                progress_circular.setVisibility(View.GONE);

                if (appointmentList.isEmpty()){
                    Toast.makeText(Users_Activity.this, "No requests to show.", Toast.LENGTH_SHORT).show();
                    progress_circular.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // this method reads all the appointments that residents have booked.
    private void readAllBookingAppointments() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("appointments");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    appointmentList.add(appointment);
                }
                Intent makeAppointmentIntent = new Intent(Users_Activity.this, Drawer_options_activity.class);
                makeAppointmentIntent.putExtra("FRAGMENT_NAME", "dashboard");
                startActivity(makeAppointmentIntent);
                appointmentAdapter.notifyDataSetChanged();
                progress_circular.setVisibility(View.GONE);


                if (appointmentList.isEmpty()){
                    Toast.makeText(Users_Activity.this, "No requests to show", Toast.LENGTH_SHORT).show();
                    progress_circular.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // this method reads all the appointments that assigned to volunteer by the admin.
    private void readAssignedAppointments(){
        String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("requests");
        Query query = reference.orderByChild("assignedto").equalTo(userid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(mActivity == null){
                    return;
                }
                appointmentList.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    appointmentList.add(appointment);

                }
                appointmentAdapter.notifyDataSetChanged();
                progress_circular.setVisibility(View.GONE);

                if (appointmentList.isEmpty()){
                    Toast.makeText(Users_Activity.this, "No requests to show", Toast.LENGTH_SHORT).show();
                    progress_circular.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


//    @Override
//    public void onBackPressed() {
//
//        new AlertDialog.Builder(this)
//                .setTitle("Mishan app")
//                .setMessage("Are you sure you want to exit?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        Users_Activity.super.onBackPressed();
//                    }
//                })
//                .setNegativeButton("No", null)
//                .show();
//    }

//    public void replaceFragment(Fragment fragment) {
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.usersLayout, fragment);
////        transaction.addToBackStack(null);
//        transaction.commit();
//    }


}