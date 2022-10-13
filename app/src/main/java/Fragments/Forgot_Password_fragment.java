package Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newprojectmishanxx.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

//Fragment for passport changing fragment

public class Forgot_Password_fragment extends Fragment {

    View view;
    private Toolbar toolbar;
    private EditText newEmail;
    private Button resetPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;
    private TextView forgotPasswordBack;
    Activity mActivity;
    Context mContext;

    //init all needed items and firestore authentication
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_forgot__password_fragment, container, false);
        mActivity = getActivity();
        mContext = container.getContext();


        toolbar = view.findViewById(R.id.forgotpasswordtoolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Reset Password");

        //Getting the email that needs it's password changed
        newEmail = view.findViewById(R.id.forgotPasswordEmail);
        resetPassword = view.findViewById(R.id.resetPasswordButton);
        forgotPasswordBack = view.findViewById(R.id.forgotPasswordBack);
        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(mContext);

        //onClickListener for the reset password button
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = newEmail.getText().toString().trim();
                //Error message if user clicks button without filling in the email
                if (TextUtils.isEmpty(email)) {
                    newEmail.setError("Your Email is required!");
                    return;
                } else {
                    dialog.setTitle("Requesting...");
                    dialog.setMessage("Please wait as a new password is being requested");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    //Using a method in firebase authentication, send reset email to user
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //if email sent successfully, return user to login fragment
                                Toast.makeText(mActivity, "Password reset email sent to your email. Please check it out.", Toast.LENGTH_LONG).show();
                                Toast.makeText(mActivity, "Check your Spam!", Toast.LENGTH_LONG).show();
                                replaceFragment(new Login_fragment());
//                                Intent intent = new Intent(mActivity, LoginActivity.class);
//                                startActivity(intent);
//                                mActivity.finish();
                                replaceFragment(new Login_fragment());
                            } else {
                                Toast.makeText(mActivity, "Error in sending reset email.." + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        //back button return user to login fragment
        forgotPasswordBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new Login_fragment());
            }
        });

        return view;
    }

    //handling attach and detach for fragment
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    //method that handles selected menu items
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

    //method that replaces current fragment
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}