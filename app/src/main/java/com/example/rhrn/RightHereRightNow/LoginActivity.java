
package com.example.rhrn.RightHereRightNow;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity   {
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int LOCATIONS_PERMISSION = 0;

    // UI references.
    public AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    //Firebase authentication object
    private FirebaseAuth firebaseAuth;

    //process dialog to display to user
    public ProgressDialog progressDialog;

    //Signup button
    private Button buttonSignup;
    private CallbackManager callbackManager;
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        //Setup a Firebase object
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        //Sets the buttonSignup button to the register button on the xml layout
        buttonSignup = (Button) findViewById(R.id.register_button);

        //set email object to user's text input
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        //Set password
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    //If login matches
                    signIn();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //If register button is clicked, switches activity to RegisterActivity
        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATIONS_PERMISSION);
        }
        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ForgotPasswordActivity.class);
                startActivity(intent);

            }
        });

    }

    //Facebook register user
    private void facebookRegister()
    {
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                signIn();
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException e) {}
        });
    }

    //added a sign in function
    private void signIn()
    {
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        //if empty email, display error
        if (TextUtils.isEmpty(email)) {
            //get the string from strings.xml and display it
            mEmailView.setError(getString(R.string.error_field_required));
            return;
        } else if (!isEmailValid(email)) {
            //if not empty then it's wrong data
            mEmailView.setError(getString(R.string.error_invalid_email));
        }

        //if password is empty
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            return;
        } else if (!isPasswordValid(password)) {
            //if password is incorrect
            mPasswordView.setError(getString(R.string.error_invalid_password));
        }

        //Displays the process display of logging in
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        //Authenticate with firebase backend
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && emailVerified()) {
                            //if successfully logs in, displays success,
                            Toast.makeText(LoginActivity.this,"Successfully Logged In",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            //if failed, display error message
                            Toast.makeText(LoginActivity.this,"Login Error",Toast.LENGTH_LONG).show();
                        }
                        //once done, process dialog disappears
                        progressDialog.dismiss();
                    }
                });
    }

    public boolean emailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.isEmailVerified())
        {
            return true;
        }
        else
        {
            //logout user and return false
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(LoginActivity.this,"Email not Verified!",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: MM: send email verfication?
        return (email.contains("@"));
    }

    private boolean isPasswordValid(String password) {
        //TODO: MM: Password implementation? Right Now->If not empty (No restriction on password)
        return (password.length() > 0);
    }

}

