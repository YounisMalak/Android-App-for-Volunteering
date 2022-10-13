//package Fragments;
//
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
//import com.example.newprojectmishanxx.Model.Service;
//import com.example.newprojectmishanxx.R;
//
//import DB.DatabaseHelper;
//
//public class All_Services_fragment extends android.app.Fragment {
//
//    View view;
//    private static final String TAG = "AllServicesFragment";
//
//    DatabaseHelper databaseHelper;
//    private Button all_services_addBtn, all_services_viewDataBtn;
//    private EditText edt_to_add_service;
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        view = inflater.inflate(R.layout.fragment_all_services_fragment, container, false);
//
//        edt_to_add_service = (EditText) view.findViewById(R.id.edt_to_add_service);
//        all_services_addBtn = (Button) view.findViewById(R.id.all_services_addBtn);
//        all_services_viewDataBtn = (Button) view.findViewById(R.id.all_services_viewDataBtn);
//        databaseHelper = new DatabaseHelper(view.getContext());
//
//
//        all_services_addBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String newEntry = edt_to_add_service.getText().toString();
//                if(edt_to_add_service.length() != 0 ){
//                    addServiceData(newEntry);
//                    edt_to_add_service.setText("");
//                } else{
//                    toastMessage("You must enter text in text field!");
//                }
//            }
//        });
//
//        all_services_viewDataBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                replaceFragment(new List_of_all_services());
//            }
//        });
//
//
//        return view;
//    }
//
//    public void addServiceData(String newEntry){
//        Service service = new Service(newEntry);
//        boolean insertData = databaseHelper.addServiceData(service);
//
//        if(insertData){
//            toastMessage("Service data successfuly inserted");
//        } else{
//            toastMessage("Something went wrong");
//        }
//    }
//
//
//
//    private void toastMessage(String message){
//        Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
//    }
//
//
//    public void replaceFragment(Fragment fragment) {
//
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.usersLayout, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }
//}