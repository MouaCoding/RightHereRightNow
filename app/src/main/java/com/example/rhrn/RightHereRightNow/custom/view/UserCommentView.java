package com.example.rhrn.RightHereRightNow.custom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.R;
import com.example.rhrn.RightHereRightNow.firebase_entry.Comments;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.google.android.gms.vision.text.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by NatSand on 5/3/17.
 */

public class UserCommentView extends FrameLayout {
    private ImageView miniProfilePicture;
    private TextView displayName,
                     handle,
                     commentText,
                     likeTextButton,
                     likeCount,
                     replyTextButton,
                     replyCount;

    private String CommentID;
    private int commentCount;

    public UserCommentView(Context context){
        super(context);
        createView();
    }

    public UserCommentView(Context context, AttributeSet attrs){
        super(context, attrs);
        createView();
    }

    public void createView(){
        inflate(getContext(), R.layout.comment_post_display, this);
        miniProfilePicture = (ImageView) findViewById(R.id.simp_user_image);
        displayName = (TextView) findViewById(R.id.simp_user_name);
        handle = (TextView) findViewById(R.id.simp_user_handle);
        commentText = (TextView) findViewById(R.id.comment_text);
        likeTextButton = (TextView) findViewById(R.id.like_text_button);
        likeCount = (TextView) findViewById(R.id.comment_like_count);
        replyTextButton = (TextView) findViewById(R.id.reply_text_button);
        replyCount = (TextView) findViewById(R.id.comment_reply_count);

        likeTextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        replyTextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void getComment(final String commentID){
        Comments.requestComment(commentID, new Comments.CommentReceivedListener() {
            @Override
            public void onCommentReceived(Comments... commentses) {
                Comments comment = commentses[0];
                setComment(comment);
                CommentID = commentID;
                commentCount = comment.replies;
            }
        });

    }

    public void setComment(Comments comment){
        if(comment.isAnon == true){
            displayName.setText("Anonymous");
            handle.setText("");
            miniProfilePicture.setImageResource(R.drawable.happy);
            miniProfilePicture.setClickable(false);
        }
        else {
            User.requestUser(comment.ownerID, "auth", new User.UserReceivedListener() {
                @Override
                public void onUserReceived(User... users) {
                    User usr = users[0];
                    displayName.setText(usr.DisplayName);
                    handle.setText(usr.handle);
                    try {
                        //Convert the URL to aa Bitmap using function, then set the profile picture
                        miniProfilePicture.setImageBitmap(getBitmapFromURL(usr.ProfilePicture));
                    }catch (Exception e){}
                }
            });
        }
        commentText.setText(comment.content);
        likeCount.setText(comment.likes);
        replyCount.setText(comment.replies);

    }

    //stackoverflow function
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch( Exception e) {
            return null;
        }
    }

}
