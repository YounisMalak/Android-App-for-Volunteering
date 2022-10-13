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
//
//
//public class All_Residents_fragment extends Fragment {
//
//    View view;
//    private static final String TAG = "AllResidentFragment";
//    Activity activity;
//
//    DatabaseHelper databaseHelper;
//    private Button all_residents_addBtn, all_residents_viewDataBtn;
//    private EditText edt_to_add_resident, edt_to_add_resident_address;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        view =  inflater.inflate(R.layout.fragment_all__residents_fragment, container, false);
//
//        activity = getActivity();
//        activity.setTitle(getString(R.string.all_residents));
//
//
//        edt_to_add_resident = (EditText) view.findViewById(R.id.edt_to_add_resident);
//        edt_to_add_resident_address = (EditText) view.findViewById(R.id.edt_to_add_resident_address);
//        all_residents_addBtn = (Button) view.findViewById(R.id.all_residents_addBtn);
//        all_residents_viewDataBtn = (Button) view.findViewById(R.id.all_residents_viewDataBtn);
//        databaseHelper = new DatabaseHelper(view.getContext());
//
//        all_residents_addBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String newEntryResName = edt_to_add_resident.getText().toString();
//                String newEntryResAddress = edt_to_add_resident_address.getText().toString();
//                if(edt_to_add_resident.length() != 0 && edt_to_add_resident_address.length() != 0){
//                    addResidentData(newEntryResName, newEntryResAddress);
//                    edt_to_add_resident.setText("");
//                    edt_to_add_resident_address.setText("");
//                } else{
//                    toastMessage("You must enter text in text field!");
//                }
//            }
//        });
//
//
////        all_residents_viewDataBtn.setOnClickListener(new View.OnClickListener() {
////            @Override
//////            public void onClick(View view) {
//////                replaceFragment(new List_of_all_residents());
//////            }
////        });
//
//
//        return view;
//    }
//
//    public void addResidentData(String newEntryName, String newEntryAddress){
//        boolean insertData = databaseHelper.addResidentData(newEntryName, newEntryAddress);
//
//        if(insertData){
//            toastMessage("Resident data successfuly inserted");
//        } else{
//            toastMessage("Resident: Something went wrong");
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