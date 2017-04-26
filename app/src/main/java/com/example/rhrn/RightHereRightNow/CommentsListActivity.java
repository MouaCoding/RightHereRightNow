package com.example.rhrn.RightHereRightNow;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;


/**
 * Created by NatSand on 4/25/17.
 */

public class CommentsListActivity extends FragmentActivity {


    Button mButton;
    Bundle intent;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String postID = getIntent().getStringExtra("postID");





        FragmentManager manager = this.getSupportFragmentManager();
        CreateCommentDialogFragment createComment = new CreateCommentDialogFragment();
        Toast.makeText(getApplicationContext(), postID, Toast.LENGTH_LONG).show();
        createComment.getPostID(postID);
        createComment.show(manager, "comment");









    }








}
