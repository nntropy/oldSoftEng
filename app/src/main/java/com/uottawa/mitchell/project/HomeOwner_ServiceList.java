package com.uottawa.mitchell.project;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class HomeOwner_ServiceList extends AppCompatActivity {
    private ArrayList<Service> services;
    ListView listview;
    TextView noserviceView;
    ArrayList<String> arrayList;



    DatabaseReference databaseServices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initilizes views
        setContentView(R.layout.activity_home_owner__service_list);
        listview =  findViewById(R.id.servicesList);
        noserviceView=findViewById(R.id.noServices2);
        // Initializes database to Services
        services = new ArrayList<>();
        arrayList = new ArrayList<>();
        databaseServices = FirebaseDatabase.getInstance().getReference().child("Services");
        arrayList = getIntent().getStringArrayListExtra("ids");
        // Gets services
        if (arrayList.size() > 0 ) {
            for (String id : arrayList) {
                databaseServices.child(id).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Maps services. This is done as we need EVERY service, and it's the easiest way I could think of
                                // Not sure if there is a better way
                                services.add(new Service(dataSnapshot.child("name").getValue().toString(),
                                        dataSnapshot.child("role").getValue().toString(),
                                        dataSnapshot.child("rate").getValue().toString(),
                                        dataSnapshot.getKey()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {


                            }
                        });
            }
            noserviceView.setVisibility(View.GONE);
            populateListView(services);
        }
        else {
            Toast.makeText(this, "No search results!", Toast.LENGTH_LONG);
        }
    }



    /*Populates List view with populateLustView(ArrayList <Service> list) list in parameter*/

    public void populateListView (ArrayList<Service> list) {
        //creating and innitializing custom Service adaptor for list view
        ServiceAdaptor adaptor = new ServiceAdaptor(this, list);
        listview.setAdapter(adaptor);
        //setting on click listener for when user clicks on a item in a list to redirect them to EditServicesActivity.class
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //creating a var to store the selected service
                Service selectedservice;
                //storing the selected service in selectedservice
                selectedservice = services.get(position);
                //creating intent that will direct to EditServiceActivity
                Intent intent = new Intent(HomeOwner_ServiceList.this, SomethingGood.class);
                //putting the name,type, and rate info through intent to pass to EditServiceActivity
                // (I was debating on passing a service object) but to do that i needed to make Service
                // a parcelable object which i could do if yo guys want :) but for now this should do the trick
                intent.putExtra("id", selectedservice.getId());
                //starting activity
                startActivity(intent);
            }
        }); }
}
