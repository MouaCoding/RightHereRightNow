package com.example.rhrn.RightHereRightNow;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.Comments;

import java.util.ArrayList;


/**
 * Created by NatSand on 4/25/17.
 */

public class CommentsListActivity extends FragmentActivity {

    private TextView newComment;
    private ListView commentList;
    private ArrayList<Comments> mComments;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        String postID = getIntent().getStringExtra("postID");
        int CommentCount = getIntent().getIntExtra("numComments", 0);

        if (CommentCount == 0) {
            FragmentManager manager = this.getSupportFragmentManager();
            CreateCommentDialogFragment createComment = new CreateCommentDialogFragment();
            Toast.makeText(getApplicationContext(), postID, Toast.LENGTH_LONG).show();
            createComment.getPostID(postID);
            createComment.getOrder(0);
            createComment.show(manager, "comment");
        }

        else {

        }








    }








}
