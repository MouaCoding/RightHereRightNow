package com.example.rhrn.RightHereRightNow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.Comments;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by NatSand on 4/25/17.
 */

public class CommentsListActivity extends FragmentActivity {

    private Button newComment;
    private ImageButton backButton;
    private ListView mListView;
    private ArrayList<Comments> mComments;
    private commentsAdapter mAdapter;
    public App mApp;

    String postID;
    int CommentCount;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        postID = getIntent().getStringExtra("postID");
        CommentCount = getIntent().getIntExtra("numComments", 0);

        if (CommentCount == 0) {
            newComment(postID, false, null);
        }

        else {
            setContentView(R.layout.comment_list);
            mApp = (App)getApplicationContext();
            mComments = new ArrayList<>();
            mListView = (ListView)findViewById(R.id.comment_list_view);
            newComment = (Button) findViewById(R.id.new_comment);
            newComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newComment(postID, false, null);

                }
            });
            backButton = (ImageButton) findViewById(R.id.comment_back_button);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            getComments(postID);
        }


    }



    public void getComments(String postID){
        Toast.makeText(getApplicationContext(), "Got here", Toast.LENGTH_LONG).show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Comments").child(postID);
        ref.orderByChild("timestamp_create");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    Comments comment = commentSnapshot.getValue(Comments.class);
                    mComments.add(comment);

                }

                mAdapter = new commentsAdapter(mComments);
                mListView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });




    }

    public void newComment(String postID, Boolean isReply, String responseID){
        FragmentManager manager = this.getSupportFragmentManager();
        CreateCommentDialogFragment createComment = new CreateCommentDialogFragment();
        Toast.makeText(getApplicationContext(), postID, Toast.LENGTH_LONG).show();
        createComment.getPostID(postID);
        createComment.getResponseID(responseID);

        if(isReply == true){
            createComment.getOrder(1);
        }
        else {
            createComment.getOrder(0);
        }
        createComment.show(manager, "comment");
    }

    public class commentsAdapter extends ArrayAdapter<Comments> {
        commentsAdapter(ArrayList<Comments> commentses){
            super (CommentsListActivity.this, R.layout.comment_post_display, R.id.comment_text, commentses);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            convertView = super.getView(position, convertView, parent);
            final Comments comment = getItem(position);
            TextView likeTextButton = (TextView) convertView.findViewById(R.id.like_text_button);
            TextView replyTextButton = (TextView) convertView.findViewById(R.id.reply_text_button);
            TextView likeCount = (TextView) convertView.findViewById(R.id.comment_like_count);
            TextView replyCount = (TextView) convertView.findViewById(R.id.comment_reply_count);
            TextView commentText = (TextView) convertView.findViewById(R.id.comment_text);
            final TextView displayName = (TextView) convertView.findViewById(R.id.comment_simp_user_name);
            final TextView handle = (TextView) convertView.findViewById(R.id.comment_simp_user_handle);
            final ImageView miniProfilePicture  = (ImageView) convertView.findViewById(R.id.comment_simp_user_image);


            if(comment.isAnon == true){
                displayName.setText("Anonymous");
                handle.setText("");
                miniProfilePicture.setImageResource(R.drawable.happy);
                miniProfilePicture.setClickable(false);
            }
            else {
                User.requestUser(comment.ownerID.toString(), "auth", new User.UserReceivedListener() {
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
            likeCount.setText(String.valueOf(comment.likes));
            replyCount.setText(String.valueOf(comment.replies));


            likeTextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Comments.increment("likes", comment.commentID, comment.responseID);
                }
            });

            replyTextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newComment(postID, true, responseID);

                }
            });

            return convertView;
        }

        //stackoverflow function
        public Bitmap getBitmapFromURL(String src) {
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


}
