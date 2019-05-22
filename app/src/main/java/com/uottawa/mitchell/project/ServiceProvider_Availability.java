package com.uottawa.mitchell.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

/**
 * This class is used twice. Once when directed from the ServiceProvider_EnlistPage
 * where all spinners should be set to unavailable as nothing exists in the database.
 * This class is also used when directed from the ServiceProvider_HomePage
 * where all spinners should be filled with the values that exist in the database.
 */
public class ServiceProvider_Availability extends AppCompatActivity {

    //Declare global variables
    EditText editAddress, editNumber, editDescription, editLicensed;
    //make an array with all the spinners in it
    Spinner[] allSpinners;
    //This string array will be used to easily update all fields in the database
    String[] timeString = {"sundayFrom", "sundayTo", "mondayFrom", "mondayTo", "tuesdayFrom", "tuesdayTo",
                            "wednesdayFrom", "wednesdayTo", "thursdayFrom", "thursdayTo", "fridayFrom",
                            "fridayTo", "saturdayFrom", "saturdayTo"};

    ArrayAdapter<CharSequence> adapter;
    DatabaseReference databaseUsers;
    FirebaseAuth fireBaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    String serviceID, providerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider__availability);


        /**
         * Add all the spinners to an array to make it easier to access them.
         *
         * Notice that all the spinners are given in this order:
         * allSpinner[0] = sundayFrom
         * allSpinner[1] = sundayTo
         * .
         * .
         * .
         * allSpinner[12] = saturdayFrom
         * allSpinner[13] = saturdayTo
         */
        allSpinners = new Spinner[14];
        createSpinnerArray();

        //initialize all the editText boxes
        editAddress = findViewById(R.id.editTextAddress);
        editDescription = findViewById(R.id.editTextDescription);
        editNumber = findViewById(R.id.editTextNumber);
        editLicensed = findViewById(R.id.editTextLicensed);


        fireBaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
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

        //database related dependencies
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        /**
         * A String serviceID and Boolean isNull will be passed from the previous activity
         * IsNull represents whether or not the Service Provider is already registered to
         * this service.
         *
         * If isNull is true then the spinners are given default values of Unavailable.
         *
         * If isNull is false then the spinners are given the values that currently exist
         * in the database for the allotted time slots.
         */

        //making an intent var to get service info passed from either the ServiceProvider_EnlistPage or
        //the either the ServiceProvider_HomePage
        Intent receivedIntent = getIntent();

        //get the serviceID from the value passed in the intent
        serviceID = receivedIntent.getStringExtra("serviceID");

        //if the value is false (not null) then we can update the values of the spinners
        if (! receivedIntent.getBooleanExtra("isNull", true)) {
            updateSpinners();
            updateTexts();
        }
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
            if (! startTimeValue.equals(endTimeValue)){
                Toast.makeText(this, "Start and end time were not properly entered", Toast.LENGTH_SHORT).show();
            }
            return startTimeValue.equals(endTimeValue);
        } else {
            //return true if endTimeValue is lexicographically greater than startTimeValue
            if (! ((endTimeValue.compareTo(startTimeValue)) > 0)) {
                Toast.makeText(this, "Start time is larger than or equal to end time", Toast.LENGTH_SHORT).show();
            }
            return ((endTimeValue.compareTo(startTimeValue)) > 0);
        }

    }

    /**
     * Ensures that all fields have been input correctly
     * Ensures that end time is greater than start time
     * @return boolean
     */
    public boolean checkInputEntries() {
        //check all the spinners to ensure times have been input properly
        //The first index is the start time for the day, the second index is the endTime for the day
        for (int i = 0; i < 14; i += 2) {
            if (! checkTimesNotGreater(allSpinners[i], allSpinners[i+1])) {
                return false;
            }
        }

        return true;
    }

    /**
     * Ensures that all editText fields have proper input values
     *
     * editAddress and editNumber are mandatory and must be input.
     * editLicensed must be a yes or no input.
     * editDescription must be less than 20 characters.
     */
    public boolean checkTextEntries() {
        //check that all mandatory fields are input properly
        String licensed = editLicensed.getText().toString().trim().toLowerCase();
        String description = editDescription.getText().toString().trim().toLowerCase();
        if (editAddress.getText().toString().trim().equals("")
                || editNumber.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Address and Phone Number are mandatory fields", Toast.LENGTH_SHORT).show();
            return false;
        } else if (! (licensed.equals("yes") || licensed.equals("no"))) {
            Toast.makeText(this, "Input field for licensed must be Yes or No", Toast.LENGTH_SHORT).show();
            return false;
        } else if (description.length() > 20) { //should ensure there is a default description otherwise
            Toast.makeText(this, "Description must be less than 20 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createSpinnerArray() {

        //assign each spinner on the page ServiceProvider_Availability to an index in the allSpinners array
        allSpinners[0] = findViewById(R.id.spinnerSundayFrom);
        allSpinners[1] = findViewById(R.id.spinnerSundayTo);
        allSpinners[2] = findViewById(R.id.spinnerMondayFrom);
        allSpinners[3] = findViewById(R.id.spinnerMondayTo);
        allSpinners[4] = findViewById(R.id.spinnerTuesdayFrom);
        allSpinners[5] = findViewById(R.id.spinnerTuesdayTo);
        allSpinners[6] = findViewById(R.id.spinnerWednesdayFrom);
        allSpinners[7] = findViewById(R.id.spinnerWednesdayTo);
        allSpinners[8] = findViewById(R.id.spinnerThursdayFrom);
        allSpinners[9] = findViewById(R.id.spinnerThursdayTo);
        allSpinners[10] = findViewById(R.id.spinnerFridayFrom);
        allSpinners[11] = findViewById(R.id.spinnerFridayTo);
        allSpinners[12] = findViewById(R.id.spinnerSaturdayFrom);
        allSpinners[13] = findViewById(R.id.spinnerSaturdayTo);

        //setting up adapter
        adapter = ArrayAdapter.createFromResource(this, R.array.hour, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //set an adapter for each spinner
        for (Spinner spinner : allSpinners) {
            spinner.setAdapter(adapter);
        }
    }

    public void btnUpdate(View view) {

        //ensure all entries are correct
        if ( (!checkInputEntries()) || (!checkTextEntries()) ) {
            return;
        }
        //get the service provider ID to be used to access all availability times for this user from the Service
        providerID = fireBaseAuth.getCurrentUser().getUid();

        //makes uid key in table Services
        DatabaseReference Users = databaseUsers.child(providerID);

        //add all the spinner values to the database
        for (int i = 0; i < 14; i++) {
            //loop over all the names in the timeArray and all the values in the spinner array
            String timeName = timeString[i];
            String timeValue = allSpinners[i].getSelectedItem().toString();

            Users.child("Services").child(serviceID).child(timeName).setValue(timeValue);

        }

        //Add the edit text fields to the database as well
        Users.child("Services").child(serviceID).child("Address").setValue(editAddress.getText().toString().trim());
        Users.child("Services").child(serviceID).child("Number").setValue(editNumber.getText().toString().trim());
        Users.child("Services").child(serviceID).child("Description").setValue(editDescription.getText().toString().trim());
        Users.child("Services").child(serviceID).child("Licensed").setValue(editLicensed.getText().toString().trim());

        //Create message notifying the user that all values have been successfully uploaded
        Toast.makeText(this, "Values successfully updated", Toast.LENGTH_SHORT).show();

    }



    public void btnDelete(View view) {
        //get the service provider ID to be used to access all availability times for this user from the Service
        providerID = fireBaseAuth.getCurrentUser().getUid();

        dialog();
    }

    private void dialog() {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure you want to delete?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //makes uid key in table Services
                        DatabaseReference Users = databaseUsers.child(providerID);
                        Users.child("Services").child(serviceID).removeValue();
                        finish();
                    }
                }).create().show();
    }

    public void updateTexts() {
        //get the service provider ID to be used to access all availability times for this user from the Service
        providerID = fireBaseAuth.getCurrentUser().getUid();

        //makes uid key in table Services
        DatabaseReference Users = databaseUsers.child(providerID);
        DatabaseReference timeRef = Users.child("Services").child(serviceID);

        timeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //this contains the map from the database of the service times
                Map textFields = (Map<String, Object>) dataSnapshot.getValue();

                //update all the edit text fields
                editAddress.setText(textFields.get("Address").toString());
                editDescription.setText(textFields.get("Description").toString());
                editLicensed.setText(textFields.get("Licensed").toString());
                editNumber.setText(textFields.get("Number").toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void updateSpinners() {

        /**
         * Add code here that updates the values of the spinners based on the values passed
         * from ServiceProvider_HomePage
         *
         * Remove the empty string from here and enter the value of the object as it
         * relates to this value.
         *
         * Should also check and make sure none of the spinner values are null
         */

        //get the service provider ID to be used to access all availability times for this user from the Service
        providerID = fireBaseAuth.getCurrentUser().getUid();

        //makes uid key in table Services
        DatabaseReference Users = databaseUsers.child(providerID);
        DatabaseReference timeRef = Users.child("Services").child(serviceID);


        timeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //this contains the map from the database of the service times
                Map serviceTimes = (Map<String, Object>) dataSnapshot.getValue();

                //loop over all the names in the timeArray and all the values in the spinner array
                for (int i = 0; i < 14; i++) {
                    //refer to the array timeString to understand this code
                    String timeName = timeString[i];
                    //Access the spinner associated with this specific time field
                    Spinner spinner = allSpinners[i];

                    //get the time-field value from the database
                    String timeValue = serviceTimes.get(timeName).toString();
                    //retrieve the position in the array R.id.hour (in strings.xml) where the database
                    //matches the spinner values
                    int spinnerPosition = adapter.getPosition(timeValue);
                    //set the spinner to this value
                    spinner.setSelection(spinnerPosition);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
