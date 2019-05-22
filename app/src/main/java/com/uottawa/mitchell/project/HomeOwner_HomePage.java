package com.uottawa.mitchell.project;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.Query;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class HomeOwner_HomePage extends AppCompatActivity {

    String searchField;
    EditText editRating;
    Spinner spinnerSearch, spinnerType, spinnerFrom, spinnerTo, spinnerDay;
    DatabaseReference dbRef;
    ArrayList<String> services;
    DatabaseReference db;
    String fromAvail;
    String toAvail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_owner__home_page);

        //Creates a spinner for displaying the search type
        spinnerSearch = (Spinner) findViewById(R.id.spinnerSearch);
        ArrayAdapter<CharSequence> searchType = ArrayAdapter.createFromResource(this,
                R.array.search_Type, android.R.layout.simple_spinner_item);
        searchType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSearch.setAdapter(searchType);

        //Creates a spinner for selecting the type
        spinnerType = (Spinner) findViewById(R.id.spinnerType);
        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this,
                R.array.services_Array, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);

        db = FirebaseDatabase.getInstance().getReference().child("Users");
        dbRef = FirebaseDatabase.getInstance().getReference().child("Services");
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


        //Instantiate editText field
        editRating = (EditText) findViewById(R.id.editTextRating);

    }

    @Override
    public void onResume() {
        super.onResume();
        services = new ArrayList<>();
    }

    /**
     * Compares individual startTime to endTime to ensure that endTime > startTime
     * @return start time is greater than end time
     */
    public boolean checkTimesNotGreater(Spinner startTime, Spinner endTime) {
        if (! searchField.equals("Time")) {
            return true;
        }
        String startTimeValue = startTime.getSelectedItem().toString();
        String endTimeValue = endTime.getSelectedItem().toString();

        if (startTimeValue.equals("Unavailable") || endTimeValue.equals("Unavailable")) {
            //ensures that if startTime or endTime is unavailable then both must be listed as unavailable
            if ( (!startTimeValue.equals(endTimeValue)) || searchField.equals("Time") ){
                Toast.makeText(this, "Start and end time were not properly entered", Toast.LENGTH_SHORT).show();
                return false;
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
        //check the time spinners to ensure that time have been input properly
       if (! checkTimesNotGreater(spinnerFrom, spinnerTo)) {
           return false;
       }
        return true;
    }

    /**
     * //Aidan (since this function has all the values you're going to get you might
     *      might as well look through all of it)
     * Ensure that all the rating is a double
     * Catches all exceptions
     * @return boolean
     */
    public boolean checkTextEntries() {
        if (searchField.equals("Rating")) {
            try {
                Double value = Double.parseDouble(editRating.getText().toString());

                if (! (value <= 5)) {
                    Toast.makeText(this, "Rating must be less than or equal to 5", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (Exception e) {
                Toast.makeText(this, "Rating is not properly entered", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }



    public void btnSearch(View view) {


        //Aidan
        //check to see which field has been chosen as the search parameter and
        //pass this value in the intent
        //searchFields can be either: "Type of Service" or "Time" or "Rating"
        searchField = spinnerSearch.getSelectedItem().toString();

        //further check to ensure both time fields are input if the searchField is on time
        if ( (!checkInputEntries()) || (!checkTextEntries()) ) {
            return;
        }



        //only pass the values that match the search field
        if (searchField.equals("Type of Service")) {
            //Aidan
            //pass the type of service (referred to as role in the database)
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot service: dataSnapshot.getChildren()) {
                        if (service.child("role").getValue().toString().equals(spinnerType.getSelectedItem().toString())){
                            services.add(service.getKey());
                        }

                    }

                    //Begin a new intent and only pass the values that will be queried
                    Intent intent = new Intent( HomeOwner_HomePage.this, HomeOwner_ServiceList.class);
                    intent.putStringArrayListExtra("ids", services);
                    //starting activity
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (searchField.equals("Time")) {
            //Aidan
            //These strings will have the form: sundayFrom and sundayTo (depends on the day of the week)
            //They exactly match the values in the database
            final String fromTime = spinnerFrom.getSelectedItem().toString().toLowerCase();
            final String toTime = spinnerTo.getSelectedItem().toString().toLowerCase();
            final String fromName = spinnerDay.getSelectedItem().toString().toLowerCase() + "From";
            final String toName = spinnerDay.getSelectedItem().toString().toLowerCase() + "To";
            db.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Maps services. This is done as we need EVERY service, and it's the easiest way I could think of
                            // Not sure if there is a better way
                            for (DataSnapshot clean : dataSnapshot.getChildren()) {
                                for (DataSnapshot swag : clean.child("Services").getChildren()) {
                                        String fromAvail = swag.child(fromName).getValue().toString();
                                        String toAvail = swag.child(toName).getValue().toString();
                                        if ((fromTime.compareTo(fromAvail)) > 0 &&
                                                (toTime.compareTo(toAvail)) < 0) {
                                            services.add(swag.getKey());
                                        }
                                }
                            }

                            //Begin a new intent and only pass the values that will be queried
                            Intent intent = new Intent(HomeOwner_HomePage.this, HomeOwner_ServiceList.class);
                            intent.putStringArrayListExtra("ids", services);
                            intent.putExtra("serviceStartTime", fromAvail);
                            intent.putExtra("serviceEndTime", toAvail);
                            //starting activity
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {


                        }
                    });

        } else if (searchField.equals("Rating")) {

            /**
             * //Aidan
             * Since this has the type double you're going to have to change the value
             * from the database to a double or at least ensure that the value in the database
             * is a double.
             * You could also convert this value to a string but im not sure how well
             * it would work for comparisons
             */

            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot service: dataSnapshot.getChildren()) {
                        Double comparedRating = Double.parseDouble(service.child("rating").getValue().toString());
                        Double rating = Math.round(Double.parseDouble(editRating.getText().toString()) * 10.0) / 10.0;
                        if (comparedRating >= rating ) {
                            services.add(service.getKey());
                        }

                    }

                    //Begin a new intent and only pass the values that will be queried
                    Intent intent = new Intent( HomeOwner_HomePage.this, HomeOwner_ServiceList.class);
                    intent.putStringArrayListExtra("ids", services);
                    //starting activity
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
