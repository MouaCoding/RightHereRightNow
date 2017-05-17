package com.example.rhrn.RightHereRightNow;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.R;
import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.example.rhrn.RightHereRightNow.util.LocationUtils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.example.rhrn.RightHereRightNow.MapsFragment.getBitmapFromURL;


public class ViewEventActivity extends AppCompatActivity implements OnMapReadyCallback {
    TextView content, likes, comments, shares, displayName;
    ImageView profile, eventImage;
    GoogleMap mMap;
    private LatLng createLoc;
    private MapView event_location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        content = (TextView) findViewById(R.id.view_event_content);
        likes = (TextView) findViewById(R.id.user_event_like_count);
        comments = (TextView) findViewById(R.id.user_event_comment_count);
        profile = (ImageView) findViewById(R.id.view_event_user);
        shares = (TextView) findViewById(R.id.user_event_share_count);
        displayName = (TextView) findViewById(R.id.view_user_displayname);
        eventImage = (ImageView) findViewById(R.id.view_user_eventimage);

        event_location = (MapView) findViewById(R.id.event_location_map_view);
        //event_location.getMapAsync(this);
        //event_location.onCreate(savedInstanceState);
        String eventid = null;
        if(getIntent().getExtras()!=null) {
            eventid = getIntent().getExtras().getString("eventid");
            populate(eventid);
            //TODO:MM - get the post location
            //getPostLocation(postid);
        }
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
                try {content.setText(event.description);}catch(Exception e){} //to encompass some posts with no description
                likes.setText(Integer.toString(event.likes));
                comments.setText(Integer.toString(event.comments));
                shares.setText(Integer.toString(event.shares));
                displayName.setText(event.DisplayName);
                try{
                    if(event.userProfilePicture != null)
                        Picasso.with(getBaseContext()).load(event.userProfilePicture).into(profile);
                    else
                        Picasso.with(getBaseContext()).load(R.mipmap.ic_launcher).into(profile);
                } catch(Exception e){}
                try{
                    if(event.ProfilePicture != null)
                        Picasso.with(getBaseContext()).load(event.ProfilePicture).into(eventImage);
                    else
                        Picasso.with(getBaseContext()).load(R.drawable.ic_group_black_24dp).into(eventImage);
                } catch(Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                    public void onKeyExited(String key) {}
                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {}
                    @Override
                    public void onGeoQueryReady() {}
                    @Override
                    public void onGeoQueryError(DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }

}
