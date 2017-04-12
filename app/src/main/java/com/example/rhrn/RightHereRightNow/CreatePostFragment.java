package com.example.rhrn.RightHereRightNow;

import android.app.ProgressDialog;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import static android.content.Context.LOCATION_SERVICE;

public class CreatePostFragment extends Fragment {

    private EditText    post_name,
                        post_content;

    private boolean anon;

    private FirebaseAuth firebaseAuth;

    //private CheckBox check;

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

        CheckBox check = (CheckBox) r.findViewById(R.id.AnonBox);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anon = true;
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

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Creating Event Please Wait...");
            progressDialog.show();

            DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference gettingKey = RootRef.child("Post").push();
            DatabaseReference createdPost = RootRef.child("Post").child("Post_" + gettingKey.getKey());
            gettingKey.setValue(null);

            DatabaseReference postLocation = RootRef.child("PostLocations");
            GeoFire geoFireLocation = new GeoFire(postLocation);

            //set date and time to today, right now?
            // TODO: BB: include all fields from Post rather than just some, and get actual coordinates
            createdPost.setValue(new Post(firebaseAuth.getCurrentUser().getUid(), createdPost.getKey(), date, time,
                    str_event_content, "response Post ID", 10, 0, 0, 0, anon));

            // Post(String aOwner, String aID, String aCreateDate, String aCreateTime, String aContent,
            //        String aResponseID, double aViewRadius, int aOrder, int aLikes, int aComments)

            geoFireLocation.setLocation(createdPost.getKey(), new GeoLocation(location.getLatitude(), location.getLongitude()));

            progressDialog.dismiss();

        } catch (SecurityException e) {}
    }

}
