package Fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.newprojectmishanxx.R;

//Fragment that shows different user types for registration
public class Select_Registration_fragment extends Fragment {

    View view;
    private TextView back;
    private Button residentRegistration, volunteerRegistration;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_select__registration_fragment, container, false);

        back = view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new Login_fragment());
            }
        });

        // this click listener moves user resident's registration fragment
        residentRegistration = view.findViewById(R.id.residentRegistration);
        residentRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new Resident_registration_fragment());
            }
        });

        // this click listener moves user volunteer's registration fragment
        volunteerRegistration =view.findViewById(R.id.volunteerRegistration);
        volunteerRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new Volunteer_registration_fragment());
            }
        });

        return view;
    }


    // method replacing the main frameLayout in main activity with fragment.. in this case its teh login fragment
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }





}