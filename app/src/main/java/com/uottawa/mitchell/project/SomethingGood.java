package com.uottawa.mitchell.project;

import android.content.Intent;
import android.provider.ContactsContract;
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

public class SomethingGood extends AppCompatActivity {
    private ArrayList<Service> services;
    ListView listview;
    TextView noserviceView;
    String id;
    Boolean isTime;



    DatabaseReference databaseServices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initializes views
        setContentView(R.layout.activity_home_owner__service_list);
        listview =  findViewById(R.id.servicesList);
        noserviceView=findViewById(R.id.noServices2);
        // Initializes database to Services
        services = new ArrayList<>();
        databaseServices = FirebaseDatabase.getInstance().getReference();
        id = getIntent().getStringExtra("id");
        // Gets services
        if (id.length() > 0) {
                databaseServices.child("Users").addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Maps services. This is done as we need EVERY service, and it's the easiest way I could think of
                                // Not sure if there is a better way
                                for (DataSnapshot clean : dataSnapshot.getChildren()) {
                                    for (DataSnapshot swag : clean.child("Services").getChildren()) {
                                        if (id.equals(swag.getKey())) {
                                            services.add(new Service(clean.child("firstName").getValue().toString() + " " + clean.child("lastName").getValue().toString(),
                                                    swag.child("Licensed").getValue().toString(),
                                                    clean.getKey(),
                                                    id));
                                        }
                                    }
                                }
                                noserviceView.setVisibility(View.GONE);
                                populateListView(services);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {


                            }
                        });
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
                Intent intent = new Intent(SomethingGood.this, HomeOwner_BookService.class);
                //putting the name,type, and rate info through intent to pass to EditServiceActivity
                // (I was debating on passing a service object) but to do that i needed to make Service
                // a parcelable object which i could do if yo guys want :) but for now this should do the trick
                intent.putExtra("spID", selectedservice.getHourlyrate());
                intent.putExtra("serviceID", selectedservice.getId());
                //starting activity
                startActivity(intent);
            }
        }); }


}
