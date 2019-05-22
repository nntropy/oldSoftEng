package com.uottawa.mitchell.project;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ServiceProvider_EnlistPage extends AppCompatActivity {
    private static final int BLACK = Color.parseColor("#1F1F1F");
    private ArrayList<Service> services;
    ListView listview;
    DatabaseReference databaseServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider__enlist_page);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(BLACK));
        setTitle("Enlist Page");
        listview =  findViewById(R.id.enlistedServicesList);

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

                        // populates listView
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
                Intent intent = new Intent( ServiceProvider_EnlistPage.this, ServiceProvider_Availability.class);
                //putting the name,type, and rate info through intent to pass to EditServiceActivity
                intent.putExtra("serviceID",selectedservice.getId());
                intent.putExtra("isNull",true);
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
