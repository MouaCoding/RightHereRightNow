package com.example.rhrn.RightHereRightNow.custom.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
                    likeButton.setColorFilter(R.color.colorTextDark);
                    Toast.makeText(getContext(), "Unliked", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference("Likes").child(EventID).child(currUsr).removeValue();
                    Event.changeCount("likes", EventID, false);
                    getEvent(EventID);

                }
                else{
                    likeButton.setColorFilter(R.color.crimson);
                    Likes.Like(2, EventID, currUsr);
                    Event.changeCount("likes", EventID, true);
                    Toast.makeText(getContext(), "Liked", Toast.LENGTH_SHORT).show();
                    getEvent(EventID);
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
                context.startActivity(intent);

            }
        });
        shareButton = (ImageButton) findViewById(R.id.user_event_share_button);
        shareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: increment shares, implement sharing
            }
        });
    }

    public void getEvent(final String eventID) {
        // TODO fetch event information from params and fill fields
        Event.requestEvent(eventID, "AuthToken", new Event.EventReceivedListener() {
            @Override
            public void onEventReceived(Event... events) {
                Event ev = events[0];
                setEvent(ev);
                EventID = eventID;

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
            eventMiniImageView.setImageBitmap(getBitmapFromURL(ev.ProfilePicture));
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



}
