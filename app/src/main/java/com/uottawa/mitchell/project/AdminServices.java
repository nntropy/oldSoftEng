package com.uottawa.mitchell.project;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.view.View.GONE;

public class AdminServices extends AppCompatActivity {
    private static final String TAG = "AdminServices";
    private ArrayList<Service> services;
    ListView listview;
    TextView noserviceView;



    DatabaseReference databaseServices;

    //Keep these please, will need in the future -Aidan
    //FirebaseAuth fireBaseAuth;
    //FirebaseAuth.AuthStateListener authStateListener;
    //DatabaseReference firstName, role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initilizes views
        setContentView(R.layout.activity_admin_services);
        listview =  findViewById(R.id.servicesList);
        noserviceView=findViewById(R.id.noServices);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Initializes database to Services
        databaseServices = FirebaseDatabase.getInstance().getReference().child("Services");
        // Gets services
        databaseServices.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Maps services. This is done as we need EVERY service, and it's the easiest way I could think of
                        // Not sure if there is a better way
                        services = collectServices((Map<String, Object>) dataSnapshot.getValue());

                        // Add the ArrayList to the view, Bilal
                        noserviceView.setVisibility(View.GONE);
                        populateListView(services);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {


                    }
          });
    }

    /*Populates List view with populateLustView(ArrayList <Service> list) list in parameter*/

    public void populateListView (ArrayList<Service> list){
        //creating and innitializing custom Service adaptor for list view
        ServiceAdaptor adaptor = new ServiceAdaptor( this, list);
        listview.setAdapter(adaptor);
        //setting on click listener for when user clicks on a item in a list to redirect them to EditServicesActivity.class
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //creating a var to store the selected service
                Service selectedservice;
                //storing the selected service in selectedservice
                selectedservice=services.get (position);
                //creating intent that will direct to EditServiceActivity
                Intent intent = new Intent(AdminServices.this, EditServicesActivity.class);
                //putting the name,type, and rate info through intent to pass to EditServiceActivity
                // (I was debating on passing a service object) but to do that i needed to make Service
                // a parcelable object which i could do if yo guys want :) but for now this should do the trick
                intent.putExtra("name",selectedservice.getName());
                intent.putExtra ("type",selectedservice.getType());
                intent.putExtra("rate",selectedservice.getHourlyrate());
                intent.putExtra("id",selectedservice.getId());
                //starting activity
                startActivity(intent);


            }
        });
        

    }
    private ArrayList<Service>  collectServices(Map<String,Object> users) {
        // Creates ArrayList of all Services
        // NOTE: THIS ARRAYLIST IS STORING Service (name, role, rate, id)
        // Therefore to access individual values you must call built-ins.
        ArrayList<Service> services = new ArrayList<>();

        //iterate through each service, ignoring the UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Gets all attributes and creates a service
            String tname = singleUser.get("name").toString();
            String trole = singleUser.get("role").toString();
            String trate = singleUser.get("rate").toString();
            String tid = entry.getKey();
            services.add(new Service (tname, trole, trate, tid));
        }
        return services;

    }

}
