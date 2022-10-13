package Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.newprojectmishanxx.Model.Service;
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
import java.util.UUID;

import DB.DatabaseHelper;
import de.hdodenhof.circleimageview.CircleImageView;

//A Fragment for the manager to add a service
public class Services_registration extends Fragment {

    View view;
    Activity mActivity;
    Context mContext;
    private LinearLayout serviceRegistrationScrollView;
    private Button addButton;
    private TextInputEditText serviceRegistrationName;

    private FirebaseFirestore firestore;
    private ProgressDialog loader;

    DatabaseHelper databaseHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_services_registration, container, false);
        mActivity = getActivity();
        mContext = container.getContext();
        serviceRegistrationScrollView = view.findViewById(R.id.serviceRegistrationScrollView);
        databaseHelper = new DatabaseHelper(mContext);

        serviceRegistrationName = view.findViewById(R.id.serviceRegistrationName);
        addButton = view.findViewById(R.id.addButton);

        loader = new ProgressDialog(getActivity());
        firestore = FirebaseFirestore.getInstance();


        //OnClickListener for adding a service to the ListView, database, and firestore
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting service name
                final String name = serviceRegistrationName.getText().toString();

                //if the field is empty display appropriate error message
                if (TextUtils.isEmpty(name)){
                    serviceRegistrationName.setError("Service name Required!");
                    return;
                }

                //if service name is not empty
                else {
                    loader.setMessage("Registration in progress...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    //creating a new service
                    Service service = new Service(name);
                    int id = service.getId();

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage(R.string.are_you_sure)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //adding the service to the firestore
                                    firestore.collection("services").document(String.valueOf(id)).set(service).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                //adding service to SQLite database in case the service was added successfully to the firestore
                                                Snackbar.make(serviceRegistrationScrollView, getString(R.string.added_successfully), Snackbar.LENGTH_SHORT).show();
                                                databaseHelper.addServiceData(service);
                                                //after adding service move admin to services fragment to view all services
                                                replaceFragment(new Services_fragment());
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            System.out.println(e);
                                        }
                                    });

                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    loader.dismiss();



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


    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.drawer_options_layout, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

}