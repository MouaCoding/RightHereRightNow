package com.example.rhrn.RightHereRightNow;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.firebaseEntry.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Bradley Wang on 2/13/2017.
 * Edit by Matt on 3/8/2018 --> Queried displayName and updated the profile page name
 */
public class ProfilePageFragment extends Fragment {
    public TextView userName,
                    hash_tag,
                    numberFollowers,
                    numActivityPoints,
                    numLikes;
    //TODO: Add an about me to the user class, I forgot to do it!
    public EditText editAboutMe;

    public User temp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = inflater.inflate(R.layout.profile_page_layout, container, false);

        // restore any state here if necessary

        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();

        userName = (TextView) r.findViewById(R.id.profile_name_main);
        hash_tag = (TextView) r.findViewById(R.id.profile_userhandle);
        numberFollowers= (TextView) r.findViewById(R.id.profile_followers_value);
        numActivityPoints= (TextView) r.findViewById(R.id.profile_activitypoints_value);
        numLikes= (TextView) r.findViewById(R.id.profile_karma_value);
        editAboutMe = (EditText) r.findViewById(R.id.profile_about_text);
        String edit = editAboutMe.getText().toString(); //Update this string to Firebase


        queryFirebase();
        return r;
    }

    public void queryFirebase()
    {
        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference users= FirebaseDatabase.getInstance().getReference("User");
        users.orderByChild("Email").equalTo(fbuser.getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            temp = userSnapshot.getValue(User.class);
                            userName.setText(temp.DisplayName);
                            hash_tag.setText(temp.handle);
                            numberFollowers.setText(Integer.toString(temp.followers.size()));
                            numActivityPoints.setText(Integer.toString(temp.ActivityPoints));
                            numLikes.setText(Integer.toString(temp.LikesReceived));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
    }
}
