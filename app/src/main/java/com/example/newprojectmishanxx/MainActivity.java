package com.example.newprojectmishanxx;


import androidx.appcompat.app.AppCompatActivity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import Fragments.Home_fragment;
import Fragments.Login_fragment;

public class MainActivity extends AppCompatActivity {


// when the app runs home fragment opens after the splash screen.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        replaceFragment(new Home_fragment());

        int SPLASH_SCREEN = 2500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                replaceFragment(new Login_fragment());
            }
        }, SPLASH_SCREEN);





    }







// replaces the frame in main activity with the requested fragment.
    private void replaceFragment(Fragment fragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrame, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }
}