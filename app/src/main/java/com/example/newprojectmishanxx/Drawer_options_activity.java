package com.example.newprojectmishanxx;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;
import Fragments.All_app_users_fragment;
import Fragments.Dashboard_fragment;
import Fragments.MakeAppointment_fragment;
import Fragments.Notification_fragment;
import Fragments.Profile_fragment;
import Fragments.Residents_fragment;
import Fragments.Services_fragment;
import Fragments.Volunteers_fragment;

// this activity for the drawer window that slides.
public class Drawer_options_activity extends AppCompatActivity {

    String fragment_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_options);

        Bundle extras = getIntent().getExtras();

        // all the items in that drawer open different fragments, fragments name is sent to this acitivty using putExtra method that
        // exist in Users_activity. every item opens different fragment, handled using switch case.
        if(extras != null) {
            fragment_name = extras.getString("FRAGMENT_NAME");

            switch (fragment_name){
//                case "all_requests":
//                    replaceFragment();
//                    break;
                case "all_users":
                    replaceFragment(new All_app_users_fragment());
                    break;
                case "services":
                    replaceFragment(new Services_fragment());
//                    replaceFragment(new Services_registration());
                    break;
                case "residents":
                    replaceFragment(new Residents_fragment());
                    break;
                case "volunteers":
                    replaceFragment(new Volunteers_fragment());
                    break;
                case "profile":
                    replaceFragment(new Profile_fragment());
                    break;
                case "make_appointment":
                    replaceFragment(new MakeAppointment_fragment());
                    break;
                case "notifications":
                    replaceFragment(new Notification_fragment());
                    break;
//                case "appointments":
//                    replaceFragment(new );
//                case "dashboard":
//                    replaceFragment(new Dashboard_fragment());
//                    break;

            }
        }






    }

    // this method replaces the drawer activity fragment to the requested fragment.
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.drawer_options_layout, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

}