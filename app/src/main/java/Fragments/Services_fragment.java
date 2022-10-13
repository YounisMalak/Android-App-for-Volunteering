package Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newprojectmishanxx.Model.Service;
import com.example.newprojectmishanxx.Model.User;
import com.example.newprojectmishanxx.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import DB.DatabaseHelper;
import de.hdodenhof.circleimageview.CircleImageView;

//Fragment that shows a list of services, with a button to add a service for the admin
public class Services_fragment extends Fragment {
    private static final String TAG = "ListDataFragment";
    View view;
    private Toolbar settingsToolbar;
    private LinearLayout servicesLinearLayout;
    Button addServiceBtn;
    private ListView services_listview;
    DatabaseHelper databaseHelper;

    Activity mActivity;
    Context mContext;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_services_fragment, container, false);
        servicesLinearLayout = view.findViewById(R.id.servicesLinearLayout);
        firestore = FirebaseFirestore.getInstance();
        mActivity = getActivity();
        mContext = container.getContext();
        services_listview = view.findViewById(R.id.services_listview);
        databaseHelper = new DatabaseHelper(view.getContext());

        //clickListener for admin to add a service, moves admin to service registration fragment
        addServiceBtn = view.findViewById(R.id.addServiceBtn);
        addServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new Services_registration());
            }
        });


        //handling the action bar
        settingsToolbar = view.findViewById(R.id.settingsToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(settingsToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("All Services");

        populateListView();


        return view;
    }

    // a method that displays all services in a ListView
    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying Data in ListView");

        //get the data and oppend to a list
        Cursor data = databaseHelper.getAllServicesData();
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {
            //get the value from the database in column 1
            //then add it to the ArrayList
            listData.add(data.getString(1));
        }
        //create the list adapter and set the adapter
        ListAdapter adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, listData);
        services_listview.setAdapter(adapter);

        //set an onItemClickListener on the ListView
        services_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "onItemClick: You Clicked on " + name);

                Cursor data = databaseHelper.getServiceItemID(name); // get the id associated with that name
                int itemID = -1;
                while (data.moveToNext()) {
                    itemID = data.getInt(0);
                }
                if (itemID > -1) {
                    Log.d(TAG, "onItemClick: The ID is: " + itemID);

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    int finalItemID = itemID;
                    builder.setMessage(R.string.do_you_want_to_delete_this_service)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    firestore.collection("services").document(String.valueOf(finalItemID)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Snackbar.make(servicesLinearLayout, getString(R.string.deleted_successfully), Snackbar.LENGTH_SHORT).show();
                                            databaseHelper.deleteService(finalItemID, name);
                                            replaceFragment(new Services_fragment());
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

                } else {
                    toastMessage("No Id associated with that name");
                }
            }
        });

    }

    private void toastMessage(String message){
        Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
    }


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

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.drawer_options_layout, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }
}