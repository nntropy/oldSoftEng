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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.concurrent.CountDownLatch;

import java.util.ArrayList;
import java.util.Map;

public class ServiceProvider_HomePage extends AppCompatActivity {
    private static final int CHRONO_BLACK = Color.parseColor("#1F1F1F");

    private ArrayList <Service> enlistedServices;
    private ArrayList <String> serviceID;
    ListView listview;
    DatabaseReference databaseServices;
    DatabaseReference dbServices;
    FirebaseAuth fireBaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider__home_page);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(CHRONO_BLACK));
        setTitle("Home Page");
        enlistedServices = new ArrayList<>();
        databaseServices = FirebaseDatabase.getInstance().getReference().child("Users");
        dbServices = FirebaseDatabase.getInstance().getReference().child("Services");
        fireBaseAuth = FirebaseAuth.getInstance();
        listview=findViewById(R.id.enlistedServicesList);
        authStateListener = new FirebaseAuth.AuthStateListener() {

            /**
             *
             * @param firebaseAuth
             *
             * This method implements the service provider homepage.
             *
             * This method, if user is logged in, gets all Services the service provider is enrolled in
             */
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {

                }
            }
        };
    }
    @Override
    public void onResume() {
        super.onResume();
        enlistedServices.clear();
        fireBaseAuth.addAuthStateListener(authStateListener);
        System.out.println(fireBaseAuth.getCurrentUser().getUid());
        // Gets services
        databaseServices.child(fireBaseAuth.getCurrentUser().getUid()).child("Services").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    collectServices((Map<String, Object>) dataSnapshot.getValue());
                } catch (Exception e) {
                    //do nothing
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void populateListView (ArrayList<Service> list){
        //creating and initializing custom Service adaptor for list view
        ServiceAdaptor adaptor = new ServiceAdaptor( this, list);
        listview.setAdapter(adaptor);
        adaptor.notifyDataSetChanged();
        //setting on click listener for when user clicks on a item in a list to redirect them to EditServicesActivity.class
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //creating a var to store the selected service
                Service selectedEnlistedservice;
                //storing the selected service in selectedservice
                selectedEnlistedservice=enlistedServices.get (position);
                //creating intent that will direct to EditServiceActivity
                Intent intent = new Intent(ServiceProvider_HomePage.this, ServiceProvider_Availability.class);
                //putting the service ID and setting isNull to false through intent to pass to EditServiceActivity
                intent.putExtra("serviceID",selectedEnlistedservice.getId());
                intent.putExtra("isNull",false);
                //starting activity
                startActivity(intent);
            }
        });
    }


    private void collectServices(Map<String,Object> users) {
        // Creates ArrayList of all Services
        // NOTE: THIS ARRAYLIST IS STORING Service (name, role, rate, id)
        // Therefore to access individual values you must call built-ins.

        //iterate through each service, ignoring the UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Gets id
            String tid = entry.getKey();
            makeService(tid);
        }

    }
    public void makeService(String id) {
        dbServices.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.toString());
                enlistedServices.add( new Service(
                        dataSnapshot.child("name").getValue().toString(),
                        dataSnapshot.child("role").getValue().toString(),
                        dataSnapshot.child("rate").getValue().toString(),
                        dataSnapshot.getKey()));
                populateListView(enlistedServices);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void btnEnlistPage(View view) {
        startActivity(new Intent(ServiceProvider_HomePage.this, ServiceProvider_EnlistPage.class));
    }
}