package com.example.rhrn.RightHereRightNow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.firebase_entry.Comments;
import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ViewPostActivity extends AppCompatActivity implements OnMapReadyCallback {
    TextView content, likes, comments, shares, displayName;
    ImageView profile;
    GoogleMap mMap;
    private LatLng createLoc;
    private MapView post_location;

    ArrayList<Comments> commentArray;
    ListView commentList;
    CommentsListActivity.CommentsAdapter commentsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        content = (TextView) findViewById(R.id.view_post_content);
        likes = (TextView) findViewById(R.id.user_post_like_count);
        comments = (TextView) findViewById(R.id.user_post_comment_count);
        profile = (ImageView) findViewById(R.id.view_post_user);
        shares = (TextView) findViewById(R.id.user_post_share_count);
        displayName = (TextView) findViewById(R.id.view_user_displayname);
        commentList = (ListView) findViewById(R.id.view_post_comment_list);
        commentArray = new ArrayList<>();

        post_location = (MapView) findViewById(R.id.post_location_map_view);
        //post_location.getMapAsync(this);
        //post_location.onCreate(savedInstanceState);
        String postid = null;
        if (getIntent().getExtras() != null) {
            postid = getIntent().getExtras().getString("postid");
            populate(postid);
            populateComments(postid);
            //TODO:MM - get the post location
            //getPostLocation(postid);
        }
        //createLoc = new LatLng(0,0);


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
                content.setText(post.content);
                likes.setText(Integer.toString(post.likes));
                comments.setText(Integer.toString(post.comments));
                shares.setText(Integer.toString(post.shares));
                displayName.setText(post.DisplayName);
                try {
                    if (post.ProfilePicture != null)
                        Picasso.with(getBaseContext()).load(post.ProfilePicture).into(profile);
                    else
                        Picasso.with(getBaseContext()).load(R.mipmap.ic_launcher).into(profile);
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getPostLocation(final String postid) {
        DatabaseReference postsOnMap = FirebaseDatabase.getInstance().getReference("PostLocations").child(postid);
        final GeoFire postFire = new GeoFire(postsOnMap);

        postFire.getLocation(postid, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                GeoQuery postQuery = postFire.queryAtLocation(new GeoLocation(location.latitude, location.longitude), 10);
                LatLng l = new LatLng(location.latitude, location.longitude);
                mMap.addMarker(new MarkerOptions().position(l).draggable(false)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.exclamation_blue)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }

    public void populateComments(String postid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    ;
                else {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Comments comments = dataSnapshot1.getValue(Comments.class);
                        commentArray.add(comments);
                    }
                    commentsAdapter = new CommentsListActivity.CommentsAdapter(getBaseContext(), commentArray, "Post");
                    commentList.setAdapter(commentsAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
