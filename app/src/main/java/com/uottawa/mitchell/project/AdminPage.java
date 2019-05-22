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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.*;
import android.app.ProgressDialog;

/**
 * Purpose:
 * This page is the main Administrator page upon logging into the system.
 * From this page Administrators are capable of creating new Services and will be redirected
 * to a page from which they may edit and delete services.
 *
 * Restrictions:
 * ONLY Administrators shall be directed towards this page.
 * All fields must be input by the user.
 * (There are no restrictions on duplication of values in the database)
 *
 * Global variables:
 * spinnerServices - represents the drop down menu accessible by the user to select the type of service they wish to provide.
 * serviceName - represents the text input field for the service name.
 * serviceRate - represents the text input field for the service rate. (IMPORTANT: this value is stored as a double in the database)
 *
 */
public class AdminPage extends AppCompatActivity {

    // Declaration of global variables
    Spinner spinnerServices;
    EditText serviceName, serviceRate;
    DatabaseReference databaseServices;
    ProgressDialog progressDialog;

    //Please don't delete. Will be used in future -Aidan
    //FirebaseAuth fireBaseAuth;
    //FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        //Initiate the progress dialog
        progressDialog = new ProgressDialog(this);
        //Initialize global variables and a database reference
        serviceName = findViewById(R.id.editService);
        serviceRate = findViewById(R.id.editHourly);
        databaseServices = FirebaseDatabase.getInstance().getReference().child("Services");


        //Creates a spinner
        spinnerServices = (Spinner) findViewById(R.id.spinnerServices);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.services_Array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerServices.setAdapter(adapter);
    }

    /**
     * @param text
     *
     * This method takes the value of a text input field and checks if it is empty.
     *
     * @return boolean
     */
    boolean isEmpty(EditText text) {
        return text.getText().toString().isEmpty();
    }

    /**
     * This method checks text input fields serviceName and serviceRate to ensure all fields are input.
     * Also checks to ensure that the rate is a double of type $$.$$
     *
     * @return boolean
     */
    public boolean checkInputEntries() {

        // Ensures that all fields have been input
        if (isEmpty(serviceName) || isEmpty(serviceRate)) {
            Toast.makeText(this, "You did not enter an input field", Toast.LENGTH_SHORT).show();
            return false;
        }


        // This try-catch block attempts to retrieve the decimal value of the rate input
        // If this is not possible the catch block prompts the user to input a decimal value
        try{
            double rateValue = Double.parseDouble(serviceRate.getText ().toString ().trim ());

            //ensure that value is not negative
            if (rateValue <= 0.0) {
                Toast.makeText(this, "Service rate must be a positive value", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Service rate must be of the form $$.$$", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * @param view
     *
     * This method implements the onClick functionality of the button btnRegister
     *
     * This method calls the method checkInputEntries to ensure all values have been input properly.
     * If they have been then the the addService method is called to add all values to the database.
     * Finally, all text input fields are cleared.
     */
    public void btnRegister(View view) {
        if (checkInputEntries()) {

            //Update the database
            addService(view);
            Toast.makeText (this,"User created ",Toast.LENGTH_LONG).show ();

            //Empty all input fields
            serviceName.setText("");
            serviceRate.setText("");
        }
    }

    /**
     * @param view
     *
     * This method adds the values input by the user (name, rate, role) to the database
     * under a unique entry containing all fields.
     */
    public void addService(View view) {
        //Add values to the database
        //NOTE: sanitize inputs before calling this, as there are no listeners for firebase errors

        //rate is already ensured to be of type Double
        String name = serviceName.getText ().toString ().trim ();
        Double rate = Double.parseDouble(serviceRate.getText ().toString ().trim ());
        String role = spinnerServices.getSelectedItem().toString();

        //Gets key
        String id = databaseServices.push().getKey();

        //Creates a visual while sending firebase information
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        //makes uid key in table Services
        DatabaseReference cServices = databaseServices.child(id);

        //adds data to table
        cServices.child("name").setValue(name);
        cServices.child("role").setValue(role);
        cServices.child("rate").setValue(rate);
        cServices.child("rating").setValue(0.0);
        cServices.child("numberOfRatings").setValue(0);

        //send success message
        progressDialog.dismiss();
        Toast.makeText(this,"Service added",Toast.LENGTH_SHORT).show ();
    }

    /**
     *
     * @param view
     *
     * This method implements the onClick functionality of the button btnEdit
     *
     * This method redirects the user to the page AdminServices allowing them to add and delete Services.
     */
    public void btnEditServices(View view) {
        Intent intent = new Intent(this, AdminServices.class);
        startActivity(intent);
    }

}