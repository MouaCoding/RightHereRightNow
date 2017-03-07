package com.example.rhrn.RightHereRightNow.firebase_entry;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Brian Becker on 2/21/2017.
 */

public class Post {
    public String   ownerID,
                    postID,
                    createDate,
                    createTime,
                    content,
                    responseID;
    //  TODO: BB: Change dates and times to type Date

    public double   viewRadius,
                    latitude,
                    longitude;

    public int      order,  // is it an original post (0), response (1), or response to a response (2)
                    likes,
                    comments;

    public Post() {}

    public Post(String aOwner, String aID, String aCreateDate, String aCreateTime, String aContent,
                String aResponseID, double aViewRadius, double aLat, double aLong,
                int aOrder, int aLikes, int aComments) {

        ownerID     = aOwner;
        postID      = aID;

        createDate  = aCreateDate;
        createTime  = aCreateTime;

        content     = aContent;

        responseID  = aResponseID;
        latitude    = aLat;
        longitude   = aLong;

        viewRadius  = aViewRadius;

        order       = aOrder;
        likes       = aLikes;
        comments    = aComments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setResponseID(String responseID) {
        this.responseID = responseID;
    }

    public void setViewRadius(double viewRadius) {
        this.viewRadius = viewRadius;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
