package com.example.rhrn.RightHereRightNow;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.DialogFragment;

import com.example.rhrn.RightHereRightNow.R;
import com.example.rhrn.RightHereRightNow.firebase_entry.Comments;
import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Comment;


public class CreateCommentDialogFragment extends DialogFragment {

    private EditText commentContent;
    FirebaseAuth firebaseAuth;
    int Order;
    String User;
    String PostID;
    String ResponseID;


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState){
        View r = inflater.inflate(R.layout.comment_post_make, container, false);

        Button postComment = (Button) r.findViewById(R.id.Comment_post_button);
        CheckBox anon = (CheckBox) r.findViewById(R.id.comment_anonymous_check);
        commentContent = (EditText) r.findViewById(R.id.Comment_content);
        firebaseAuth = FirebaseAuth.getInstance();
        //User = firebaseAuth.getCurrentUser().getUid();

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = commentContent.getText().toString();
                createComment(firebaseAuth.getCurrentUser().getUid(), PostID,  temp, Order, ResponseID);
                Event.changeCount("comments", PostID, true);
                dismiss();
            }
        });



        return r;


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

    public void getPostID(String postID) {
        PostID = postID;
    }
    public void getOrder(int order){Order = order;}
    public void getResponseID(String respid){ResponseID = respid;}
}