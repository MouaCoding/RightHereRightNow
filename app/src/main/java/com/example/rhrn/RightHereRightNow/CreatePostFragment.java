package com.example.rhrn.RightHereRightNow;

import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.LOCATION_SERVICE;

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
        Toast.makeText(getActivity(), "got here", Toast.LENGTH_LONG);

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Location location;

        try {
            Toast.makeText(getActivity(), "in try", Toast.LENGTH_LONG);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            ProgressDialog progressDialog = new ProgressDialog(getActivity());

            //Once register button is clicked, will display a progress dialog
            progressDialog.setMessage("Creating Event Please Wait...");
            progressDialog.show();

            DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference createdPost = RootRef.child("Post").push();
            // Event.setValue(str_event_name);
            // then, Event.child().setValue(...)

            //set date and time to today, right now?
            // TODO: BB: include all fields from Post rather than just some, and get actual coordinates
            createdPost.setValue(new Post("ownerID", "postID", "createDate", "createTime",
                    str_event_content, "response Post ID", 10, location.getLatitude(), location.getLongitude(), 0,
                    0, 0, new GeoLocation(location.getLatitude(), location.getLongitude())));

            // Post(String aOwner, String aID, String aCreateDate, String aCreateTime, String aContent,
            //        String aResponseID, double aViewRadius, double aLat, double aLong,
            //        int aOrder, int aLikes, int aComments)


            progressDialog.dismiss();

        } catch (SecurityException e) {}

    }
}
