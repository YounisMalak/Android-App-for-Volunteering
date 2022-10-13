package Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.newprojectmishanxx.Drawer_options_activity;
import com.example.newprojectmishanxx.Model.IDOFTHEADMIN;
import com.example.newprojectmishanxx.R;
import com.example.newprojectmishanxx.Users_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import DB.DatabaseHelper;

//fragment for resident to create appointment
public class MakeAppointment_fragment extends Fragment {

    private static final String TAG = "Make_Appointment";
    View view;

    private Toolbar settingsToolbar;

    private EditText descriptionText;
    private Spinner availabilitySpinner;
    private Button makeAppointmentButton;
    private Spinner servicesSpinner;

    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private DatabaseReference postedByRef;
    DatabaseHelper databaseHelper;
    Activity mActivity;
    Context mContext;

    //init all needed items, firestore authentication, and SQLite database
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_make_appointment_fragment, container, false);
        mActivity = getActivity();
        mContext = container.getContext();
        descriptionText = view.findViewById(R.id.descriptionText);
        availabilitySpinner = view.findViewById(R.id.availabilitySpinner);
        servicesSpinner = view.findViewById(R.id.servicesSpinner);
        makeAppointmentButton = view.findViewById(R.id.makeAppointmentButton);
        loader = new ProgressDialog(mContext);
        mAuth = FirebaseAuth.getInstance();
        databaseHelper = new DatabaseHelper(mContext);

        settingsToolbar = view.findViewById(R.id.settingsToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(settingsToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Make An Appointment");

        //Getting all services' names from SQLite database to display in spinner
        Cursor data = databaseHelper.getAllServicesData();
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {
            //get the value from the database in column 1
            //then add it to the ArrayList
            listData.add(data.getString(1));
        }
        ArrayAdapter serviceAdapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, listData);
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        servicesSpinner.setAdapter(serviceAdapter);
        servicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(mActivity, listData.get(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //once resident presses make appointment, a method is called to validate data
        makeAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performIdeaValidations();
            }
        });

        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);

        descriptionText.setText(prefs.getString("autoSave", ""));

        descriptionText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){

            }

            @Override
            public void afterTextChanged(Editable s){
                prefs.edit().putString("autoSave", s.toString()).commit();
            }

        });

        return view;
    }

    //loader showing that registration is in progress
    private void startLoader(){
        loader.setMessage("Saving. Please wait...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
    }

    //method that validates the data entered by the resident
    private void performIdeaValidations(){
        //retrieving all text fields and selected spinner items
        String appointmentDetails = descriptionText.getText().toString();
        String appointmentTime = availabilitySpinner.getSelectedItem().toString();
        String serviceName = servicesSpinner.getSelectedItem().toString();
        Log.d(TAG, servicesSpinner.getSelectedItem().toString());

        //if any fields are empty show appropriate error message
        if (TextUtils.isEmpty(appointmentDetails)){
            descriptionText.setError("Description required!");
        }

        if (appointmentTime.equals("Select Time You Are Available")){
            Toast.makeText(mActivity, "Select a valid time!", Toast.LENGTH_SHORT).show();
        }

        else {
            //adding appointment to firestore
            startLoader();
            String mDate = DateFormat.getDateInstance().format(new Date());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("appointments");
            final String appointmentid = reference.push().getKey();

            //hashmap containing all appointment details
            HashMap<String, Object> hashMap =   new HashMap<>();
            hashMap.put("id", appointmentid);
            hashMap.put("details", appointmentDetails);
            hashMap.put("publisher", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            hashMap.put("time", appointmentTime);
            hashMap.put("service", serviceName);
            hashMap.put("date", mDate);

            assert appointmentid != null;
            reference.child(appointmentid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //if appointment was added to firestore
                    if (task.isSuccessful()){
                        loader.dismiss();
                        Toast.makeText(mActivity, "Booking Made successfully", Toast.LENGTH_SHORT).show();
                        Intent appointmentIntent = new Intent(mActivity, Drawer_options_activity.class);
                        appointmentIntent.putExtra("FRAGMENT_NAME", "appointments");
                        startActivity(appointmentIntent);
//                        startActivity(new Intent(mActivity, Drawer_options_activity.class));
                        mActivity.finish();

                        String text = "Made an appointment booking";

                        //adding notification to resident using appointment id and text
                        addNotifications(appointmentid, text);
                    }else {
                        String e = Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(mActivity, "Could not be posted" + e, Toast.LENGTH_SHORT).show();
                        loader.dismiss();
                    }
                }
            });
        }

    }

    /*a method that receives an appointment id and creates
     a notification, adds the notification to firestore
     */
    private void addNotifications(String postid, String text){
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("notifications").child(IDOFTHEADMIN.getIdOfTheAdmin());
        HashMap<String, Object>hashMap = new HashMap<>();
        String mDate = DateFormat.getDateInstance().format(new Date());
        hashMap.put("userid", mAuth.getCurrentUser().getUid());
        hashMap.put("text", text);
        hashMap.put("postid", postid);
        hashMap.put("ispost",true);
        hashMap.put("date", mDate);

        reference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //if adding notification to firestore was successful, move to Drawer_options_activity Activity and open notifications fragment
                if (task.isSuccessful()){
                    loader.dismiss();
                    Toast.makeText(mActivity, "new notification", Toast.LENGTH_SHORT).show();
                    Intent notiIntent = new Intent(mActivity, Drawer_options_activity.class);
                    notiIntent.putExtra("FRAGMENT_NAME", "notifications");
                    startActivity(notiIntent);
                    mActivity.finish();
                }
            }
        });
    }

    //method that handles menu item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public void onBackPressed() {
//
//        new AlertDialog.Builder(mContext)
//                .setTitle("Making An Appointment")
//                .setMessage("Are you sure you want to leave?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        MakeAppointment_fragment.super.onBackPressed();
//                    }
//                })
//                .setNegativeButton("No", null)
//                .show();
//    }
//


}