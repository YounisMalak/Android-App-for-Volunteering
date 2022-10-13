//package Fragments;
//
//import android.app.Activity;
//import android.app.Fragment;
//import android.app.FragmentTransaction;
//import android.os.Bundle;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.example.newprojectmishanxx.R;
//
//import DB.DatabaseHelper;
//import DB.DatabasehelperTwo;
//
//public class All_Volunteers_fragment extends Fragment {
//
//    View view;
//    private static final String TAG = "AllVolunteersFragment";
//
////    DatabaseHelper databaseHelper;
//    DatabasehelperTwo databasehelperTwo;
//    private Button all_volunteers_addBtn, all_volunteers_viewDataBtn;
//    private EditText edt_to_add_volunteer, edt_to_add_volunteer_address;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        view =  inflater.inflate(R.layout.fragment_all__volunteers_fragment, container, false);
//
//
//
//        edt_to_add_volunteer = (EditText) view.findViewById(R.id.edt_to_add_volunteer);
//        edt_to_add_volunteer_address = (EditText) view.findViewById(R.id.edt_to_add_volunteer_address);
//        all_volunteers_addBtn = (Button) view.findViewById(R.id.all_volunteers_addBtn);
//        all_volunteers_viewDataBtn = (Button) view.findViewById(R.id.all_volunteers_viewDataBtn);
////        databaseHelper = new DatabaseHelper(view.getContext());
//        databasehelperTwo = new DatabasehelperTwo(view.getContext());
//
//
//        all_volunteers_addBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String newEntryVolName = edt_to_add_volunteer.getText().toString();
//                String newEntryVolAddress = edt_to_add_volunteer_address.getText().toString();
//                if(edt_to_add_volunteer.length() != 0 && edt_to_add_volunteer_address.length() != 0){
//                    addVolunteerData(newEntryVolName, newEntryVolAddress);
//                    edt_to_add_volunteer.setText("");
//                    edt_to_add_volunteer_address.setText("");
//                } else{
//                    toastMessage("You must enter text in text field!");
//                }
//            }
//        });
//
//
//        all_volunteers_viewDataBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.usersLayout, new List_of_all_volunteers());
//                transaction.addToBackStack(null);
//                transaction.commit();
//            }
//        });
//
//        return view;
//    }
//
//    public void addVolunteerData(String newEntryName, String newEntryAddress){
//        boolean insertData = databasehelperTwo.addVolunteerData(newEntryName, newEntryAddress);
//
//        if(insertData){
//            toastMessage("Volunteer data successfuly inserted");
//        } else{
//            toastMessage("Volunteer: Something went wrong");
//        }
//    }
//
//
//    private void toastMessage(String message){
//        Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
//    }
//
//
////    private void replaceFragment(Fragment fragment) {
////
////        FragmentTransaction transaction = getFragmentManager().beginTransaction();
////        transaction.replace(R.id.managerLayout, fragment);
////        transaction.addToBackStack(null);
////        transaction.commit();
////    }
//
//}