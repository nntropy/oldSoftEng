package com.uottawa.mitchell.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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


public class RegisterPage extends AppCompatActivity {

    EditText firstName, lastName, email, password;
    Spinner spinner;

    DatabaseReference databaseUsers;
    ProgressDialog progressDialog;
    FirebaseAuth fireBaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);


        //Creates a spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_Array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        //creates instance of firebase auth for future use
        fireBaseAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        //
        //fireBaseAuth.addAuthStateListener(authListener);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                }
            }
        };

        //creates Progress Dialog to give visual while adding data to database
        progressDialog=new ProgressDialog(this);
        //Create instance variables
        firstName = findViewById(R.id.editFirstName);
        lastName = findViewById(R.id.editLastName);
        email = findViewById(R.id.editEmail);
        password = findViewById(R.id.editPassword);
    }

    /**
     *
     * @param view
     *
     * This method implements the onClick functionality of the button btnCreate
     *
     * This method creates the user account.
     */
    public void btnCreate(View view) {
        if (checkInputEntries()) {
            //Update the database
            addUser(view);
            Toast.makeText (this,"User created",Toast.LENGTH_LONG).show ();
            //Empty all input fields
            firstName.setText("");
            lastName.setText("");
            email.setText("");
            password.setText("");

            //The finish method kills the page too quickly before all information is sent to the database
            //Should still be implemented in future updates
            //finish();
        }
        else {
            Toast.makeText(this, "Please enter info in all fields", Toast.LENGTH_LONG).show();
        }

    }

    boolean isEmpty(EditText text) {
        return text.getText().toString().isEmpty();
    }

    /**
     *
     * This method implements input checking.
     *
     * This method checks all inputs, returning true if inputs are there.
     */
    public boolean checkInputEntries() {
        if (isEmpty(firstName) || isEmpty(lastName)
                || isEmpty(email) || isEmpty(password)) {
            Toast.makeText(this, "You did not enter an input field", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     *
     * @param view
     *
     * This method adds the User to firebase.
     *
     * This method gets input fields from the view, creating the user account as well as making a Node with user information
     * under UID.
     */
    public void addUser(View view) {
        final String fName=firstName.getText ().toString ().trim ();
        final String lName=lastName.getText().toString().trim();
        String email2=email.getText().toString().trim();
        String pass=password.getText ().toString().trim();
        final String role = spinner.getSelectedItem().toString();
        //must also check the uniqueness of all values

        //Creates a visual while sending firebase information
        progressDialog.setMessage("Creating account...");
        progressDialog.show();
        //Adds account to firebase
        fireBaseAuth.createUserWithEmailAndPassword(email2,pass).addOnCompleteListener(RegisterPage.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    //get unique id
                    String uid = fireBaseAuth.getCurrentUser().getUid();
                    //makes uid key in table Users
                    DatabaseReference cUserDB = databaseUsers.child(uid);
                    //adds data to table
                    cUserDB.child("firstName").setValue(fName);
                    cUserDB.child("lastName").setValue(lName);
                    cUserDB.child("role").setValue(role);

                    progressDialog.dismiss();
                } else {
                    Toast.makeText(RegisterPage.this, "Authentication failed:" +
                            task.getException(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        });
    }
    protected void onStart() {
        super.onStart();
        fireBaseAuth.addAuthStateListener(authStateListener);
    }




}
