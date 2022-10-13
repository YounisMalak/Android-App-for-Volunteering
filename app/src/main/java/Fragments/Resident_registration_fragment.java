package Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.newprojectmishanxx.Drawer_options_activity;
import com.example.newprojectmishanxx.MainActivity;
import com.example.newprojectmishanxx.Model.User;
import com.example.newprojectmishanxx.R;
import com.example.newprojectmishanxx.Users_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import DB.DatabaseHelper;
import de.hdodenhof.circleimageview.CircleImageView;

//fragment for handling resident registration
public class Resident_registration_fragment extends Fragment {

    View view;
    private TextView regPageQuestion;
    private Button regButton;
    private TextInputEditText registrationFullname, registrationPhoneNumber, registrationAddress, registrationEmail, loginPassword;
    private CircleImageView profileImage;
    private Uri resultUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private ProgressDialog loader;
    private DatabaseReference userDatabaseRef;
    private String currentUserOnlineID;
    Activity mActivity;
    Context mContext;
    DatabaseHelper databaseHelper;


    //init all needed items, firebase authentication, firestore and SQLite database
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_resident_registration_fragment, container, false);

        mActivity = getActivity();
        mContext = container.getContext();
        databaseHelper = new DatabaseHelper(mContext);

        registrationFullname = view.findViewById(R.id.registrationFullname);
        registrationPhoneNumber = view.findViewById(R.id.registrationPhoneNumber);
        registrationAddress = view.findViewById(R.id.registrationAddress);
        registrationEmail = view.findViewById(R.id.registrationEmail);
        loginPassword = view.findViewById(R.id.loginPassword);
        profileImage = view.findViewById(R.id.profileImage);
        regPageQuestion = view.findViewById(R.id.regPageQuestion);
        regButton = view.findViewById(R.id.regButton);

        loader = new ProgressDialog(getActivity());
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        //uploading profile image

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        //if resident is already signed up, move to sign in fragment
        regPageQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new Login_fragment());
            }
        });


        //if resident clicks sign up
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //retrieve all text fields
                final String email = registrationEmail.getText().toString().trim();
                final String password = loginPassword.getText().toString().trim();
                final String fullNames = registrationFullname.getText().toString().trim();
                final String address = registrationAddress.getText().toString().trim();
                final String phoneNumber = registrationPhoneNumber.getText().toString().trim();


                //check if any of the fields is empty, if it is shows appropriate error message
                if (TextUtils.isEmpty(email)){
                    registrationEmail.setError("Email Required!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    loginPassword.setError("Password Required!");
                    return;
                }if (TextUtils.isEmpty(fullNames)){
                    registrationFullname.setError("FullName Required!");
                    return;
                }
                if (TextUtils.isEmpty(address)){
                    registrationAddress.setError("Address Required!");
                    return;
                }

                if (TextUtils.isEmpty(phoneNumber)){
                    registrationPhoneNumber.setError("Phone Number Required!");
                    return;
                }

                if (resultUri == null){
                    Toast.makeText(getActivity(), "Your profile Image is required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    loader.setMessage("Registration in progress...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    //using firebase authentication to create user with the entered email and password
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                String error = task.getException().toString();
                                Toast.makeText(getActivity(), "Registration Failed: \n" + error, Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }else { //if the resident was created, get their id and using hashmap put all their details in the firestore

                                currentUserOnlineID = mAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = firestore.collection("users").document(currentUserOnlineID);
                                userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserOnlineID);

                                HashMap userInfo = new HashMap();
                                userInfo.put("id",currentUserOnlineID);
                                userInfo.put("name",fullNames);
                                userInfo.put("email", email);
                                userInfo.put("address", address);
                                userInfo.put("phonenumber", phoneNumber);
                                userInfo.put("type", "resident");


                                documentReference.set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isComplete()){
                                            //if adding user to firestore was successful, add resident to SQLite database
                                            Toast.makeText(mActivity, "Details set successfully on Firestore", Toast.LENGTH_SHORT).show();
//                                            Snackbar.make(residentRegistrationScrollView, "Details set successfully on Firestore", Snackbar.LENGTH_SHORT).show();
                                            databaseHelper.addResidentData(fullNames, address);
                                        } else{
                                            String error = task.getException().toString();
                                            Toast.makeText(mActivity, "Details upload Failed on Firestore " + error, Toast.LENGTH_SHORT).show();
//                                            Snackbar.make(residentRegistrationScrollView,  "Details upload Failed on Firestore" + error, Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(mActivity, "Details set successfully", Toast.LENGTH_SHORT);
                                        }else {
                                            String error = task.getException().toString();
                                            Toast.makeText(mActivity, "Details upload Failed: "+ error, Toast.LENGTH_SHORT);
                                        }
                                        mActivity.finish();
                                        loader.dismiss();
                                    }
                                });

                                //adding profile picture to firestore
                                if (resultUri !=null){
                                    final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profile pictures").child(currentUserOnlineID);
                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplication().getContentResolver(), resultUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream byteArrayOutputStStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20,byteArrayOutputStStream);
                                    byte[] data = byteArrayOutputStStream.toByteArray();
                                    UploadTask uploadTask = filepath.putBytes(data);

                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            mActivity.finish();
                                            return;
                                        }
                                    });

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        //if profile picture upload was successful
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            if (taskSnapshot.getMetadata() != null) {
                                                if (taskSnapshot.getMetadata().getReference() != null) {
                                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            String imageUrl = uri.toString();
                                                            Map newImageMap = new HashMap();
                                                            newImageMap.put("profilepictureurl", imageUrl);

                                                            userDatabaseRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                                @Override
                                                                public void onComplete(@NonNull Task task) {
                                                                    if (task.isSuccessful()){
                                                                        Toast.makeText(mActivity, "Registration successful", Toast.LENGTH_SHORT).show();
                                                                    }else {
                                                                        String error = task.getException().toString();
                                                                        Toast.makeText(mActivity, "Process failed "+ error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                            mActivity.finish();
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });

                                    //after registration is successfully over, move resident to user activity
                                    Intent intent = new Intent(getActivity(), Users_Activity.class);
                                    startActivity(intent);
                                    mActivity.finish();
                                    loader.dismiss();

                                }

                            }}

                    });
                }
            }
        });
        return view;
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

    //making sure the profile image was uploaded successfully
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1 && resultCode == Activity.RESULT_OK && data != null){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            profileImage.setImageURI(resultUri);
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