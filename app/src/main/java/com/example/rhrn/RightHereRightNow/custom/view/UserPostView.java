package com.example.rhrn.RightHereRightNow.custom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.R;
import com.example.rhrn.RightHereRightNow.firebase_entry.Post;

/**
 * Created by Bradley Wang on 3/6/2017.
 */

public class UserPostView extends FrameLayout {
    private UserMiniHeaderView postMakerHeader;

    private TextView postBodyTextView;
    private TextView likesCount;
    private TextView commentsCount;
    private TextView sharesCount;


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





    }

    public void getPost(final String postID) {
      Post.requestPost(postID, "authToken", new Post.PostReceivedListener() {
          @Override
          public void onPostReceived(Post... posts) {
              Post pst = posts[0];
              setPost(pst);
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
        //
    }
}
