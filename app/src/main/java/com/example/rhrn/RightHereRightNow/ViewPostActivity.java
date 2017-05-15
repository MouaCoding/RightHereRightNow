package com.example.rhrn.RightHereRightNow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.R;
import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.example.rhrn.RightHereRightNow.MapsFragment.getBitmapFromURL;


public class ViewPostActivity extends AppCompatActivity {
    TextView content, likes, comments;
    ImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        content = (TextView) findViewById(R.id.content);
        likes = (TextView) findViewById(R.id.number_likes);
        comments = (TextView) findViewById(R.id.number_comments);
        profile = (ImageView) findViewById(R.id.profile_pic);
        String postid = null;
        if(getIntent().getExtras()!=null){
            postid = getIntent().getExtras().getString("postid");
            populate(postid);
        }

    }

    private void populate(final String postid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Post");
        ref.child(postid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                content.setText(post.content);
                likes.setText(Integer.toString(post.likes));
                comments.setText(Integer.toString(post.comments));
                try{
                    if(post.ProfilePicture != null)
                        Picasso.with(getBaseContext()).load(post.ProfilePicture).into(profile);
                    else
                        Picasso.with(getBaseContext()).load(R.drawable.images).into(profile);
                } catch(Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
