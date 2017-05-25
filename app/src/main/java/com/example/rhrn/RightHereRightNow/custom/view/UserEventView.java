package com.example.rhrn.RightHereRightNow.custom.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.rhrn.RightHereRightNow.CommentsListActivity;
import com.example.rhrn.RightHereRightNow.R;
import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.firebase_entry.Likes;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.example.rhrn.RightHereRightNow.util.CircleTransform;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static com.example.rhrn.RightHereRightNow.MapsFragment.getBitmapFromURL;

/**
 * Created by Bradley Wang on 3/6/2017.
 */

public class UserEventView extends FrameLayout {
    private UserMiniHeaderView eventMakerHeader;

    private ImageView eventMiniImageView;
    private TextView eventTitleView;
    private TextView eventStartTimeView;
    private TextView eventEndTimeView;
    private TextView eventLocationView;
    private TextView likesCount;
    private TextView commentsCount;
    private TextView sharesCount;
    int usrLikes;

    private static final int RC = 1;


    private Spinner eventRSVPStateSpinner;

    private String EventID;
    private int CommentCount;
    private String currUsr = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();




    private ImageButton likeButton;
    private ImageButton commentButton;
    private ImageButton shareButton;

    public UserEventView(Context context) {
        super(context);
        createView();
    }

    public UserEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView();
    }

    public void createView() {
        inflate(getContext(), R.layout.user_event_layout, this);

        eventMakerHeader = (UserMiniHeaderView) findViewById(R.id.user_event_mini_head);

        eventMiniImageView = (ImageView) findViewById(R.id.user_event_mini_image);
        eventTitleView = (TextView) findViewById(R.id.user_event_title);
        eventStartTimeView = (TextView) findViewById(R.id.user_event_start_time);
        eventEndTimeView = (TextView) findViewById(R.id.user_event_end_time);
        eventLocationView = (TextView) findViewById(R.id.user_event_location);


        eventRSVPStateSpinner = (Spinner) findViewById(R.id.user_event_rsvp_state_spinner);

        String[] rsvpStates = new String[] {getContext().getString(R.string.event_rsvp_not_going), getContext().getString(R.string.event_rsvp_maybe), getContext().getString(R.string.event_rsvp_going)};
        eventRSVPStateSpinner.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_entry_layout, rsvpStates));
        // TODO create callback for spinner here for updating RSVP state on server side

        likesCount = (TextView) findViewById(R.id.user_event_like_count);
        commentsCount = (TextView) findViewById (R.id.user_event_comment_count);
        sharesCount = (TextView) findViewById(R.id.user_event_share_count);

        likeButton = (ImageButton) findViewById(R.id.user_event_like_button);
        likeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Likes.hasLiked(2, EventID, currUsr )){
                    likeButton.setColorFilter(ContextCompat.getColor(getContext(),R.color.colorTextDark));
                    Toast.makeText(getContext(), "Unliked", Toast.LENGTH_SHORT).show();
                    Event.Unlike(EventID, currUsr);
                    updateCounts(EventID);

                }
                else{
                    likeButton.setColorFilter(ContextCompat.getColor(getContext(),R.color.crimson));
                    Likes.Like(2, EventID, currUsr);
                    Event.changeCount("likes", EventID, true);

                    Toast.makeText(getContext(), "Liked", Toast.LENGTH_SHORT).show();
                    Event.Like(EventID, currUsr);
                    updateCounts(EventID);
                }
            }
        });

        commentButton = (ImageButton) findViewById(R.id.user_event_comment_button);
        commentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                Bundle params = new Bundle();
                Intent intent = new Intent(context, CommentsListActivity.class);
                intent.putExtra("postID", EventID.toString());
                intent.putExtra("type", 2);
                //context.startActivityForResult(intent, RC);
                context.startActivity(intent);
                updateCounts(EventID);

            }
        });
        shareButton = (ImageButton) findViewById(R.id.user_event_share_button);
        shareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Event.Share(EventID, currUsr);
                updateCounts(EventID);
            }
        });
    }

    public void getEvent(final String eventID) {
        // TODO fetch event information from params and fill fields
        Event.requestEvent(eventID, "AuthToken", new Event.EventReceivedListener() {
            @Override
            public void onEventReceived(Event... events) {
                Event ev = events[0];
                try {
                    setEvent(ev);
                    EventID = eventID;
                }catch(Exception e){}

            }
        });
    }

    public void updateCounts(final String eventID){
        Event.requestEvent(eventID, "authToken", new Event.EventReceivedListener() {
            @Override
            public void onEventReceived(Event... events) {
                Event ev = events[0];
                try{
                    likesCount.setText(Integer.toString(ev.likes));
                    commentsCount.setText(Integer.toString(ev.comments));
                    sharesCount.setText(String.valueOf(ev.shares));


                } catch(Exception e){}
            }
        });
    }

    public void setEvent(Event ev){

                eventMakerHeader.getUser(ev.ownerID);
                eventTitleView.setText(ev.eventName);
                eventStartTimeView.setText(ev.startTime);
                eventEndTimeView.setText(ev.endTime);
                eventLocationView.setText(ev.address);
                likesCount.setText(Integer.toString(ev.likes));
                commentsCount.setText(Integer.toString(ev.comments));
                sharesCount.setText(String.valueOf(ev.shares));

                try {
                    if(ev.ProfilePicture != null)
                        Picasso.with(getContext()).load(ev.ProfilePicture).transform(new CircleTransform()).into(eventMiniImageView);
                    else
                        Picasso.with(getContext()).load(R.drawable.images).transform(new CircleTransform()).into(eventMiniImageView);
                }catch (Exception e){}
            }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public void getOwnerLikes(String ownerID){
        FirebaseDatabase.getInstance().getReference().child("User").child(ownerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User postOwner = dataSnapshot.getValue(User.class);
                usrLikes = postOwner.LikesReceived;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
