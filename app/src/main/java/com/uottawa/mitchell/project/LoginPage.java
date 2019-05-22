package com.uottawa.mitchell.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginPage extends AppCompatActivity {

    EditText email, password;
    String uid, roleString;

    DatabaseReference databaseUsers, role;
    ProgressDialog progressDialog;
    FirebaseAuth fireBaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        progressDialog = new ProgressDialog(this);
        fireBaseAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {

                }
            }
        };

        email = findViewById(R.id.editEmail);
        password = findViewById(R.id.editPassword);
    }

    public void btnLogin(View view) {
        login(view);
        email.setText("");
        password.setText("");
    }

    public void login(View view) {
        String email2=email.getText().toString().trim();
        String pass=password.getText().toString().trim();
        if (checkInputEntries()) {
            progressDialog.setMessage("Logging in...");
            progressDialog.show();
            fireBaseAuth.signInWithEmailAndPassword(email2, pass)
                    .addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(LoginPage.this, "Sign-in failed.", Toast.LENGTH_LONG).show();
                                    } else {
                                        pageRedirect();
                                    }
                                }
                            }
                    );
        }

    }

    public void pageRedirect() {

        uid = fireBaseAuth.getCurrentUser().getUid();
        role = databaseUsers.child(uid).child("role");
        role.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //do what you want with the email
                roleString = dataSnapshot.getValue(String.class);

                //Use this value to consider which page to redirect the user to
                if (roleString.equals("Administrator")) {
                    //Redirect Administrator to the administrator homepage
                    startActivity(new Intent(LoginPage.this, AdminPage.class));
                } else if (roleString.equals("Service Provider")) {
                    //Redirect Service Provider to the service provider homepage
                    startActivity(new Intent(LoginPage.this, ServiceProvider_HomePage.class));
                } else if (roleString.equals("Homeowner")) {
                    startActivity(new Intent(LoginPage.this, HomeOwner_HomePage.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        startActivity(new Intent(LoginPage.this, WelcomePage.class));

    }

    public void btnRegister(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterPage.class);
        startActivity(intent);

    }

    boolean isEmpty(EditText text) {
        return text.getText().toString().isEmpty();
    }

    public boolean checkInputEntries() {
        if (isEmpty(email) || isEmpty(password)) {
            Toast.makeText(this, "You did not enter an input field", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    protected void onStart() {
        super.onStart();
        fireBaseAuth.addAuthStateListener(authStateListener);
    }

}
