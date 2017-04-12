package com.example.rhrn.RightHereRightNow;

import android.content.Intent;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends LoginActivity {

    private EditText    first_name,
                        last_name,
                        display_name,
                        hashtag,
                        user_email,
                        user_password,
                        user_phone,
                        user_address,
                        user_city,
                        user_state,
                        activity_points,
                        numLikes;

    //Button for when user fills in the texts and then clicks on button
    private Button button;
    //create firebase auth object to store into database
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //When user clicks the button, calls function registerUser();
        button = (Button) findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        //Initializes each text view to the class's objects
        first_name = (EditText)findViewById(R.id.first_name);
        last_name = (EditText)findViewById(R.id.last_name);
        display_name = (EditText)findViewById(R.id.display_name);
        hashtag = (EditText)findViewById(R.id.hashtag);
        user_email = (EditText)findViewById(R.id.register_email);
        user_password = (EditText)findViewById(R.id.register_password);
        user_phone = (EditText)findViewById(R.id.register_phone);
        user_address = (EditText)findViewById(R.id.register_address);
        user_city = (EditText)findViewById(R.id.register_city);
        user_state = (EditText) findViewById(R.id.register_state);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    //Function to save the data onto firebase
    private void saveData()
    {
        //Convert all user inputs to string values
        String firstName = first_name.getText().toString().trim();
        String lastName = last_name.getText().toString().trim();
        String displayName = display_name.getText().toString().trim();
        String hashTag = hashtag.getText().toString().trim();
        String fullname = firstName + " " + String.valueOf(lastName);
        //fullname = firstName.concat(String.valueOf(lastName));
        String email = user_email.getText().toString().trim();
        String password = user_password.getText().toString().trim();
        String phone = user_phone.getText().toString().trim();
        String address = user_address.getText().toString().trim();
        String city = user_city.getText().toString().trim();
        String state = user_state.getText().toString().trim();
        String id, uid;

        //Create a root reference of database onto the JSON tree provided by firebase
        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        //TODO: this only saves one user right now, need to implement an efficient algorithm
        // to save many user and have fast access to each one.
            // BB: Can have individual users and then have each post or event correspond to both a user
            // and a different post (in reply) or as its own "base" post
            // Will be able to search for post by userID :)

        //Create a user reference which is a child of the Root, also generates a unique id per user

        //DatabaseReference user = RootRef.child("User").push();
        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
        //id = user.getKey();
        uid = fbuser.getUid();


        //creating user and indexing by Firebase UID

        User usr = new User(firstName, lastName, displayName, hashTag, email, password, phone, address, city, state, "000", uid, 0, 0);
        RootRef.child("User").child(fbuser.getUid()).setValue(usr);


        //Set the user reference to the user's name
        //user.setValue(fullname);

        //store and set the values associated with the user

        //user.setValue(new User(firstName,lastName,displayName,hashTag,email,password,phone,address,city,state,id,uid));//data);


    }

    //Added a register button function
    public void registerUser() //https://www.simplifiedcoding.net/android-firebase-tutorial-1/
    {
        //Init email and password into Strings
        String email = user_email.getText().toString().trim();
        String password = user_password.getText().toString().trim();

        //Once register button is clicked, will display a progress dialog
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //calls firebase auth to create user email and password and store to authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        //Should only save user data to Firebase if successful
                        if(task.isSuccessful()){
                            saveData();
                            Toast.makeText(RegisterActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();
                            //Once successfully registered, changes activity to the login activity
                            Intent goBackToLogin = new Intent (getApplicationContext(), LoginActivity.class);
                            startActivity(goBackToLogin);
                        }else{
                            Toast.makeText(RegisterActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
}
