package com.example.rhrn.RightHereRightNow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.R;
import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.rhrn.RightHereRightNow.MapsFragment.getBitmapFromURL;

public class ViewEventActivity extends AppCompatActivity {
    TextView content, likes, comments, title;
    ImageView profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        title = (TextView) findViewById(R.id.view_event_title);
        content = (TextView) findViewById(R.id.view_event_content);
        likes = (TextView) findViewById(R.id.view_num_likes);
        comments = (TextView) findViewById(R.id.view_num_comments);
        profile = (ImageView) findViewById(R.id.view_event_profile_pic);
        String eventID = null;
        if(getIntent().getExtras()!=null){
            eventID = getIntent().getExtras().getString("eventid");
            populate(eventID);
        }
    }

    private void populate(final String eventid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Event");
        ref.child(eventid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                title.setText(event.eventName);
                likes.setText(Integer.toString(event.likes));
                comments.setText(Integer.toString(event.comments));
                try{
                    content.setText(event.description);
                    if(event.ProfilePicture != null)
                        profile.setImageBitmap(getBitmapFromURL(event.ProfilePicture));
                    else
                        profile.setImageResource(R.drawable.ic_recent_actors_black_24dp);
                } catch(Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
