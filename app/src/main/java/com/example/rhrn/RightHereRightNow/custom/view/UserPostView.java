package com.example.rhrn.RightHereRightNow.custom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
<<<<<<< HEAD
=======
import android.widget.Toast;
import android.view.View.OnClickListener;
>>>>>>> likesShares

import com.example.rhrn.RightHereRightNow.CreateCommentFragment;
import com.example.rhrn.RightHereRightNow.R;
import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Bradley Wang on 3/6/2017.
 */

public class UserPostView extends FrameLayout {
    private UserMiniHeaderView postMakerHeader;

    private TextView postBodyTextView;

    private ImageButton likeButton;
    private ImageButton commentButton;
    private ImageButton shareButton;

<<<<<<< HEAD
=======

    private String PostID;
    private String OwnerID;
    private int postLikes;
    private int usrLikes;




>>>>>>> likesShares
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

        likeButton = (ImageButton) findViewById(R.id.user_post_like_button);
        commentButton = (ImageButton) findViewById(R.id.user_post_comment_button);
        shareButton = (ImageButton) findViewById(R.id.user_post_share_button);
<<<<<<< HEAD
=======
        commentButton.setClickable(true);


        likeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                int pstValue = postLikes + 1;
                int usrValue = usrLikes + 1;
                FirebaseDatabase.getInstance().getReference("Post").child(PostID).child("likes").setValue(pstValue);
                FirebaseDatabase.getInstance().getReference("User").child(OwnerID).child("LikesReceived").setValue(usrValue);
                //Toast.makeText(getContext(), "Liked", Toast.LENGTH_SHORT).show();
                //  likeButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.LightGrey));
                numLikes.setText(Integer.toString(pstValue));
                likeButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.crimson));
                likeButton.setClickable(false);
            }
        });

        commentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "comment clicked", Toast.LENGTH_LONG);
            }
        });
>>>>>>> likesShares
    }

    public void getPost(String postID) {
        FirebaseDatabase.getInstance().getReference("Post").child(postID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post p = dataSnapshot.getValue(Post.class);

                setPost(p);

                // eventMiniImageView.setImageBitmap(ev.image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setPost(Post p) {
        postMakerHeader.getUser(p.ownerID);
        postBodyTextView.setText(p.content);
        //
    }
}
