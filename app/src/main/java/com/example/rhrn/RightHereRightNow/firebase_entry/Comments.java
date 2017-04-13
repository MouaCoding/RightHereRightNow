package com.example.rhrn.RightHereRightNow.firebase_entry;



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
                    likes;


    public boolean  isAnon;

    public Comments() {}

    public Comments(String aOwner, String aID, String aCreateDate, String aCreateTime, String aContent,
                String aResponseID, int aOrder, int aLikes, boolean Anon) {

        ownerID     = aOwner;
        commentID      = aID;

        createDate  = aCreateDate;
        createTime  = aCreateTime;

        content     = aContent;

        responseID  = aResponseID;


        order       = aOrder;
        likes       = aLikes;

        isAnon      = Anon;
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
