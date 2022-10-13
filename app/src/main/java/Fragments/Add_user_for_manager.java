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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.newprojectmishanxx.Drawer_options_activity;
import com.example.newprojectmishanxx.R;
import com.example.newprojectmishanxx.Users_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import DB.DatabasehelperTwo;
import de.hdodenhof.circleimageview.CircleImageView;

// fragment where resident can wither add resident or volunteer
public class Add_user_for_manager extends Fragment {
    View view;

    private TextInputEditText registrationEmail, loginPassword, registrationFullname, registrationAddress, registrationPhoneNumber, registrationUserType;
    private Button adminRegUserBtn;


    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private ProgressDialog loader;
    private DatabaseReference userDatabaseRef;
    private  String currentUserOnlineID;
    private CircleImageView profileImage;

    private Uri resultUri;
    Activity mActivity;
    Context mContext;

    DatabaseHelper databaseHelper;
    DatabasehelperTwo databasehelperTwo;

    //init all items, including databases
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_add_user_for_manager, container, false);
        mActivity = getActivity();
        mContext = container.getContext();
        databaseHelper = new DatabaseHelper(mContext);
        databasehelperTwo = new DatabasehelperTwo(mContext);

        registrationEmail = view.findViewById(R.id.registrationEmail);
        loginPassword = view.findViewById(R.id.loginPassword);
        registrationAddress = view.findViewById(R.id.registrationAddress);
        registrationPhoneNumber = view.findViewById(R.id.registrationPhoneNumber);
        adminRegUserBtn = view.findViewById(R.id.adminRegUserBtn);
        registrationFullname = view.findViewById(R.id.registrationFullname);
        profileImage = view.findViewById(R.id.profileImage);
        registrationUserType = view.findViewById(R.id.registrationUserType);


        loader = new ProgressDialog(mContext);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        //onClickListener for adding profile picture
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        //onClickListener for adding user by admin, retrieving all data in text fields
        adminRegUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userType = registrationUserType.getText().toString().trim();
                final String email = registrationEmail.getText().toString().trim();
                final String password = loginPassword.getText().toString().trim();
                final String fullNames = registrationFullname.getText().toString().trim();
                final String address = registrationAddress.getText().toString().trim();
                final String phoneNumber = registrationPhoneNumber.getText().toString().trim();


                /*validating if all data has been entered and not left blank,
                if not an appropriate error message is shown to the admin
                 */

                if (TextUtils.isEmpty(userType)){
                    registrationUserType.setError("User Type Required!");

                    return;
                }

                if(!(userType.equals("resident")  || userType.equals("volunteer"))){
                    registrationUserType.setError("Write resident or volunteer");
                    return;
                }

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
                    Toast.makeText(getActivity(), "profile Image is required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    //starting the registration process
                    loader.setMessage("Registration in progress...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    //calling on auth in firebase to register a new user with the given email and password
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                String error = task.getException().toString();
                                Toast.makeText(getActivity(), "Registration Failed: \n" + error, Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }else {

                                //adding the new suer to the firestore database to the users collection

                                currentUserOnlineID = mAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = firestore.collection("users").document(currentUserOnlineID);
                                userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserOnlineID);

                                HashMap userInfo = new HashMap();
                                userInfo.put("id",currentUserOnlineID);
                                userInfo.put("name",fullNames);
                                userInfo.put("email", email);
                                userInfo.put("address", address);
                                userInfo.put("phonenumber", phoneNumber);
                                userInfo.put("type", userType);


                                documentReference.set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(mActivity, "Details set successfully on Firestore", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mActivity, "Details upload Failed on Firestore", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                //updating the collection
                                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(mActivity, "Details set successfully", Toast.LENGTH_SHORT).show();
                                        }else {
                                            String error = task.getException().toString();
                                            Toast.makeText(mActivity, "Details upload Failed: "+ error, Toast.LENGTH_SHORT).show();
                                        }
//                                        mActivity.finish();
                                        loader.dismiss();
                                    }
                                });

                                //uploading the profile picture
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
                                                                        /* after successfully adding user to fireStore now the user is added to
                                                                           the SQLite database based on their type: resident/volunteer
                                                                        */
                                                                        Toast.makeText(mActivity, "Registration successful", Toast.LENGTH_SHORT).show();
                                                                        if(userType.equals("resident")){
                                                                            databaseHelper.addResidentData(fullNames, address);
                                                                        } else if(userType.equals("volunteer")){
                                                                            databasehelperTwo.addVolunteerData(fullNames, address);
                                                                        }

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

                                    //replacing the registration fragment with the fragment showing all users
                                    replaceFragment(new All_app_users_fragment());
////                                    Intent intent = new Intent(getActivity(), Drawer_options_activity.class);
////                                    startActivity(intent);
//                                    mActivity.finish();
                                    loader.dismiss();

                                }

                            }}

                    });
                }
            }
        });




        return view;
    }

// handling fragment attach and detach
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

    //retrieving the result of the activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1 && resultCode == Activity.RESULT_OK && data != null){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            profileImage.setImageURI(resultUri);
        }
    }


    //method to replace current fragment
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.drawer_options_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}