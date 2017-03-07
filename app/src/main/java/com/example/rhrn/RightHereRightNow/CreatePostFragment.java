package com.example.rhrn.RightHereRightNow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.rhrn.RightHereRightNow.firebaseEntry.Event;
import com.example.rhrn.RightHereRightNow.firebaseEntry.Post;
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
        View r = inflater.inflate(R.layout.create_event_page_layout, container, false);
            // TODO: set this page layout to post creation instead of event creation

        Button b = (Button) r.findViewById(R.id.create_event_confirm);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPost();
            }
        });

        //Initializes each text view to the class's objects

        // TODO: change these to post values instead of event values
        post_content = (EditText)r.findViewById(R.id.event_name);
        post_name = (EditText)r.findViewById(R.id.event_description);

        firebaseAuth = FirebaseAuth.getInstance();

        return r;
    }

    public void createPost() {

        String str_event_name = post_name.getText().toString().trim();
        String str_event_content = post_content.getText().toString().trim();

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Location location;

        try {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            ProgressDialog progressDialog = new ProgressDialog(getActivity());

            //Once register button is clicked, will display a progress dialog
            progressDialog.setMessage("Creating Event Please Wait...");
            progressDialog.show();

            DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference createdPost = RootRef.child("Post").push();
            // Event.setValue(str_event_name);
            // then, Event.child().setValue(...)

            // TODO: BB: include all fields from Post rather than just some, and get actual coordinates
            createdPost.setValue(new Post("ownerID", "postID", "createDate", "createTime",
                    str_event_content, "response Post ID", 10, location.getLatitude(), location.getLongitude(), 0,
                    0, 0));

            // Post(String aOwner, String aID, String aCreateDate, String aCreateTime, String aContent,
            //        String aResponseID, double aViewRadius, double aLat, double aLong,
            //        int aOrder, int aLikes, int aComments)


            progressDialog.dismiss();

        } catch (SecurityException e) {}

    }
}
