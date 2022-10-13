package Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.newprojectmishanxx.R;
import com.example.newprojectmishanxx.Users_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//Fragment for admin login

public class Admin_login_fragment extends Fragment {

    View view;
    private Button adminLoginButton;
    private TextInputEditText adminRegistrationEmail,adminLoginPassword;
    private TextView adminBackBtn;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    Activity mActivity;
    Context mContext;


    //init all necessary items plus getting instances on auth and firebase
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin_login_fragment, container, false);
        mActivity = getActivity();
        mContext = container.getContext();

        adminLoginButton = view.findViewById(R.id.adminLoginButton);
        adminRegistrationEmail = view.findViewById(R.id.adminRegistrationEmail);
        adminLoginPassword = view.findViewById(R.id.adminLoginPassword);
        adminBackBtn = view.findViewById(R.id.adminBackBtn);
        loader = new ProgressDialog(mContext);
        mAuth = FirebaseAuth.getInstance();

        //pressing back takes user back to main login fragment
        adminBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new Login_fragment());
            }
        });

        //onClickListener for login button for admin
        adminLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //retrieving the text fields the admin has entered
                String email =  adminRegistrationEmail.getText().toString();
                String password = adminLoginPassword.getText().toString();

                /*checking if text fields are empty,
                if they are the admin is shows an appropriate error message*/
                if (TextUtils.isEmpty(email)){
                    adminRegistrationEmail.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    adminLoginPassword.setError("Password is required");
                    return;
                }

                else {
                    //signing in the user using firebase authentication
                    loader.setMessage("Sign in process in progress...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String type = snapshot.child("type").getValue(String.class);

                                        /* if the email and password entered belong to a user of type admin,
                                        start admins activity */

                                        if (type.equals("admin")){

                                            Intent intent = new Intent(mActivity, Users_Activity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            mActivity.finish();
                                            Toast.makeText(mActivity, getString(R.string.admin_login_successful), Toast.LENGTH_SHORT).show();

                                        }else {
                                            /* if the email and password entered do not belong to the admin, the user is signed
                                             out and returned to login fragment after being shown an error message
                                             */
                                            Toast.makeText(mActivity, getString(R.string.you_not_admin), Toast.LENGTH_LONG).show();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(mActivity, Login_fragment.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            mActivity.finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(mActivity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });



                            } //if sign in failed
                            else {
                                Toast.makeText(mActivity, getString(R.string.sign_in_failed_try_again)+task.getException(), Toast.LENGTH_LONG).show();
                            }
                            loader.dismiss();

                        }
                    });
                }
            }
        });


        return view;
    }

    //handling attach and detach of fragment

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

    //method that replaces current fragment

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}