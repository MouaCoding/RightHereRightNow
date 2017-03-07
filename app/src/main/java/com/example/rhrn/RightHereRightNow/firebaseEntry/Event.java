package com.example.rhrn.RightHereRightNow.firebaseEntry;

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
                    description;
    //  TODO: BB: Change dates and times to type Date
    // BB: we also might need some sort of unique event ID

    public double   latitude,
                    longitude,
                    viewRadius;

    public int      likes,
                    comments,
                    rsvp;

    public Event() {}

    public Event(String aName, String aOwner, String aStartDate, String aEndDate, String aStartTime,
                 String aEndTime, String aAddress, String aDescription, double aLat, double aLong,
                 double aViewRadius, int aLikes, int aComments, int aRSVPs) {
        eventName   = aName;
        ownerID     = aOwner;

        startDate   = aStartDate;
        endDate     = aEndDate;
        startTime   = aStartTime;
        endTime     = aEndTime;

        address     = aAddress;
        description = aDescription;

        latitude    = aLat;
        longitude   = aLong;

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

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
}
