package com.example.rhrn.RightHereRightNow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.Comments;
import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.firebase_entry.Likes;
import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.example.rhrn.RightHereRightNow.util.CircleTransform;
import com.firebase.client.ServerValue;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ViewPostActivity extends AppCompatActivity implements OnMapReadyCallback {
    public TextView content, likes, comments, shares, displayName, handle, viewComments;
    private TextView likesCount;
    private TextView commentsCount;
    private TextView sharesCount;
    public ImageView profile, postImage;
    public ImageButton back, likeButton, commentButton, shareButton;
    public EditText commentContent;
    public Button postButton;
    public GoogleMap mMap;
    private LatLng createLoc;
    private MapView post_location;
    private CheckBox anon;

    ArrayList<Comments> commentArray;
    ListView commentList;
    CommentsListActivity.CommentsAdapter commentsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        final String postID = getIntent().getStringExtra("postid");
        final String ownerID = getIntent().getStringExtra("ownerID");
        final String currUsr = FirebaseAuth.getInstance().getCurrentUser().getUid();
        content = (TextView) findViewById(R.id.view_post_content);
        likes = (TextView) findViewById(R.id.user_post_like_count);
        comments = (TextView) findViewById(R.id.user_post_comment_count);
        profile = (ImageView) findViewById(R.id.view_post_user);
        shares = (TextView) findViewById(R.id.user_post_share_count);
        displayName = (TextView) findViewById(R.id.view_user_displayname);
        postImage = (ImageView) findViewById(R.id.view_user_postimage);
        postImage.requestFocus();
        commentList = (ListView) findViewById(R.id.view_post_comment_list);
        handle = (TextView) findViewById(R.id.view_user_handle);
        anon = (CheckBox) findViewById(R.id.comment_anonymous_check);
        commentContent = (EditText) findViewById(R.id.Comment_content);
        likesCount = (TextView) findViewById(R.id.user_post_like_count);
        commentsCount = (TextView) findViewById (R.id.user_post_comment_count);
        sharesCount = (TextView) findViewById(R.id.user_post_share_count);
        viewComments = (TextView) findViewById(R.id.view_comments);
        viewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentList.setVisibility(View.VISIBLE);
                getComments(getIntent().getStringExtra("postid"), false);
            }
        });

        commentArray = new ArrayList<>();
        commentsAdapter = new CommentsListActivity.CommentsAdapter(ViewPostActivity.this,commentArray, "Post");
        commentList.setAdapter(commentsAdapter);
        commentList.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent post) {
                int action = post.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch posts.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch posts.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch posts.
                v.onTouchEvent(post);
                return true;
            }
        });

        post_location = (MapView) findViewById(R.id.post_location_map_view);
        back = (ImageButton) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        postButton = (Button) findViewById(R.id.Comment_post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //commentsAdapter.clear();
                String temp = commentContent.getText().toString();
                temp = temp.trim();
                if (temp.length() > 0) {
                    commentArray = new ArrayList<Comments>();
                    createComment(FirebaseAuth.getInstance().getCurrentUser().getUid(), getIntent().getStringExtra("postid"), temp, 0, null, anon.isChecked());
                    if(((String)getIntent().getStringExtra("type")).equals("Event"))
                        Event.changeCount("comments", getIntent().getStringExtra("postid"), true);
                    else if(((String)getIntent().getStringExtra("type")).equals("Post"))
                        Post.changeCount("comments", getIntent().getStringExtra("postid"), true);
                    getComments(getIntent().getStringExtra("postid"), true);
                    commentContent.setText("");
                    commentsAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter Comment", Toast.LENGTH_SHORT).show();
                }
            }
        });

        likeButton = (ImageButton) findViewById(R.id.user_post_like_button);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Likes.hasLiked(1, postID, currUsr )){
                    likeButton.setColorFilter(ContextCompat.getColor(ViewPostActivity.this,R.color.colorTextDark));
                    Toast.makeText(ViewPostActivity.this, "Unliked", Toast.LENGTH_SHORT).show();
                    Post.Unlike(postID, currUsr);
                    updateCounts(postID);
                }
                else{
                    likeButton.setColorFilter(ContextCompat.getColor(ViewPostActivity.this,R.color.crimson));
                    Toast.makeText(ViewPostActivity.this, "Liked", Toast.LENGTH_SHORT).show();
                    Post.Like(postID, currUsr);
                    updateCounts(postID);
                }
            }
        });

        commentButton = (ImageButton) findViewById(R.id.user_post_comment_button);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = ViewPostActivity.this;
                Bundle params = new Bundle();
                Intent intent = new Intent(context, CommentsListActivity.class);
                intent.putExtra("postID", postID.toString());
                intent.putExtra("type", "Post");
                //context.startActivityForResult(intent, RC);
                context.startActivity(intent);
                updateCounts(postID);

            }
        });
        shareButton = (ImageButton) findViewById(R.id.user_post_share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareButton.setColorFilter(ContextCompat.getColor(ViewPostActivity.this,R.color.MainBlue));
                Post.Share(postID, currUsr);
                updateCounts(postID);
            }
        });
        displayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPostActivity.this, ViewUserActivity.class);
                intent.putExtra("otherUserID", getIntent().getExtras().getString("eventid"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPostActivity.this, ViewUserActivity.class);
                intent.putExtra("otherUserID", getIntent().getExtras().getString("eventid"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });



        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Likes.hasLiked(2, postID, currUsr )){
                    likeButton.setColorFilter(ContextCompat.getColor(ViewPostActivity.this,R.color.colorTextDark));
                    Toast.makeText(ViewPostActivity.this, "Unliked", Toast.LENGTH_SHORT).show();
                    Post.Unlike(postID, currUsr);
                    updateCounts(postID);
                }
                else{
                    likeButton.setColorFilter(ContextCompat.getColor(ViewPostActivity.this,R.color.crimson));
                    Likes.Like(2, postID, currUsr);
                    Toast.makeText(ViewPostActivity.this, "Liked", Toast.LENGTH_SHORT).show();
                    Post.Like(postID, currUsr);
                    updateCounts(postID);
                }
            }
        });


        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = ViewPostActivity.this;
                Bundle params = new Bundle();
                Intent intent = new Intent(context, CommentsListActivity.class);
                intent.putExtra("postID", postID.toString());
                intent.putExtra("type", "Post");
                //context.startActivityForResult(intent, RC);
                context.startActivity(intent);
                updateCounts(postID);

            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareButton.setColorFilter(ContextCompat.getColor(ViewPostActivity.this,R.color.MainBlue));
                Post.Share(postID, currUsr);
                updateCounts(postID);
            }
        });


        //post_location.getMapAsync(this);
        //post_location.onCreate(savedInstanceState);
        String postid = null;
        if (getIntent().getExtras() != null) {
            postid = getIntent().getExtras().getString("postid");
            populate(postid);
            //populateComments(postid);
            //TODO:MM - get the post location
            //getPostLocation(postid);
        }
        //createLoc = new LatLng(0,0);


    }

    public void updateCounts(final String postID){
        likesCount = (TextView) findViewById(R.id.user_post_like_count);
        commentsCount = (TextView) findViewById (R.id.user_post_comment_count);
        sharesCount = (TextView) findViewById(R.id.user_post_share_count);
        Post.requestPost(postID, "authToken", new Post.PostReceivedListener() {
            @Override
            public void onPostReceived(Post... posts) {
                Post ev = posts[0];
                try{
                    likesCount.setText(Integer.toString(ev.likes));
                    commentsCount.setText(Integer.toString(ev.comments));
                    sharesCount.setText(String.valueOf(ev.shares));


                } catch(Exception e){}
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
    }

    private void populate(final String postid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Post");
        ref.child(postid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                try {
                    content.setText(post.content);
                } catch (Exception e) {} //to encompass some posts with no description
                likes.setText(Integer.toString(post.likes));
                comments.setText(Integer.toString(post.comments));
                shares.setText(Integer.toString(post.shares));
                if(!post.isAnon) {
                    displayName.setText(post.DisplayName);
                    handle.setText(post.handle);
                    try {
                        if (post.ProfilePicture != null)
                            Picasso.with(getBaseContext()).load(post.ProfilePicture).transform(new CircleTransform()).into(profile);
                        else
                            Picasso.with(getBaseContext()).load(R.mipmap.ic_launcher).into(profile);
                    } catch (Exception e) {
                    }
                    try {
                        if (post.PostPicture != null) {
                            Picasso.with(getBaseContext()).load(post.PostPicture).into(postImage);
                            //Picasso.with(getBaseContext()).load(post.ProfilePicture).into(postImage);
                            postImage.setVisibility(View.VISIBLE);
                        } else
                            Picasso.with(getBaseContext()).load(R.drawable.images).into(postImage);
                    } catch (Exception e) {
                    }
                }


                else{
                    displayName.setText("Anonymous");
                    handle.setText("");
                    handle.setClickable(false);
                    displayName.setClickable(false);
                    profile.setImageResource(R.drawable.happy);
                    profile.setClickable(false);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void createComment(String userID, String postID, String Content, int Order, String responseID, boolean anon) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference rootreference = FirebaseDatabase.getInstance().getReference("Comments").child(postID);
        DatabaseReference reference;
        if (Order == 0) {
            reference = rootRef.child("comment").push();
        } else {
            reference = rootRef.child("comment").child(postID).child(responseID).push();
        }
        String key = reference.getKey();

        DatabaseReference createdComment = FirebaseDatabase.getInstance().getReference("Comments").child(postID).child(key);
        createdComment.setValue(new Comments(userID, key, Content, postID, Order, 0, 0, anon, ServerValue.TIMESTAMP));
        createdComment.child("timestamp_create").setValue(ServerValue.TIMESTAMP);
    }

    public void getPostLocation(final String postid) {
        DatabaseReference postsOnMap = FirebaseDatabase.getInstance().getReference("PostLocations");
        final GeoFire postFire = new GeoFire(postsOnMap);

        postFire.getLocation("location", new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                GeoQuery postQuery = postFire.queryAtLocation(new GeoLocation(location.latitude, location.longitude), 10);
                postQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String s, GeoLocation l) {
                        LatLng location = new LatLng(l.latitude, l.longitude);
                        mMap.addMarker(new MarkerOptions()
                                .position(location).draggable(false)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.exclamation_blue)));
                    }

                    @Override
                    public void onKeyExited(String key) {
                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {
                    }

                    @Override
                    public void onGeoQueryReady() {
                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }

    public void getComments(String postID, boolean first) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Comments").child(postID);
        ref.orderByChild("timestamp_create");
        if (first) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                        Comments comment = commentSnapshot.getValue(Comments.class);
                        commentArray.add(comment);
                        commentsAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

        } else {
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Comments comment = dataSnapshot.getValue(Comments.class);
                    commentsAdapter.add(comment);
                    commentsAdapter.notifyDataSetChanged();
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

    }

    public void populateComments(String postid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    ; //no comments, so do nothing
                else {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Comments comments = dataSnapshot1.getValue(Comments.class);
                        commentArray.add(comments);
                    }
                    commentsAdapter = new CommentsListActivity.CommentsAdapter(getBaseContext(), commentArray, "Post");
                    commentList.setAdapter(commentsAdapter);
                    //commentsAdapter.notifyDataSetChanged();
                    commentList.setOnTouchListener(new ListView.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent post) {
                            int action = post.getAction();
                            switch (action) {
                                case MotionEvent.ACTION_DOWN:
                                    // Disallow ScrollView to intercept touch posts.
                                    v.getParent().requestDisallowInterceptTouchEvent(true);
                                    break;

                                case MotionEvent.ACTION_UP:
                                    // Allow ScrollView to intercept touch posts.
                                    v.getParent().requestDisallowInterceptTouchEvent(false);
                                    break;
                            }

                            // Handle ListView touch posts.
                            v.onTouchEvent(post);
                            return true;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
