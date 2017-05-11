package com.example.rhrn.RightHereRightNow.firebase_entry;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Brian Becker on 2/21/2017.
 */

public class Post {
    public String   ownerID,
                    postID,
                    createDate,
                    createTime,
                    content,
                    responseID,
                    DisplayName,
                    ProfilePicture;
    //  TODO: BB: Change dates and times to type Date

    public double   viewRadius;

    public int      //order,  // is it an original post (0), response (1), or response to a response (2)
                    likes,
                    shares,
                    comments;

    public boolean  isAnon;

    public Post() {}

    public Post(String aOwner, String aID, String aCreateDate, String aCreateTime, String aContent,
                String aResponseID, double aViewRadius, int aShares, int aLikes, int aComments, boolean Anon) {

        ownerID     = aOwner;
        postID      = aID;

        createDate  = aCreateDate;
        createTime  = aCreateTime;

        content     = aContent;

        responseID  = aResponseID;

        viewRadius  = aViewRadius;

        shares       = aShares;
        likes       = aLikes;
        comments    = aComments;
        isAnon      = Anon;
    }

    public static void requestPost(String PostID, String authToken, final Post.PostReceivedListener listener) {
        if (listener == null) return;
        FirebaseDatabase.getInstance().getReference("Post").child(PostID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Post pst  = dataSnapshot.getValue(Post.class);
                        listener.onPostReceived(pst);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onPostReceived();
                    }
                });
    }

    public static interface PostReceivedListener {
        public void onPostReceived(Post... posts);
    }

    public static void changeCount(String type, String postID, final boolean inc) {

        FirebaseDatabase.getInstance().getReference("Post").child(postID).child(type).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if(mutableData.getValue() == null){
                    mutableData.setValue(0);

                }
                else {

                    int count = mutableData.getValue(Integer.class);
                    if(inc){
                        mutableData.setValue(count + 1);
                    }
                    else{
                        mutableData.setValue(count - 1);
                    }


                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }

        });

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

    public void setContent(String content) { this.content = content; }

    public void setResponseID(String responseID) {
        this.responseID = responseID;
    }

    public void setViewRadius(double viewRadius) {
        this.viewRadius = viewRadius;
    }

    public void setShares(int share) {
        this.shares = share;
    }
}
