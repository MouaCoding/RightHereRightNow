package com.example.rhrn.RightHereRightNow;

import java.util.Date;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Brian Becker on 2/20/2017.
 */

public class Event {
    private String  EventName,
                    OwnerID,
                    StartDate,
                    EndDate,
                    StartTime,
                    EndTime,
                    Address,
                    Description;
    //  TODO: BB: Change dates and times to type Date
    // BB: we also might need some sort of unique event ID

    private LatLng  Coordinates;

    private double  ViewRadius;

    private int     Likes,
                    Comments,
                    RSVPs;

    public Event(String aName, String aOwner, String aStartDate, String aEndDate, String aStartTime,
                 String aEndTime, String aAddress, String aDescription, LatLng aCoordinate,
                 double aViewRadius, int aLikes, int aComments, int aRSVPs) {
        EventName   = aName;
        OwnerID     = aOwner;

        StartDate   = aStartDate;
        EndDate     = aEndDate;
        StartTime   = aStartTime;
        EndTime     = aEndTime;

        Address     = aAddress;
        Description = aDescription;

        Coordinates = aCoordinate;

        ViewRadius  = aViewRadius;

        Likes       = aLikes;
        Comments    = aComments;
        RSVPs       = aRSVPs;
    }

    public String getEventName() {
        return EventName;
    }

    public String getAddress() {
        return Address;
    }

    public String getDescription() {
        return Description;
    }

    public LatLng getCoordinates() {
        return Coordinates;
    }

    public double getViewRadius() {
        return ViewRadius;
    }
    public void setViewRadius(double updatedRadius) {
        ViewRadius = updatedRadius;
    }

    public int getLikes() {
        return Likes;
    }

    public int getComments() {
        return Comments;
    }

    public int getRSVPs() {
        return RSVPs;
    }
}
