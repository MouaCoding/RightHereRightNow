package com.example.rhrn.RightHereRightNow.custom.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.CommentsListActivity;
import com.example.rhrn.RightHereRightNow.R;
import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.firebase_entry.Likes;
import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Bradley Wang on 3/6/2017.
 */

public class UserPostView extends FrameLayout {
    private UserMiniHeaderView postMakerHeader;

    private TextView postBodyTextView;
    private TextView likesCount;
    private TextView commentsCount;
    private TextView sharesCount;

    private String PostID;
    private String currUsr = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

    private ImageButton likeButton;
    private ImageButton commentButton;
    private ImageButton shareButton;

    public UserPostView(Context context) {
        super(context);
        createView();
    }

    public UserPostView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView();
    }

    public void createView() {
        inflate(getContext(), R.layout.user_post_layout, this);

        postMakerHeader = (UserMiniHeaderView) findViewById(R.id.user_post_mini_head);

        postBodyTextView = (TextView) findViewById(R.id.user_post_body);
        likesCount = (TextView) findViewById(R.id.user_post_like_count);
        commentsCount = (TextView) findViewById(R.id.user_post_comment_count);
        sharesCount = (TextView) findViewById(R.id.user_post_share_count);

        likeButton = (ImageButton) findViewById(R.id.user_post_like_button);
        commentButton = (ImageButton) findViewById(R.id.user_post_comment_button);
        shareButton = (ImageButton) findViewById(R.id.user_post_share_button);

        likeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Likes.hasLiked(2, PostID, currUsr )){
                    likeButton.setColorFilter(R.color.colorTextDark);
                    Toast.makeText(getContext(), "Unliked", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference("Likes").child(PostID).child(currUsr).removeValue();
                    Post.changeCount("likes", PostID, false);
                    getPost(PostID);

                }
                else{
                    likeButton.setColorFilter(R.color.crimson);
                    Likes.Like(2, PostID, currUsr);
                    Post.changeCount("likes", PostID, true);
                    Toast.makeText(getContext(), "Liked", Toast.LENGTH_SHORT).show();
                    getPost(PostID);
                }

            }
        });

        commentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                Intent intent = new Intent(context, CommentsListActivity.class);
                intent.putExtra("postID", PostID.toString());
                intent.putExtra("type", 1);
                context.startActivity(intent);
                getPost(PostID);

            }
        });
    }

    public void getPost(final String postID) {
      Post.requestPost(postID, "authToken", new Post.PostReceivedListener() {
          @Override
          public void onPostReceived(Post... posts) {
              Post pst = posts[0];
              setPost(pst);
              PostID = postID;
          }
      });
    }

    public void setPost(Post p) {
        if(p.isAnon == true){
            postMakerHeader.anonUser();
        }
        else {
            postMakerHeader.getUser(p.ownerID);
        }
        postBodyTextView.setText(p.content);
        likesCount.setText(Integer.toString(p.likes));
        commentsCount.setText(Integer.toString(p.comments));
        sharesCount.setText(Integer.toString(p.shares));
        try {
            postMakerHeader.getUser(p.ownerID);
            postBodyTextView.setText(p.content);
        } catch (Exception e) {}
        //
    }
}
