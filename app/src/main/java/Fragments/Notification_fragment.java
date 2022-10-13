package Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.newprojectmishanxx.Adapter.NotificationAdapter;
import com.example.newprojectmishanxx.Model.Notification;
import com.example.newprojectmishanxx.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//fragment that displays all notifications for user

public class Notification_fragment extends Fragment {

    View view;
    Activity mActivity;
    Context mContext;
    private Toolbar notifications_toolbar;

    private RecyclerView notifications_recycler_view;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    private ImageView search_error_image;

    //init all needed items
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notification_fragment, container, false);
        mActivity = getActivity();
        mContext = container.getContext();

        search_error_image = view.findViewById(R.id.search_error_image);

        notifications_toolbar = view.findViewById(R.id.notifications_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(notifications_toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        //populating RecyclerView with array list of notifications and setting adapter
        notifications_recycler_view = view.findViewById(R.id.notifications_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        notifications_recycler_view.setHasFixedSize(true);
        notifications_recycler_view.setLayoutManager(layoutManager);

        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(mContext, notificationList);
        notifications_recycler_view.setAdapter(notificationAdapter);

        readNotifications();

        return view;
    }

    //a method that gets all the notifications that concern the current user from the notifications collection in firestore
    private void readNotifications() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notifications").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationList.clear();
                //adding notification to arraylist
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Notification notification = snapshot.getValue(Notification.class);
                    notificationList.add(notification);

                }

                Collections.reverse(notificationList);
                notificationAdapter.notifyDataSetChanged();

                if (notificationList.isEmpty()){
                    search_error_image.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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


}