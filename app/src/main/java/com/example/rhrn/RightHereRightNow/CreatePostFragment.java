package com.example.rhrn.RightHereRightNow;

import android.app.ProgressDialog;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.EditText;
import android.widget.Toast;


import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.example.rhrn.RightHereRightNow.util.LocationUtils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import static android.content.Context.LOCATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class CreatePostFragment extends Fragment {

    private EditText    post_name,
                        post_content;

    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
        View r = inflater.inflate(R.layout.create_post_page_layout, container, false);
            // TODO: set this page layout to post creation instead of event creation

        Button b = (Button) r.findViewById(R.id.confirm_post_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPost();
            }
        });

        //Initializes each text view to the class's objects

        // TODO: change these to post values instead of event values
        post_content = (EditText)r.findViewById(R.id.content_post);
      ///  post_name = (EditText)r.findViewById(R.id.event_description);

        firebaseAuth = FirebaseAuth.getInstance();

        return r;
    }

    public void createPost() {

       // String str_event_name = post_name.getText().toString().trim();
        String str_event_content = post_content.getText().toString().trim();

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

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

            Location location = LocationUtils.getBestAvailableLastKnownLocation(getContext());

            //ProgressDialog progressDialog = new ProgressDialog(getActivity());
            //progressDialog.setMessage("Creating Event Please Wait...");
            //progressDialog.show();


            Toast.makeText(getContext(), "Creating Post...", Toast.LENGTH_SHORT).show();
            DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference gettingKey = RootRef.child("Post").push();
            DatabaseReference createdPost = RootRef.child("Post").child("Post_" + gettingKey.getKey());
            gettingKey.setValue(null);

            DatabaseReference postLocation = RootRef.child("PostLocations");
            GeoFire geoFireLocation = new GeoFire(postLocation);

            //set date and time to today, right now?
            // TODO: BB: include all fields from Post rather than just some, and get actual coordinates
            createdPost.setValue(new Post(firebaseAuth.getCurrentUser().getUid(), createdPost.getKey(), date, time,
                    str_event_content, "response Post ID", 10, 0, 0, 0,false));
            createdPost.child("timestamp_create").setValue(ServerValue.TIMESTAMP);


            // Post(String aOwner, String aID, String aCreateDate, String aCreateTime, String aContent,
            //        String aResponseID, double aViewRadius, int aOrder, int aLikes, int aComments)

            geoFireLocation.setLocation(createdPost.getKey(), new GeoLocation(location.getLatitude(), location.getLongitude()));
            //Saves the city of created event
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (addresses.size() > 0 & addresses != null) {
                    RootRef.child("Post").child("Post_" + gettingKey.getKey()).child("City")
                            .setValue(addresses.get(0).getLocality());
                }
            }catch (IOException e) {
                e.printStackTrace();
            }

            setExtraValues(createdPost.getKey(), firebaseAuth.getCurrentUser().getUid());


            Toast.makeText(getContext(), "Post Created!", Toast.LENGTH_SHORT).show();
            //progressDialog.dismiss();

        } catch (SecurityException e) {}
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
