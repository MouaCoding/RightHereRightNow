package com.example.rhrn.RightHereRightNow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlmostDoneActivity extends AppCompatActivity {

    private EditText display_name, handle, user_phone;
    String displayNAME,handle1,phone;

    //Button for when user fills in the texts and then clicks on button
    private Button button;
    //create firebase auth object to store into database
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_almost_done);
        //When user clicks the button, calls function registerUser();
        button = (Button) findViewById(R.id.done_profile);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        //Initializes each text view to the class's objects
        display_name = (EditText)findViewById(R.id.displayName);
        handle = (EditText)findViewById(R.id.handle1);
        user_phone = (EditText)findViewById(R.id.phone_number);

        firebaseAuth = FirebaseAuth.getInstance();


    }

    //Function to save the data onto firebase
    private void saveData()
    {
        displayNAME = display_name.getText().toString().trim();
        handle1 = handle.getText().toString().trim();
        phone = user_phone.getText().toString().trim();

        //Create a root reference of database onto the JSON tree provided by firebase
        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();

        //DatabaseReference user = RootRef.child("User").push();
        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
        //id = user.getKey();
        String uid = fbuser.getUid();
        String firstName = getIntent().getStringExtra("first_name");
        String lastName = getIntent().getStringExtra("last_name");
        String ProfilePicture = getIntent().getStringExtra("profile_picture");

        final User usr = new User(firstName,lastName,displayNAME,handle1,null,null,phone,null,null,null,"000",uid,0,0);


        //creating user and indexing by Firebase UID
        DatabaseReference userRef = RootRef.child("User").child(uid);
        userRef.setValue(usr);
        userRef.child("ProfilePicture").setValue(ProfilePicture);
        /*userRef.child("DisplayName").setValue(displayNAME);
        userRef.child("handle").setValue(handle1);
        userRef.child("Phone").setValue(phone);*/

        Toast.makeText(getApplicationContext(), "You're all set! Enjoy.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();

    }



}
