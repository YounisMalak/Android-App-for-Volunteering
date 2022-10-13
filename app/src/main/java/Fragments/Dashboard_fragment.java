package Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newprojectmishanxx.Adapter.AppointmentAdapter;
import com.example.newprojectmishanxx.Model.Appointment;
import com.example.newprojectmishanxx.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

//Dashboard that shows cardview that contains resident and volunteer assigned to request
public class Dashboard_fragment extends Fragment {
    View view;
    private RecyclerView dashboardRecycleView;
    private List<Appointment> appointmentList;
    AppointmentAdapter appointmentAdapter;
    Activity mActivity;
    Context mContext;

    //init all needed items and  the firestore
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_dashboard_fragment, container, false);
        mActivity = getActivity();
        mContext = container.getContext();
        dashboardRecycleView = view.findViewById(R.id.dashboardRecycleView);
        //Array list that contains all appointments
        appointmentList = new LinkedList<>();
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("appointments");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentList.clear();
                //iterating appointments and adding to arraylist
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Appointment appointment = postSnapshot.getValue(Appointment.class);
                    appointmentList.add(appointment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //setting adapter for RecyclerView to show appointments
        dashboardRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        appointmentAdapter = new AppointmentAdapter(appointmentList);
        dashboardRecycleView.setAdapter(appointmentAdapter);



        return view;
    }
}