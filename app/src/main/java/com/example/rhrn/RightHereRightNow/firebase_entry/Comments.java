package com.example.rhrn.RightHereRightNow.firebase_entry;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Natsand on 4/11/2017.
 */

public class Comments {
    public String   ownerID,
            commentID,
            createDate,
            createTime,
            content,
            responseID;
    //  TODO: NS: Change dates and times to type Date


    public int      order,  // is it an original post (0), response (1), or response to a response (2)
                    likes,
                    replies;

    public boolean  isAnon;

    public Comments() {}

    public Comments(String aOwner, String aID, String aContent,
                String aResponseID, int aOrder, int aLikes, int aReplies, boolean Anon) {

        ownerID     = aOwner;
        commentID      = aID;

        content     = aContent;

        responseID  = aResponseID;


        order       = aOrder;
        likes       = aLikes;
        replies     = aReplies;

        isAnon      = Anon;
    }

    public static void requestComment(String commentID, final CommentReceivedListener listener){
        if(listener == null)
            return;
        FirebaseDatabase.getInstance().getReference("Comments").child(commentID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Comments comment = dataSnapshot.getValue(Comments.class);
                        listener.onCommentReceived(comment);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onCommentReceived(null);
                    }
                });
    }

    public static interface CommentReceivedListener {
        public void onCommentReceived(Comments...commentses);
    }
    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public void setCommentID(String CommentID) {
        this.commentID = CommentID;
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

    public void setOrder(int order) {
        this.order = order;
    }
}
