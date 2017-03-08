package com.example.rhrn.RightHereRightNow;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.app.ProgressDialog;

import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.LOCATION_SERVICE;

public class CreateEventFragment extends Fragment {

    private EditText        event_name,
                            event_description,
                            startDate,
                            endDate,
                            startTime,
                            endTime,
                            address;


    private FirebaseAuth    firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
        View r = inflater.inflate(R.layout.create_event_page_layout, container, false);

        Button b = (Button) r.findViewById(R.id.create_event_confirm);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent();
            }
        });

        //Initializes each text view to the class's objects
        event_name = (EditText)r.findViewById(R.id.event_name);
        event_description = (EditText)r.findViewById(R.id.event_description);
        startDate = (EditText)r.findViewById(R.id.editStartDate);
        endDate = (EditText)r.findViewById(R.id.editEndDate);
        startTime = (EditText)r.findViewById(R.id.editStartTime);
        endTime = (EditText)r.findViewById(R.id.editEndTime);
        address = (EditText)r.findViewById(R.id.editAddress);

        firebaseAuth = FirebaseAuth.getInstance();


        return r;
    }

    public void getUserInfo(){

    }


    public void createEvent() {

        String str_event_name = event_name.getText().toString().trim();
        String str_event_description = event_description.getText().toString().trim();
        String str_eventSDate = startDate.getText().toString();
        String str_eventEDate = endDate.getText().toString();
        String str_eventSTime = startTime.getText().toString();
        String str_eventETime = endTime.getText().toString();
        String str_eventAddr  = address.getText().toString();


        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Location location;

        try {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            ProgressDialog progressDialog = new ProgressDialog(getActivity());

            //Once register button is clicked, will display a progress dialog
            progressDialog.setMessage("Creating Event Please Wait...");
            progressDialog.show();

            DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference createdEvent = RootRef.child("Event").push();
            // Event.setValue(str_event_name);
            // then, Event.child().setValue(...)


            // TODO: BB: include all fields from Event rather than just some, and get actual coordinates
            createdEvent.setValue(new Event(str_event_name, "Fill in owner ID", str_eventSDate,
                    str_eventEDate, str_eventSTime, str_eventETime, str_eventAddr,
                    str_event_description, location.getLatitude(), location.getLongitude(), 10, 100,
                    0, 4));

            // public Event(String aName, String aOwner, String aStartDate, String aEndDate, String aStartTime,
            //              String aEndTime, String aAddress, String aDescription, double aLat, double aLong,
            //              double aViewRadius, int aLikes, int aComments, int aRSVPs)

            progressDialog.dismiss();

        } catch (SecurityException e) {}

    }

}
