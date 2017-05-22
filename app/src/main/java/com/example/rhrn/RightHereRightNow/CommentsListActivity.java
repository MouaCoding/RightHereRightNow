package com.example.rhrn.RightHereRightNow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.Comments;
import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.firebase.client.ServerValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
    private Button postButton;
    private CheckBox anon;
    private EditText content;
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



            setContentView(R.layout.comment_list);


            mApp = (App)getApplicationContext();
            mComments = new ArrayList<>();
            mListView = (ListView)findViewById(R.id.comment_list_view);
            mAdapter = new commentsAdapter(getBaseContext(),mComments);
            mListView.setAdapter(mAdapter);
            anon = (CheckBox) findViewById(R.id.comment_anonymous_check);
            content = (EditText) findViewById(R.id.Comment_content);

            postButton = (Button) findViewById(R.id.Comment_post_button);
            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.clear();
                    String temp = content.getText().toString();
                    createComment(FirebaseAuth.getInstance().getCurrentUser().getUid(), postID,  temp, 0, null);
                    Event.changeCount("comments", postID, true);
                    getComments(postID, true);
                }
            });
            backButton = (ImageButton) findViewById(R.id.comment_back_button);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            getComments(postID, true);
        }






    public void getComments(String postID, boolean first){
        Toast.makeText(getApplicationContext(), "Got here", Toast.LENGTH_LONG).show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Comments").child(postID);
        ref.orderByChild("timestamp_create");
        if(first) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                        Comments comment = commentSnapshot.getValue(Comments.class);
                        mComments.add(comment);
                        mAdapter.notifyDataSetChanged();

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }


            });

        }
        else{
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Comments comment = dataSnapshot.getValue(Comments.class);
                    mAdapter.add(comment);

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

    public void createComment(String userID, String postID, String Content, int Order, String responseID) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference rootreference = FirebaseDatabase.getInstance().getReference("Comments").child(postID);
        DatabaseReference reference;
        if(Order == 0){
            reference = rootRef.child("comment").push();
        }
        else{
            reference = rootRef.child("comment").child(postID).child(responseID).push();
        }
        String key = reference.getKey();

        DatabaseReference createdComment = FirebaseDatabase.getInstance().getReference("Comments").child(postID).child(key);
        createdComment.setValue(new Comments(userID, key, Content, postID, Order, 0, 0, false, ServerValue.TIMESTAMP));
        createdComment.child("timestamp_create").setValue(ServerValue.TIMESTAMP);



    }

    public static class commentsAdapter extends ArrayAdapter<Comments> {
        commentsAdapter(Context context, ArrayList<Comments> commentses){
            super (context, R.layout.comment_post_display, R.id.comment_text, commentses);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            convertView = super.getView(position, convertView, parent);
            final Comments comment = getItem(position);
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
