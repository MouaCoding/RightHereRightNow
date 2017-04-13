package com.example.rhrn.RightHereRightNow;

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

import com.example.rhrn.RightHereRightNow.R;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;


public class CreateCommentFragment extends Fragment {

    private EditText commentContent;
    FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState){
        View r = inflater.inflate(R.layout.comment_post_make, container, false);

        Button postComment = (Button) r.findViewById(R.id.Comment_post_button);
        CheckBox anon = (CheckBox) r.findViewById(R.id.comment_anonymous_check);
        commentContent = (EditText) r.findViewById(R.id.Comment_content);
        firebaseAuth = FirebaseAuth.getInstance();


        return r;
    }


}