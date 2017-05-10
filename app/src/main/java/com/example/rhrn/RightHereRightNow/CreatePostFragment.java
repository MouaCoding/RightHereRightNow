package com.example.rhrn.RightHereRightNow;

import android.app.ProgressDialog;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.example.rhrn.RightHereRightNow.util.LocationUtils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import static android.content.Context.LOCATION_SERVICE;

public class CreatePostFragment extends Fragment implements OnMapReadyCallback {
    private MapView post_location;
    private GoogleMap mMap;

    private EditText post_content;

<<<<<<< HEAD
    private CheckBox    anon;

    private FirebaseAuth firebaseAuth;
=======
    private LatLng createLoc;
>>>>>>> master

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = inflater.inflate(R.layout.create_post_page_layout, container, false);

        Button b = (Button) r.findViewById(R.id.confirm_post_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPost();
            }
        });

        anon  = (CheckBox) r.findViewById(R.id.AnonBox);



        //Initializes each text view to the class's objects
        post_content = (EditText)r.findViewById(R.id.content_post);

        post_location = (MapView) r.findViewById(R.id.post_location_map_view);
        post_location.getMapAsync(this);
        post_location.onCreate(savedInstanceState);

        Location loc = LocationUtils.getBestAvailableLastKnownLocation(getContext());
        createLoc = new LatLng(loc.getLatitude(), loc.getLongitude());

        // TODO populate map construct

        return r;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragEnd(Marker marker) {
                createLoc = marker.getPosition();
            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });

        MarkerOptions x = new MarkerOptions()
                .position(createLoc)
                .draggable(true)
                .title("Post Location");

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(createLoc,16));

        mMap.addMarker(x).showInfoWindow();
    }

    @Override
    public void onStart() {
        post_location.onStart();
        super.onStart();
    }

    @Override
    public void onResume() {
        post_location.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        post_location.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        post_location.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        post_location.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        post_location.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        post_location.onLowMemory();
    }

    public void createPost() {
        String postContent = post_content.getText().toString().trim();

        Calendar c = Calendar.getInstance();
        String date = Integer.toString(c.get(Calendar.MONTH)) + "/" + Integer.toString(c.get(Calendar.DAY_OF_MONTH))
                + "/" + Integer.toString(c.get(Calendar.YEAR));
        String time = "";
        int Minute = c.get(Calendar.MINUTE);
        int Hour = c.get(Calendar.HOUR_OF_DAY);
        if(Hour >= 12)
        {
            if(Hour == 12){
                time = Integer.toString(Hour) + ":" + Integer.toString(Minute) + "PM";
            }
            else {
                time = Integer.toString((Hour - 12)) + ":" + Integer.toString(Minute) + "PM";
            }
        }
        else {
            time = Integer.toString(Hour) + ":" + Integer.toString(Minute) + "AM";
        }

        try {

//            Location location = LocationUtils.getBestAvailableLastKnownLocation(getContext());

            //ProgressDialog progressDialog = new ProgressDialog(getActivity());
            //progressDialog.setMessage("Creating Event Please Wait...");
            //progressDialog.show();


            Toast.makeText(getContext(), "Creating Post...", Toast.LENGTH_SHORT).show();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference gettingKey = rootRef.child("Post").push();
            DatabaseReference createdPost = rootRef.child("Post").child("Post_" + gettingKey.getKey());
            gettingKey.setValue(null);

            DatabaseReference postLocation = rootRef.child("PostLocations");
            GeoFire geoFireLocation = new GeoFire(postLocation);

            //set date and time to today, right now?
            // TODO: BB: include all fields from Post rather than just some, and get actual coordinates
<<<<<<< HEAD
            createdPost.setValue(new Post(firebaseAuth.getCurrentUser().getUid(), createdPost.getKey(), date, time,
                    str_event_content, "response Post ID", 10, 0, 0, 0, anon.isChecked()));
=======
            createdPost.setValue(new Post( FirebaseAuth.getInstance().getCurrentUser().getUid(), createdPost.getKey(), date, time,
                    postContent, "response Post ID", 10, 0, 0, 0,false));
>>>>>>> master
            createdPost.child("timestamp_create").setValue(ServerValue.TIMESTAMP);

            geoFireLocation.setLocation(createdPost.getKey(), new GeoLocation(createLoc.latitude, createLoc.longitude));
            setExtraValues(createdPost.getKey(),  FirebaseAuth.getInstance().getCurrentUser().getUid());


            Toast.makeText(getContext(), "Post Created!", Toast.LENGTH_SHORT).show();
            //progressDialog.dismiss();

        } catch (SecurityException e) {}
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void setExtraValues(final String postID, final String ownerID)
    {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("User").child(ownerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User owner = dataSnapshot.getValue(User.class);
                Log.d("HOOO",ownerID);
                ref.child("Post").child(postID).child("DisplayName").setValue(owner.DisplayName);
                try{
                    ref.child("Post").child(postID).child("ProfilePicture").setValue(owner.ProfilePicture);
                }catch (Exception e){}

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
