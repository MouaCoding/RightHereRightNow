package com.example.rhrn.RightHereRightNow;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.rhrn.RightHereRightNow.ProfilePageFragment.getBitmapFromURL;

/**
 * Created by Matt on 4/2/2017.
 */

public class NotificationFragment extends Fragment {

    public Button following, you;
    public EditText search;
    private ArrayList<Post> mPosts;
    private PostAdapter mAdapter;
    private ListView list;
    TextView messageView;
    TextView nameView;
    ImageView profilePic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = (View) inflater.inflate(R.layout.user_notification, container, false);

        following = (Button) r.findViewById(R.id.following_button);
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display following notification
            }
        });
        you = (Button) r.findViewById(R.id.you_button);
        you.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display You notification
            }
        });
        search = (EditText) r.findViewById(R.id.search_friends);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Search friends
            }
        });

        mAdapter = new PostAdapter(getContext(),mPosts);
        mPosts = new ArrayList<Post>();
        list = (ListView) r.findViewById(R.id.global_list);
        messageView = (TextView)r.findViewById(R.id.message_preview);

        //getPosts();
        getUsers();

        return r;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public class PostAdapter extends ArrayAdapter<Post> {
        PostAdapter(Context context, ArrayList<Post> users){
            super(context, R.layout.user_item, R.id.user, users);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
            convertView = super.getView(position, convertView, parent);
            Post post = getItem(position);
            nameView = (TextView)convertView.findViewById(R.id.user);
            messageView = (TextView)convertView.findViewById(R.id.message_preview);
            profilePic = (ImageView) convertView.findViewById(R.id.messaging_profile_picture);

            messageView.setText(post.content);

            try {
                nameView.setText(post.DisplayName);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) nameView.getLayoutParams();
                nameView.setLayoutParams(layoutParams);
                if (post.ProfilePicture != null)
                    profilePic.setImageBitmap(getBitmapFromURL(post.ProfilePicture));

                else
                    profilePic.setImageResource(R.mipmap.ic_launcher);
            }catch (Exception e){}
            return convertView;
        }
    }

    public void getPosts()
    {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Post");
        postRef.limitToLast(10).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post posts = dataSnapshot.getValue(Post.class);
                Log.d("postcreated", posts.createDate);
                try {
                    mPosts.add(0, posts);
                    mAdapter = new PostAdapter(getContext(), mPosts);
                    list.setAdapter(mAdapter);
                }catch (Exception e){}
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


    public void getPosts(String following)
    {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Post");
        postRef.orderByChild("ownerID").equalTo(following).limitToLast(10).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post posts = dataSnapshot.getValue(Post.class);
                Log.d("postcreated", posts.createDate);
                try {
                    mPosts.add(0, posts);
                    mAdapter = new PostAdapter(getContext(), mPosts);
                    list.setAdapter(mAdapter);
                }catch (Exception e){}
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

    public void getUsers()
    {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Following")
                .orderByChild("filler").getRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("SNAP",dataSnapshot.getKey().toString());
                getPosts(dataSnapshot.getKey().toString());
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

