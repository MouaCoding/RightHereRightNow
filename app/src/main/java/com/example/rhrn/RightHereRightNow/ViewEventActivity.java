package com.example.rhrn.RightHereRightNow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.Comments;
import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
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


public class ViewEventActivity extends AppCompatActivity implements OnMapReadyCallback {
    public TextView content, likes, comments, shares, displayName, handle;
    public ImageView profile, eventImage;
    public ImageButton back, likeButton, commentButton, shareButton;
    public EditText commentContent;
    public Button postButton;
    public GoogleMap mMap;
    private LatLng createLoc;
    private MapView event_location;

    public ArrayList<Comments> commentArray;
    public ListView commentList;
    public CommentsListActivity.commentsAdapter commentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event/*framed_view_event*/);
        content = (TextView) findViewById(R.id.view_event_content);
        likes = (TextView) findViewById(R.id.user_event_like_count);
        comments = (TextView) findViewById(R.id.user_event_comment_count);
        profile = (ImageView) findViewById(R.id.view_event_user);
        shares = (TextView) findViewById(R.id.user_event_share_count);
        displayName = (TextView) findViewById(R.id.view_user_displayname);
        eventImage = (ImageView) findViewById(R.id.view_user_eventimage);
        eventImage.requestFocus();
        commentList = (ListView) findViewById(R.id.view_event_comment_list);
        handle = (TextView) findViewById(R.id.view_user_handle);
        commentContent = (EditText) findViewById(R.id.Comment_content);
        commentArray = new ArrayList<>();
        commentsAdapter = new CommentsListActivity.commentsAdapter(ViewEventActivity.this,commentArray, "Event");
        commentList.setAdapter(commentsAdapter);
        commentList.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        event_location = (MapView) findViewById(R.id.event_location_map_view);
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
                    createComment(FirebaseAuth.getInstance().getCurrentUser().getUid(), getIntent().getStringExtra("eventid"), temp, 0, null);
                    if(getIntent().getStringExtra("type").equals("Event"))
                        Event.changeCount("comments", getIntent().getStringExtra("eventid"), true);
                    else if(getIntent().getStringExtra("type").equals("Post"))
                        Post.changeCount("comments", getIntent().getStringExtra("eventid"), true);
                    getComments(getIntent().getStringExtra("eventid"), false);
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter Comment", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //event_location.getMapAsync(this);
        //event_location.onCreate(savedInstanceState);
        String eventid = null;
        if (getIntent().getExtras() != null) {
            eventid = getIntent().getExtras().getString("eventid");
            populate(eventid);
            //populateComments(eventid);
            //TODO:MM - get the post location
            //getPostLocation(postid);
        }
        commentArray = new ArrayList<>();
        getComments(getIntent().getStringExtra("eventid"), true);
        //createLoc = new LatLng(0,0);


    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
    }

    private void populate(final String eventid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Event");
        ref.child(eventid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                try {
                    content.setText(event.description);
                } catch (Exception e) {} //to encompass some posts with no description
                likes.setText(Integer.toString(event.likes));
                comments.setText(Integer.toString(event.comments));
                shares.setText(Integer.toString(event.shares));
                displayName.setText(event.DisplayName);
                handle.setText(event.handle);
                try {
                    if (event.userProfilePicture != null)
                        Picasso.with(getBaseContext()).load(event.userProfilePicture).transform(new CircleTransform()).into(profile);
                    else
                        Picasso.with(getBaseContext()).load(R.mipmap.ic_launcher).into(profile);
                } catch (Exception e) {
                }
                try {
                    if (event.ProfilePicture != null) {
                        Picasso.with(getBaseContext()).load(event.ProfilePicture).into(eventImage);
                        //Picasso.with(getBaseContext()).load(event.ProfilePicture).into(eventImage);
                        eventImage.setVisibility(View.VISIBLE);
                    } else
                        Picasso.with(getBaseContext()).load(R.drawable.images).into(eventImage);
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void createComment(String userID, String postID, String Content, int Order, String responseID) {

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
        createdComment.setValue(new Comments(userID, key, Content, postID, Order, 0, 0, false, ServerValue.TIMESTAMP));
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

    public void populateComments(String eventid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments").child(eventid);
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
                    commentsAdapter = new CommentsListActivity.commentsAdapter(ViewEventActivity.this, commentArray, "Event");
                    commentList.setAdapter(commentsAdapter);
                    //commentsAdapter.notifyDataSetChanged();
                    commentList.setOnTouchListener(new ListView.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            int action = event.getAction();
                            switch (action) {
                                case MotionEvent.ACTION_DOWN:
                                    // Disallow ScrollView to intercept touch events.
                                    v.getParent().requestDisallowInterceptTouchEvent(true);
                                    break;

                                case MotionEvent.ACTION_UP:
                                    // Allow ScrollView to intercept touch events.
                                    v.getParent().requestDisallowInterceptTouchEvent(false);
                                    break;
                            }

                            // Handle ListView touch events.
                            v.onTouchEvent(event);
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
