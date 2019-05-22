package com.uottawa.mitchell.project;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomePage extends AppCompatActivity {

    DatabaseReference databaseUsers;
    FirebaseAuth fireBaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    DatabaseReference firstName, role;
    String uid;

    TextView writeRole, writeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        writeRole = findViewById(R.id.txtRole);
        writeUser = findViewById(R.id.txtWelcome);


        fireBaseAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        authStateListener = new FirebaseAuth.AuthStateListener() {

            /**
             *
             * @param firebaseAuth
             *
             * This method implements the welcome interface on the welcome page.
             *
             * This method, if user is logged in, gets their firstName and Role from firebase and displays a welcome message.
             */
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    uid = fireBaseAuth.getCurrentUser().getUid();
                    firstName = databaseUsers.child(uid).child("firstName");
                    firstName.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String fName = dataSnapshot.getValue(String.class);
                            //do what you want with the email
                            writeUser.setText("Welcome! " + fName);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    role = databaseUsers.child(uid).child("role");
                    role.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String role2 = dataSnapshot.getValue(String.class);
                            //do what you want with the email
                            writeRole.setText("You are logged in as " + role2);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
    }

    protected void onStart() {
        super.onStart();
        fireBaseAuth.addAuthStateListener(authStateListener);
    }

    public void btnLogout(View view) {
        writeUser.setText("");
        writeRole.setText("");

        fireBaseAuth.signOut();
        finish();
    }


}
