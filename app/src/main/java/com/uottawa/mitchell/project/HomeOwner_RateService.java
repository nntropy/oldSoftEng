package com.uottawa.mitchell.project;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeOwner_RateService extends AppCompatActivity {
    EditText desiredRating, inputComment;
    String serviceID;
    DatabaseReference services;
    DatabaseReference serviceIdentity;
    DatabaseReference serviceR;
    DatabaseReference serviceNOR;//NOR=Number Of Ratings
    double serviceRating;
    int serviceNumberOfRatings;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_owner__rate_service);
        Intent intent = getIntent();
        serviceID = intent.getStringExtra("serviceID");
        desiredRating= findViewById(R.id.editRating);
        inputComment = findViewById(R.id.editComment);
        services=FirebaseDatabase.getInstance().getReference("Services");
        serviceIdentity=services.child(serviceID);
        serviceR=serviceIdentity.child ("rating");
        serviceNOR=serviceIdentity.child ("numberOfRatings");
        //retrieving the service Rating from database
        serviceR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serviceRating=dataSnapshot.getValue(Double.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //retrieving number of ratings from database
        serviceNOR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serviceNumberOfRatings=dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }
    public void addRating (View view){
        //checking to see if the input field is empty
        if (isEmpty()){
           Toast.makeText(this,"rating field is empty",Toast.LENGTH_SHORT).show();
        }
        //checks if the input field is a number
        else if ( isNumericDouble(getRatingText()) && checkComment() ){
            double rating= Double.parseDouble(getRatingText());//storing rating because we know it is a number and parsing it to a double
            serviceRating=(((serviceRating*serviceNumberOfRatings)+(rating))/(serviceNumberOfRatings+1));// does new rating calculation
            //round rateValue to one decimal place
            serviceRating = Math.round(serviceRating * 10.0) / 10.0;
            serviceIdentity.child ("rating").setValue(serviceRating);//setting the rating value in data base to new rating
            serviceIdentity.child ("numberOfRatings").setValue(serviceNumberOfRatings+1);//incrementing the numberOfRatings in database
            serviceIdentity.child("comment").setValue(inputComment.getText().toString());
            Toast.makeText(this, "added to database",Toast.LENGTH_SHORT).show ();

        }
        else {
            Toast.makeText(this, "please enter a number",Toast.LENGTH_SHORT).show ();
        }
    }
    //checks that the comment is less than 50 characters
    public boolean checkComment() {
        return inputComment.getText().toString().trim().length() <= 50;
    }
    //checks if the input field is empty
    public boolean isEmpty (){
        return desiredRating.getText().toString().isEmpty();
    }
    //gets String from ratings text field
    public String getRatingText (){
        return desiredRating.getText().toString().trim();
    }
    //check to see if the the String is numeric
    public boolean isNumericDouble(String str) {
        try {
            double d = Double.parseDouble(str);
            if ( !(d <= 5.0) || !(d >= 0)) {
                Toast.makeText(this, "Rating must be less than or equal to 5 (not negative)", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        catch(NumberFormatException nfe){
            return false;
        }
        return true;
    }
}
