package com.example.rhrn.RightHereRightNow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.custom.view.UserMiniHeaderView;
import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.example.rhrn.RightHereRightNow.MapsFragment.getBitmapFromURL;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Matt on 4/2/2017.
 */

public class NotificationFragment extends Fragment {

    public Button following, you;
    public EditText search;
    private ArrayList<Post> mPosts;
    private ArrayList<Object> mUserNotifications;
    private PostAdapter mAdapter;
    private ListView list, userList;
    int notificationFlag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = (View) inflater.inflate(R.layout.user_notification, container, false);

        following = (Button) r.findViewById(R.id.following_button);
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosts = new ArrayList<>();
                mAdapter = new PostAdapter(getContext(), mPosts);
                list.setAdapter(mAdapter);
                getPostsNotifications();//getUsers();
            }
        });
        you = (Button) r.findViewById(R.id.you_button);
        you.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosts = new ArrayList<>();
                mAdapter = new PostAdapter(getContext(), mPosts);
                list.setAdapter(mAdapter);
                userNotification();
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
        mUserNotifications = new ArrayList<>();
        list = (ListView) r.findViewById(R.id.global_list);
        userList = (ListView) r.findViewById(R.id.global_list);

        //getPosts();
        getUsersFollowed();
        getPostsNotifications();

        return r;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static class PostAdapter extends ArrayAdapter<Post> {
        PostAdapter(Context context, ArrayList<Post> users){
            super(context, R.layout.user_post_framed_layout/*user_item*/, R.id.mini_name, users);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            final Post post = getItem(position);

            TextView postBodyTextView = (TextView) convertView.findViewById(R.id.user_post_body);
            ImageButton likeButton = (ImageButton) convertView.findViewById(R.id.user_post_like_button);
            ImageButton commentButton = (ImageButton) convertView.findViewById(R.id.user_post_comment_button);
            ImageButton shareButton = (ImageButton) convertView.findViewById(R.id.user_post_share_button);
            ImageView miniProfilePicView = (ImageView) convertView.findViewById(R.id.mini_profile_picture);;
            TextView displayNameView= (TextView) convertView.findViewById(R.id.mini_name);
            TextView userHandleView= (TextView) convertView.findViewById(R.id.mini_user_handle);
            TextView numLikes = (TextView) convertView.findViewById(R.id.number_likes);
            TextView numComments = (TextView) convertView.findViewById(R.id.number_comments);

            displayNameView.setText(post.DisplayName);
            userHandleView.setText(post.handle);
            postBodyTextView.setText(post.content);
            numLikes.setText(Integer.toString(post.likes));
            numComments.setText(Integer.toString(post.comments));
            try {
                if (post.ProfilePicture != null)
                    Picasso.with(getContext()).load(post.ProfilePicture).into(miniProfilePicView);
                    //miniProfilePicView.setImageBitmap(getBitmapFromURL(post.ProfilePicture));
                else
                    Picasso.with(getContext()).load(R.mipmap.ic_launcher).into(miniProfilePicView);
                    //miniProfilePicView.setImageResource(R.mipmap.ic_launcher);
            } catch(Exception e){}
            displayNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(),ViewPostActivity.class);
                    intent.putExtra("postid",post.postID);
                    getContext().startActivity(intent);
                }
            });
            miniProfilePicView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(),ViewPostActivity.class);
                    intent.putExtra("postid",post.postID);
                    getContext().startActivity(intent);
                }
            });

            /*
            TextView nameView = (TextView)convertView.findViewById(R.id.user);
            TextView messageView = (TextView)convertView.findViewById(R.id.message_preview);
            ImageView profilePic = (ImageView) convertView.findViewById(R.id.messaging_profile_picture);

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
            */

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
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}


        });

    }

    public void getPosts(String following)
    {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Post");
        postRef.orderByChild("ownerID").equalTo(following).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post posts = dataSnapshot.getValue(Post.class);
                //TODO: since this saves all posts by each user, it might be inefficient... MM
                DatabaseReference notifyRequest = FirebaseDatabase.getInstance().getReference().child("NotificationRequest");
                DatabaseReference curUser = notifyRequest.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                curUser.child(posts.postID).setValue(posts);

                /*try {
                    mPosts.add(0, posts);
                    mAdapter = new PostAdapter(getContext(), mPosts);
                    list.setAdapter(mAdapter);
                }catch (Exception e){}
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(),ViewPostActivity.class);
                        intent.putExtra("postid",mPosts.get(position).postID);
                        //intent.putExtra("ReceiverName",mPosts.get(position).DisplayName);
                        //TODO: Add message preview
                        //intent.putExtra("MessageContent", messageContentHere);
                        startActivity(intent);
                    }
                });
                */
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void getUsersFollowed()
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
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void getPostsNotifications()
    {
        DatabaseReference notify = FirebaseDatabase.getInstance().getReference().child("NotificationRequest");
        notify.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                //.orderByChild("createDate").startAt("201701").endAt("201712").
                //Seems like firebase already sorts them in order of created date!
                .limitToLast(5).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post posts = dataSnapshot.getValue(Post.class);
                try {
                    mPosts.add(0,posts);
                    mAdapter = new PostAdapter(getContext(), mPosts);
                    list.setAdapter(mAdapter);
                }catch (Exception e){}
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void userNotification()
    {

    }


    public static class ImageDownload extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... url) {
            return "";
        }

        @Override
        protected void onPostExecute(String result)
        {
        }

        public Bitmap getBitmapFromURL(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                return null;
            }
        }
    }
}

