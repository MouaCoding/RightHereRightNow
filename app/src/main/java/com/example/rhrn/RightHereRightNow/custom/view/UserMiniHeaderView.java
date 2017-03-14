package com.example.rhrn.RightHereRightNow.custom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.R;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by Bradley Wang on 3/6/2017.
 */

public class UserMiniHeaderView extends FrameLayout {
    private ImageView miniProfilePicView;
    private TextView displayNameView;
    private TextView userHandleView;
    private ImageButton moreButton;
    private ImageButton addButton;

    public UserMiniHeaderView(Context context) {
        super(context);
        createView();
    }

    public UserMiniHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView();
    }

    public void createView() {
        inflate(getContext(), R.layout.user_mini_header_layout, this);
        miniProfilePicView = (ImageView) findViewById(R.id.mini_profile_picture);
        displayNameView = (TextView) findViewById(R.id.mini_name);
        userHandleView = (TextView) findViewById(R.id.mini_user_handle);
        moreButton = (ImageButton) findViewById(R.id.mini_profile_more_button);
        addButton = (ImageButton) findViewById(R.id.mini_profile_add_button);
    }

    public void getUser(String userID) {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User").child(userID);
        if (ref == null)
            return;
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                displayNameView.setText(user.DisplayName);
                userHandleView.setText(user.handle);
                // eventMiniImageView.setImageBitmap(ev.image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}
        // TODO use params to get user data and fill fields.
