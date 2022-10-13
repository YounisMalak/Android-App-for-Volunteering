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
import com.google.firebase.auth.FirebaseUser;

//fragment for login
public class Login_fragment extends Fragment {

    View view;
    private TextView loginPageQuestion, loginAdmin, forgot_passwordd;
    private TextInputEditText loginEmail, loginPassword;
    private Button loginBtnNew;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    private FirebaseAuth.AuthStateListener authStateListener;
    Activity mActivity;
    Context mContext;

    //init all needed items and firestore authentication
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);

        mActivity = getActivity();
        mContext = container.getContext();
        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //getting the current user
                FirebaseUser user = mAuth.getCurrentUser();
                if (user!= null){
                    Intent intent = new Intent(mActivity, Users_Activity.class);
                    startActivity(intent);
                    mActivity.finish();
                }
            }
        };

        loginPageQuestion = view.findViewById(R.id.loginPageQuestion);
        loginAdmin = view.findViewById(R.id.loginAdmin);


        //if user is not already signed up, clicking this text takes them to registration fragment
        loginPageQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new Select_Registration_fragment());
            }
        });

        //taking admin to admin login fragment
        loginAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new Admin_login_fragment());
            }
        });

        forgot_passwordd = view.findViewById(R.id.forgot_passwordd);
        loginEmail = view.findViewById(R.id.loginEmail);
        loginPassword = view.findViewById(R.id.loginPassword);
        loginBtnNew = view.findViewById(R.id.loginBtnNew);
        loader = new ProgressDialog(mContext);

        //clicking forgot password takes user to forgot password fragment
        forgot_passwordd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new Forgot_Password_fragment());
            }
        });

        //once user clicked sign in get all text fields
        loginBtnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email =  loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                //if text fields are empty display appropriate error message
                if (TextUtils.isEmpty(email)){
                    loginEmail.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    loginPassword.setError("Password is required");
                    return;
                }

                else {
                    loader.setMessage("Sign in process in progress...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    //signing user in using firebase authentication
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                //if signing in successful, replace current activity with users activity
                                Intent intent = new Intent(mActivity, Users_Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                mActivity.finish();
                                Toast.makeText(mActivity, "Welcome", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(mActivity, getString(R.string.signin_process_failed_try_again)+task.getException(), Toast.LENGTH_LONG).show();
                            }
                            loader.dismiss();

                        }
                    });
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }

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

    //method for replacing current fragment
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}