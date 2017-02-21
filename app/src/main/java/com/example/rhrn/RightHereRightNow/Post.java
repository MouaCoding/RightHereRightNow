package com.example.rhrn.RightHereRightNow;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Brian Becker on 2/21/2017.
 */

public class Post {
    private String  OwnerID,
                    PostID,
                    CreateDate,
                    CreateTime,
                    Content,
                    ResponseID;
    //  TODO: BB: Change dates and times to type Date

    private LatLng  Coordinates;

    private double  ViewRadius;

    private int     Order,
                    Likes,
                    Comments;

    // is it an original post (0), response (1), or response to a response (2)

    public Post(String aOwner, String aID, String aCreateDate, String aCreateTime, String aContent,
                String aResponseID, LatLng aCoordinate, double aViewRadius, int aOrder, int aLikes,
                int aComments) {

        OwnerID     = aOwner;
        PostID      = aID;

        CreateDate  = aCreateDate;
        CreateTime  = aCreateTime;

        Content     = aContent;

        ResponseID  = aResponseID;
        Coordinates = aCoordinate;

        ViewRadius  = aViewRadius;

        Order       = aOrder;
        Likes       = aLikes;
        Comments    = aComments;
    }

    public String getOwnerID() {
        return OwnerID;
    }

    public String getPostID() {
        return PostID;
    }

    public String getContent() {
        return Content;
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

    public int getOrder() {
        return Order;
    }

    public int getLikes() {
        return Likes;
    }

    public int getComments() {
        return Comments;
    }
}
