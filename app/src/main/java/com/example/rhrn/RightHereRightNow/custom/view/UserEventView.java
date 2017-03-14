package com.example.rhrn.RightHereRightNow.custom.view;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
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
        commentButton = (ImageButton) findViewById(R.id.user_event_comment_button);
        shareButton = (ImageButton) findViewById(R.id.user_event_share_button);
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


                // eventMiniImageView.setImageBitmap(ev.image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
