package com.example.rhrn.RightHereRightNow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.rhrn.RightHereRightNow.ProfilePageFragment.getBitmapFromURL;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Matt on 4/2/2017.
 */

public class TrendingFragment extends Fragment {

    public Button global, city;
    public ImageView profilePicture;
    public TextView eventTitle, eventName, startTime, endTime, numLikes, numComments;
    public TextView displayNameView, userHandleView;
    String otherUserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = (View) inflater.inflate(R.layout.trending_posts, container, false);

        profilePicture = (ImageView) r.findViewById(R.id.user_event_mini_image);
        eventTitle = (TextView) r.findViewById(R.id.user_event_title);
        startTime = (TextView) r.findViewById(R.id.user_event_start_time);
        endTime = (TextView) r.findViewById(R.id.user_event_end_time);
        eventName = (TextView) r.findViewById(R.id.user_event_location);
        numLikes = (TextView) r.findViewById(R.id.number_likes);
        numComments = (TextView) r.findViewById (R.id.number_comments);

        profilePicture = (ImageView) r.findViewById(R.id.mini_profile_picture);
        //Clicking either user's profile picture or their name will start a view user activity
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewUserActivity.class);
                intent.putExtra("otherUserID",otherUserID);
                getContext().startActivity(intent);
            }
        });
        displayNameView = (TextView) r.findViewById(R.id.mini_name);
        displayNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewUserActivity.class);
                intent.putExtra("otherUserID",otherUserID);
                getContext().startActivity(intent);
            }
        });
        userHandleView = (TextView) r.findViewById(R.id.mini_user_handle);


        global = (Button) r.findViewById(R.id.global_button);
        global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display global list of events/posts
            }
        });
        city = (Button) r.findViewById(R.id.city_button);
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display city list of events/posts
            }
        });

        queryAllEvents();
        return r;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void queryAllEvents()
    {
        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Event").orderByChild("likes").startAt(0).endAt(1000).limitToLast(1).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                    Log.d("HEREHEREHEREHEREHAHA", "The " + dataSnapshot.getKey() + " value is " + dataSnapshot.getValue());
                    Event ev = dataSnapshot.getValue(Event.class);

                    eventTitle.setText(ev.eventName);
                    startTime.setText(ev.startTime);
                    endTime.setText(ev.endTime);
                    eventName.setText(ev.address);
                    numLikes.setText(Integer.toString(ev.likes));
                    numComments.setText(Integer.toString(ev.comments));

                    populateEventHeader(ev.ownerID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void populateEventHeader(String uid)
    {
        DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("User");
        user.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User usr = dataSnapshot.getValue(User.class);

                displayNameView.setText(usr.DisplayName);
                userHandleView.setText(usr.handle);
                otherUserID = usr.uid;

                try {
                    profilePicture.setImageBitmap(getBitmapFromURL(usr.ProfilePicture));
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
