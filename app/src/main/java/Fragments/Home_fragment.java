package Fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.newprojectmishanxx.MainActivity;
import com.example.newprojectmishanxx.R;

//fragment for splash screen
public class Home_fragment extends android.app.Fragment {

    View view;

    //inflating splash screen layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_fragment, container, false);

        return view;
    }

//    // method replacing the main frameLayout in main activity with fragment.. in this case its teh login fragment
//    public void replaceFragment(Fragment fragment) {
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.mainFrame, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }
}