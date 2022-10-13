package Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newprojectmishanxx.R;
import com.example.newprojectmishanxx.Users_Activity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

//fragment for users to update their details
public class Profile_fragment extends Fragment {

    View view;
    private Toolbar settingsToolbar;
    private CircleImageView profile_image;
    private EditText fullName, email, phonenumber, address;
    private Button updateDetailsButton;
    private TextView backButton;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;
    private ProgressDialog loader;
    private Uri profileImageUri;

    private String mName, mEmail,mPhoneNumber, mAddress,mProfilePicture = "";

    StorageTask uploadTask;
    StorageReference storageReference;
    private String imageSaveLocationUrl ="";

    Activity mActivity;
    Context mContext;

    //init all needed items, firebase authentication, firebase storage for images

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_profile_fragment, container, false);
        mActivity = getActivity();
        mContext = container.getContext();

        settingsToolbar = view.findViewById(R.id.settingsToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(settingsToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile Information");

        profile_image = view.findViewById(R.id.profileImage);
        fullName = view.findViewById(R.id.fullName);
        email = view.findViewById(R.id.email);
        phonenumber = view.findViewById(R.id.phonenumber);
        address = view.findViewById(R.id.address);
        updateDetailsButton = view.findViewById(R.id.updateDetailsButton);
        backButton = view.findViewById(R.id.backButton);
        loader = new ProgressDialog(mContext);

        mAuth = FirebaseAuth.getInstance();
        userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        storageReference = FirebaseStorage.getInstance().getReference("users details images");
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue(String.class);
                //if user type is resident or admin
                if (!type.equals("volunteer")){
                    getUserInformation();
                //if user is volunteer
                }else {
                    getVolunteerInformation();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //click listener for changing profile picture
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        //if user pressed on the update details button, call method to perform validation
        updateDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performValidations();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });

        return view;
    }



//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        mContext = context;
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mContext = null;
//    }

    //finding volunteer in firestore and retrieving their details and shows it
    private void getVolunteerInformation() {
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") !=null){
                        mName = map.get("name").toString();
                        fullName.setText(mName);
                        fullName.setSelection(mName.length());
                    }
                    if (map.get("email") !=null){
                        mEmail = map.get("email").toString();
                        email.setText(mEmail);
                        email.setSelection(mEmail.length());
                    }
                    if (map.get("address") !=null){
                        mAddress = map.get("address").toString();
                        address.setText(mAddress);
                        address.setSelection(mAddress.length());
                    }
                    if (map.get("phonenumber") !=null){
                        mPhoneNumber = map.get("phonenumber").toString();
                        phonenumber.setText(mPhoneNumber);
                        phonenumber.setSelection(mPhoneNumber.length());
                    }

                    if (map.get("profilepictureurl") !=null){
                        mProfilePicture = map.get("profilepictureurl").toString();
                        Glide.with(mActivity.getApplication()).load(mProfilePicture).into(profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //finding user in firestore and retrieving their details and shows it
    private void getUserInformation(){
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") !=null){
                        mName = map.get("name").toString();
                        fullName.setText(mName);
                        fullName.setSelection(mName.length());
                    }
                    if (map.get("email") !=null){
                        mEmail = map.get("email").toString();
                        email.setText(mEmail);
                        email.setSelection(mEmail.length());
                    }

                    if (map.get("phonenumber") !=null){
                        mPhoneNumber = map.get("phonenumber").toString();
                        phonenumber.setText(mPhoneNumber);
                        phonenumber.setSelection(mPhoneNumber.length());
                    }
                    if (map.get("address") !=null){
                        mAddress = map.get("address").toString();
                        address.setText(mAddress);
                        address.setSelection(mAddress.length());
                    }


                    if (map.get("profilepictureurl") !=null){
                        mProfilePicture = map.get("profilepictureurl").toString();
                        Glide.with(mActivity.getApplication()).load(mProfilePicture).into(profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //a method that retrieves text from text fields and makes sure that none of the fields are empty
    private void performValidations(){
        final String nameString  = fullName.getText().toString();
        final String emailString = email.getText().toString();
        final String phoneString = phonenumber.getText().toString();
        final String addressString = address.getText().toString();

        //if none of the fields are empty, call updateUserInfo and updateVolunteerInfo

        if (!nameString.isEmpty() &&
                !emailString.isEmpty() &&
                !phoneString.isEmpty() &&
                !addressString.isEmpty() &&
                profileImageUri !=null){

            uploadUserInfo();

        }

        if (!nameString.isEmpty() &&
                !emailString.isEmpty() &&
                !phoneString.isEmpty() &&
                !addressString.isEmpty() &&
                profileImageUri !=null){

            uploadVolunteerInfo();

        }else {
            Toast.makeText(mActivity, "Check missing fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLoader(){
        loader.setMessage("Uploading details. Please wait...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
    }

    //getting file extension
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = mActivity.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //updating user's info in firestore
    private void uploadUserInfo() {
        startLoader();

        HashMap<String, Object> hashMap =   new HashMap<>();
        hashMap.put("name", fullName.getText().toString() );
        hashMap.put("phonenumber",phonenumber.getText().toString());
        hashMap.put("address",address.getText().toString());
        hashMap.put("email", email.getText().toString());


        userDatabaseRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(mActivity, "Details Uploaded successfully", Toast.LENGTH_SHORT).show();
                    uploadProfilePicture();
                }else {
                    Toast.makeText(mActivity, "Failed to upload details" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //updating volunteers's info in firestore
    private void uploadVolunteerInfo() {
        startLoader();

        HashMap<String, Object> hashMap =   new HashMap<>();
        hashMap.put("name", fullName.getText().toString() );
        hashMap.put("phonenumber",phonenumber.getText().toString());
        hashMap.put("address",address.getText().toString());
        hashMap.put("email", email.getText().toString());


        userDatabaseRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(mActivity, "Details Uploaded successfully", Toast.LENGTH_SHORT).show();
                    uploadProfilePicture();
                }else {
                    Toast.makeText(mActivity, "Failed to upload details" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //a method that uploads users profile picture to the storage in firebase
    private void uploadProfilePicture() {
        final StorageReference fileReference;
        fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(profileImageUri));
        uploadTask = fileReference.putFile(profileImageUri);
        uploadTask.continueWithTask(new Continuation(){
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isComplete()){
                    throw Objects.requireNonNull(task.getException());
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task <Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    imageSaveLocationUrl = downloadUri.toString();

                    HashMap<String, Object> hashMap =   new HashMap<>();
                    hashMap.put("profilepictureurl", imageSaveLocationUrl);

                    userDatabaseRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(mActivity, "profile picture added successfully", Toast.LENGTH_SHORT).show();
                                //if everything is successful user is moved to user's activity
                                directUserToMainActivity();
                            }
                            loader.dismiss();
                            mActivity.finish();
                        }
                    });
                    // finish();

                }else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(mActivity, "Failed" + error, Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mActivity, "Profile image could not be added."+ e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==1 && resultCode == Activity.RESULT_OK ){
            profileImageUri = data.getData();
            profile_image.setImageURI(profileImageUri);
        }
    }

    //method that handles menu item selected
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

    //method that moves user to user's activity
    private void directUserToMainActivity(){
        Intent intent = new Intent(mActivity, Users_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        mActivity.finish();
    }
}