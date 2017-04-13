package com.example.rhrn.RightHereRightNow.firebase_entry;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Brian Becker on 2/20/2017.
 */

public class Event {

    public String   eventName,
                    ownerID,
                    startDate,
                    endDate,
                    startTime,
                    endTime,
                    address,
                    description,
                    ProfilePicture;
    //  TODO: BB: Change dates and times to type Date
    // BB: we also might need some sort of unique event ID

    public double   viewRadius;

    public int      likes,
                    comments,
                    rsvp;

    public Event() {}

    public Event(String aName, String aOwner, String aStartDate, String aEndDate, String aStartTime,
                 String aEndTime, String aAddress, String aDescription,
                 double aViewRadius, int aLikes, int aComments, int aRSVPs) {
        eventName   = aName;
        ownerID     = aOwner;

        startDate   = aStartDate;
        endDate     = aEndDate;
        startTime   = aStartTime;
        endTime     = aEndTime;

        address     = aAddress;
        description = aDescription;

        viewRadius  = aViewRadius;

        likes       = aLikes;
        comments    = aComments;
        rsvp        = aRSVPs;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setViewRadius(double viewRadius) {
        this.viewRadius = viewRadius;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public void setRsvp(int rsvp) {
        this.rsvp = rsvp;
    }

    public static void requestEvent(String eventID, String authToken, final EventReceivedListener listener) {
        if (listener == null) return;
        FirebaseDatabase.getInstance().getReference("Event").child(eventID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Event ev = dataSnapshot.getValue(Event.class);
                        listener.onEventReceived(ev);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onEventReceived(null);
                    }
                });
    }

    public static interface EventReceivedListener {
        public void onEventReceived(Event... events);
    }
}
