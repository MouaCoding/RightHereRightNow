package com.example.rhrn.RightHereRightNow;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.rhrn.RightHereRightNow.R.id.email;
import static com.example.rhrn.RightHereRightNow.R.id.first_name;
import static com.example.rhrn.RightHereRightNow.R.id.last_name;
import static com.example.rhrn.RightHereRightNow.R.id.register_email;


public class RegisterActivity extends LoginActivity {

    private EditText first_name, last_name, user_email, user_password, user_phone, user_address, user_city, user_state;
    private Button button;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        button = (Button) findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        first_name = (EditText)findViewById(R.id.first_name);
        last_name = (EditText)findViewById(R.id.last_name);
        user_email = (EditText)findViewById(R.id.register_email);
        user_password = (EditText)findViewById(R.id.register_password);
        //etCPassword = (EditText)findViewById(R.id.etCPassword);
        user_phone = (EditText)findViewById(R.id.register_phone);
        user_address = (EditText)findViewById(R.id.register_address);
        user_city = (EditText)findViewById(R.id.register_city);
        user_state = (EditText) findViewById(R.id.register_state);

        //DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        //DatabaseReference userNameRef = RootRef.child("User Name");
        firebaseAuth = FirebaseAuth.getInstance();


        //registerUser();
    }

    private void saveData()
    {
        String firstName = first_name.getText().toString().trim();
        String lastName = last_name.getText().toString().trim();
        String fullname = firstName + " " + String.valueOf(lastName);
        //fullname = firstName.concat(String.valueOf(lastName));
        String email = user_email.getText().toString().trim();
        String password = user_password.getText().toString().trim();
        String phone = user_phone.getText().toString().trim();
        String address = user_address.getText().toString().trim();
        String city = user_city.getText().toString().trim();
        String state = user_state.getText().toString().trim();

        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        //TODO: this only saves one user right now, need to implement an efficient algorithm
        // to save tons of user and have fast access to each one.
        DatabaseReference user = RootRef.child("User");
        /*Map<String, Object> data = new HashMap<String,Object>();
        data.put("Email: ", email);
        data.put("Password: ", password);
        data.put("Phone: ", phone);
        data.put("Address: ", address);
        data.put("City: ", city);
        data.put("State: ", state);*/
        user.setValue(fullname);

        user.child(fullname).setValue(new User(firstName,lastName,email,password,phone,address,city,state));//data);
    }

    //Added a register button function
    public void registerUser() //https://www.simplifiedcoding.net/android-firebase-tutorial-1/
    {
        String email = user_email.getText().toString().trim();
        String password = user_password.getText().toString().trim();
        /*String firstName = first_name.getText().toString().trim();
        String lastName = last_name.getText().toString().trim();
        String fullname = firstName + " " + String.valueOf(lastName);
        //fullname = firstName.concat(String.valueOf(lastName));
        String email = user_email.getText().toString().trim();
        String password = user_password.getText().toString().trim();
        String phone = user_phone.getText().toString().trim();
        String address = user_address.getText().toString().trim();
        String city = user_city.getText().toString().trim();
        String state = user_state.getText().toString().trim();

        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        //TODO: this only saves one user right now, need to implement an efficient algorithm
        // to save tons of user and have fast access to each one.
        DatabaseReference user = RootRef.child("User");
//        user.setValue(fullname);
        Map<String, Object> data = new HashMap<String,Object>();
        data.put("Email: ", email);
        data.put("Password: ", password);
        data.put("Phone: ", phone);
        data.put("Address: ", address);
        data.put("City: ", city);
        data.put("State: ", state);
        user.setValue(fullname);

        user.child(fullname).setValue(new User(firstName,lastName,email,password,phone,address,city,state));//data);
        user.child(firstName).setValue(data);*/


        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        //Should only save user data to Firebase if successful
                        if(task.isSuccessful()){
                            saveData();
                            Toast.makeText(RegisterActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();
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
