package com.example.rhrn.RightHereRightNow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.R;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Matt on 4/2/2017.
 * Class to view other user profiles
 */

public class ViewUserActivity extends AppCompatActivity {
    public TextView userName,
            hash_tag,
            numberFollowers,
            numActivityPoints,
            numLikes,
            about;
    public ImageView profilePicture;
    public ImageButton backButton;

    //Posts
    public ImageView miniProfilePicture;
    public TextView miniUserName,
            miniHandle,
            body,
            postsNumLikes,
            postsNumShares;

    public FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        userName = (TextView) findViewById(R.id.profile_name_main);
        hash_tag = (TextView) findViewById(R.id.profile_userhandle);
        numberFollowers = (TextView) findViewById(R.id.profile_followers_value);
        numActivityPoints = (TextView) findViewById(R.id.profile_activitypoints_value);
        numLikes = (TextView) findViewById(R.id.profile_karma_value);
        about = (TextView) findViewById(R.id.profile_about_text);
        profilePicture = (ImageView) findViewById(R.id.profile_picture);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Enlarge the profile picture
            }
        });
        backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Posts
        miniProfilePicture = (ImageView) findViewById(R.id.mini_profile_picture);
        miniUserName = (TextView) findViewById(R.id.mini_name);
        miniHandle = (TextView) findViewById(R.id.mini_user_handle);
        body = (TextView) findViewById(R.id.user_post_body);
        postsNumLikes = (TextView) findViewById(R.id.number_likes);
        postsNumShares = (TextView) findViewById(R.id.number_shares);

        Intent intent = getIntent();
        String otherUserID = intent.getStringExtra("otherUserID");
        queryFirebase(otherUserID);
    }

    public void queryFirebase(String userUID)
    {
        FirebaseDatabase.getInstance().getReference("User").child(userUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User temp = dataSnapshot.getValue(User.class);
                        userName.setText(temp.DisplayName);
                        hash_tag.setText(temp.handle);
                        numberFollowers.setText(Integer.toString(temp.followers.size()));
                        numActivityPoints.setText(Integer.toString(temp.ActivityPoints));
                        numLikes.setText(Integer.toString(temp.LikesReceived));
                        about.setText(temp.AboutMe);

                        //TRY because user might not have profile picture yet
                        //TODO: Could use an if loop instead? if(temp.Profilpicture is not null) then set it, otherwise set it to default android
                        try {
                            //Convert the URL to aa Bitmap using function, then set the profile picture
                            profilePicture.setImageBitmap(getBitmapFromURL(temp.ProfilePicture));
                            Log.d("photoURL", temp.ProfilePicture);
                        } catch (Exception e) {}
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }

                });
    }

    //stackoverflow function
    public static Bitmap getBitmapFromURL(String src){
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {return null;}
    }
}
