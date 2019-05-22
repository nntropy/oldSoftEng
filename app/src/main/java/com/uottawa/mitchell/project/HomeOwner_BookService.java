package com.uottawa.mitchell.project;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class HomeOwner_BookService extends AppCompatActivity {
    FirebaseAuth fireBaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    Spinner spinnerFrom, spinnerTo, spinnerDay;
    DatabaseReference databaseService, databaseUsers;
    String serviceID, providerID, serviceStartTime, serviceEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_owner__book_service);

        //Creates a spinner for start value of the search by time
        spinnerFrom = (Spinner) findViewById(R.id.spinnerSearchFrom);
        spinnerTo = (Spinner) findViewById(R.id.spinnerSearchTo);
        ArrayAdapter<CharSequence> adapterHour = ArrayAdapter.createFromResource(this,
                R.array.hour, android.R.layout.simple_spinner_item);
        adapterHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Both use the same adapter
        spinnerFrom.setAdapter(adapterHour);
        spinnerTo.setAdapter(adapterHour);

        //Creates a spinner for the selected day
        spinnerDay = (Spinner) findViewById(R.id.spinnerChooseDay);
        ArrayAdapter<CharSequence> adapterDay = ArrayAdapter.createFromResource(this,
                R.array.day_Array, android.R.layout.simple_spinner_item);
        adapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapterDay);

    }

    @Override
    protected void onResume() {
        super.onResume();

        fireBaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {

                }
            }
        };


        databaseService = FirebaseDatabase.getInstance().getReference().child("Services");
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        //get value of serviceID from the previous intent
        Intent receivedIntent = getIntent();
        serviceID = receivedIntent.getStringExtra("serviceID");

        //get the service provider ID
        providerID = receivedIntent.getStringExtra("spID");

    }

    public void btnRateService(View view) {
        Intent intent = new Intent (this, HomeOwner_RateService.class);
        intent.putExtra("serviceID", serviceID);
        startActivity(intent);
    }

    public void btnCreateBooking(View view) {

        //uses default values if nothing is passed
        DatabaseReference timeRef = databaseUsers.child(providerID).child("Services").child(serviceID);
        timeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //this contains the map from the database of the service times
                Map textFields = (Map<String, Object>) dataSnapshot.getValue();

                String day = spinnerDay.getSelectedItem().toString().toLowerCase().trim();

                serviceStartTime = textFields.get(day + "From").toString();
                serviceEndTime = textFields.get(day + "To").toString();

                if (!checkInputEntries() ) {
                    return;
                }

                String startTime = spinnerFrom.getSelectedItem().toString();
                String endTime = spinnerTo.getSelectedItem().toString();

                String homeOwnerID = fireBaseAuth.getCurrentUser().getUid();


                databaseService = databaseService.child(serviceID).child("Booking");
                databaseService.child(homeOwnerID).child("day").setValue(day);
                databaseService.child(homeOwnerID).child("startTime").setValue(startTime);
                databaseService.child(homeOwnerID).child("endTime").setValue(endTime);

                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Compares individual startTime to endTime to ensure that endTime > startTime
     * @return start time is greater than end time
     */
    public boolean checkTimesNotGreater(Spinner startTime, Spinner endTime) {
        String startTimeValue = startTime.getSelectedItem().toString();
        String endTimeValue = endTime.getSelectedItem().toString();

        if (startTimeValue.equals("Unavailable") || endTimeValue.equals("Unavailable")) {
            //ensures that if startTime or endTime is unavailable then both must be listed as unavailable
            Toast.makeText(this, "Start and end time were not properly entered", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!((endTimeValue.compareTo(startTimeValue)) > 0)) {
            //return true if endTimeValue is lexicographically greater than startTimeValue
            Toast.makeText(this, "Start time is larger than or equal to end time", Toast.LENGTH_SHORT).show();
            return false;
        } else /*must ensure that time input are within the bounds of the service providers availabilities*/{

            if ( serviceStartTime.equals("Unavailable") ||
                    serviceEndTime.equals("Unavailable") ) {
                Toast.makeText(this, "The service provider is not available on this day", Toast.LENGTH_SHORT).show();
                return false;
            }else if ( (startTimeValue.compareTo(serviceStartTime) < 0) ||
                    (endTimeValue.compareTo(serviceEndTime) > 0) ) {
                Toast.makeText(this, "Booking times must be within availability of service provider", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Start Time: " + serviceStartTime, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "End time: " + serviceEndTime, Toast.LENGTH_SHORT).show();

                return false;
            }
        }
        return true;
    }

    /**
     * Ensure all times correspond to a proper availability of the service provider
     * @return boolean
     */
    public boolean checkAvailabilityExists() {
        return true;
    }

    /**
     * Ensures that all fields have been input correctly
     * Ensures that end time is greater than start time
     * @return boolean
     */
    public boolean checkInputEntries() {
        //check the time spinners to ensure that time have been input properly
        if ( (!checkTimesNotGreater(spinnerFrom, spinnerTo)) || (!checkAvailabilityExists()) ) {
            return false;
        }
        return true;
    }
}
