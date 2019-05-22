package com.uottawa.mitchell.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditServicesActivity extends AppCompatActivity {
    TextView newHourlyRate;
    String newHRate;
    Button delButton;
    Button editButton;
    TextView serviceName;
    TextView serviceRate;
    TextView serviceType;
    String name;
    String type;
    String rate;
    String id;
    DatabaseReference databaseServices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_services);

        //initializes databaseRef
        databaseServices = FirebaseDatabase.getInstance().getReference().child("Services");

        //initializing buttons
        delButton= findViewById(R.id.delButton);
        editButton=findViewById(R.id.editButton);

        //initializing text views that will show info regarding the service selected
        serviceName=findViewById(R.id.serviceName);
        serviceRate=findViewById(R.id.serviceRate);
        serviceType=findViewById(R.id.serviceType);
        newHourlyRate=findViewById(R.id.newRate);

        //making an intent var to get service info passed from AdminServices
        Intent recievedIntent = getIntent();

        //String variables that hold the service that was selected
        name=recievedIntent.getStringExtra ("name");
        type=recievedIntent.getStringExtra ("type");
        rate=recievedIntent.getStringExtra ("rate");
        id=recievedIntent.getStringExtra("id");

        //setting all the text views received from intent
        serviceName.setText(name);
        serviceType.setText(type);
        serviceRate.setText (rate);


    }
    //once user clicks delete button an alert dialog will pop up stating wether they are sure or no. if so than deletes from
    //databse and goes back to AdminService.class
    public void delService (View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(EditServicesActivity.this);
                alert.setCancelable(true);
                alert.setTitle("Delete");
                alert.setMessage ("Are you sure you want to delete the following service ?");
                alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        System.out.println ("holla");
                        Toast.makeText (EditServicesActivity.this,"deletion complete",Toast.LENGTH_SHORT).show ();
                        //removes child from database
                        databaseServices.child(id).removeValue();
                        Intent intent = new Intent (EditServicesActivity.this, AdminServices.class);
                        startActivity(intent);
                    }
                });
                alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText (EditServicesActivity.this,"deletion cancelled",Toast.LENGTH_SHORT).show ();

                    }
                });
                alert.show ();
    }
    public void editService (View view){
        if (newHourlyRate.length()!=0){
            newHRate = newHourlyRate.getText() + " ";
            //edits the node in the database\
            databaseServices.child(id).child("rate").setValue(newHRate.trim());
            serviceRate.setText (newHRate);
            newHourlyRate.setText("");
        }
        else {
            Toast.makeText(this, "field is empty",Toast.LENGTH_SHORT).show ();
        }
    }

}
