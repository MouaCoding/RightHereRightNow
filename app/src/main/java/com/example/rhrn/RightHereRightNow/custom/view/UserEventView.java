package com.example.rhrn.RightHereRightNow.custom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.R;
import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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


    private Spinner eventRSVPStateSpinner;

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

        likeButton = (ImageButton) findViewById(R.id.user_event_like_button);
        likeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            //TODO: increment likes for event
            }
        });

        commentButton = (ImageButton) findViewById(R.id.user_event_comment_button);
        commentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: implement comment on click
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

    public void getEvent(String eventID) {
        // TODO fetch event information from params and fill fields
        FirebaseDatabase.getInstance().getReference("Event").child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event ev = dataSnapshot.getValue(Event.class);

                eventMakerHeader.getUser(ev.ownerID);
                eventTitleView.setText(ev.eventName);
                eventStartTimeView.setText(ev.startTime);
                eventEndTimeView.setText(ev.endTime);
                eventLocationView.setText(ev.address);

                try {
                     eventMiniImageView.setImageBitmap(getBitmapFromURL(ev.ProfilePicture));
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
