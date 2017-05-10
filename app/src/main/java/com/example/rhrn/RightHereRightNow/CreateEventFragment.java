package com.example.rhrn.RightHereRightNow;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.example.rhrn.RightHereRightNow.util.LocationUtils;
import com.firebase.client.ServerValue;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import static android.content.Context.LOCATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class CreateEventFragment extends Fragment {

    private EditText        event_name,
                            event_description,
                            startDate,
                            endDate,
                            startTime,
                            endTime,
                            address;

    private TimePickerDialog   sTime,
                               eTime;

    private DatePickerDialog   sDate,
                                 eDate;

    int currDay, currMonth, currYear, currHour, currMinute;

    private FirebaseAuth    firebaseAuth;
    public String key;
    public FirebaseUser usr;
    ProgressDialog pd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
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

        Calendar c = Calendar.getInstance();
        currDay = c.get(Calendar.DAY_OF_MONTH);
        currMonth = c.get(Calendar.MONTH);
        currYear = c.get(Calendar.YEAR);
        currHour = c.get(Calendar.HOUR_OF_DAY);
        currMinute = c.get(Calendar.MINUTE);



        firebaseAuth = FirebaseAuth.getInstance();

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sTime = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                   @Override
                   public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String amPm;
                        String min;
                        if(hourOfDay >= 12 ){
                            amPm = "PM";
                        }
                        else{
                            amPm = "AM";
                        }
                        if(hourOfDay == 0) {
                            hourOfDay = 12;
                        }
                        if(hourOfDay > 12){
                            hourOfDay = hourOfDay - 12;
                        }
                       if(minute < 10){
                           min = "0"+minute;
                       }
                       else
                       {
                           min = Integer.toString(minute);
                       }
                        startTime.setText(hourOfDay+":"+min+amPm);
                   }
               }, currHour, currMinute, DateFormat.is24HourFormat(getActivity()));
                sTime.show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eTime = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String amPm;
                        String min;
                        if(hourOfDay >= 12 ){
                            amPm = "PM";
                        }
                        else{
                            amPm = "AM";
                        }
                        if(hourOfDay == 0) {
                            hourOfDay = 12;
                        }
                        if(hourOfDay > 12){
                            hourOfDay = hourOfDay - 12;
                        }
                        if(minute < 10){
                            min = "0"+minute;
                        }
                        else
                        {
                            min = Integer.toString(minute);
                        }
                        endTime.setText(hourOfDay+":"+min+amPm);
                    }
                }, currHour, currMinute, DateFormat.is24HourFormat(getActivity()));
                eTime.show();
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sDate = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        startDate.setText((month+1)+"/"+dayOfMonth+"/"+year);
                    }
                }, currYear, currMonth, currDay);
                sDate.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eDate = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endDate.setText((month+1)+"/"+dayOfMonth+"/"+year);
                    }
                }, currYear, currMonth, currDay);
                eDate.show();
            }
        });

        return r;
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

        try {
            Location location = LocationUtils.getBestAvailableLastKnownLocation(getContext());

            Toast.makeText(getContext(), "Creating Event...", Toast.LENGTH_SHORT).show();
            DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference gettingKey = RootRef.child("Event").push();
            DatabaseReference createdEvent = RootRef.child("Event").child("Event_" + gettingKey.getKey());
            String eventKey = "Event_"+gettingKey.getKey();
            gettingKey.setValue(null);

            DatabaseReference eventLocation = RootRef.child("EventLocations");
            GeoFire geoFireLocation = new GeoFire(eventLocation);

            createdEvent.setValue(new Event(str_event_name, firebaseAuth.getCurrentUser().getUid(), str_eventSDate,
                    str_eventEDate, str_eventSTime, str_eventETime, str_eventAddr,
                    str_event_description, 10, 0, 0, 0));
            createdEvent.child("timestamp_create").setValue(ServerValue.TIMESTAMP);
            createdEvent.child("eventID").setValue("Event_"+gettingKey.getKey());

            setExtraValues(eventKey,firebaseAuth.getCurrentUser().getUid());
            geoFireLocation.setLocation(createdEvent.getKey(), new GeoLocation(location.getLatitude(), location.getLongitude()));

            //Saves the city of created event
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (addresses.size() > 0 & addresses != null) {
                    RootRef.child("Event").child("Event_" + gettingKey.getKey()).child("City")
                            .setValue(addresses.get(0).getLocality());
                }
            }catch (IOException e) {
                e.printStackTrace();
            }

            //progressDialog.dismiss();
            Toast.makeText(getContext(), "Event Created!", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {}




    }

    public void setExtraValues(final String eventID, final String ownerID)
    {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("User").child(ownerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User owner = dataSnapshot.getValue(User.class);
                ref.child("Event").child(eventID).child("DisplayName").setValue(owner.DisplayName);
                ref.child("Event").child(eventID).child("handle").setValue(owner.handle);
                try{
                    ref.child("Event").child(eventID).child("userProfilePicture").setValue(owner.ProfilePicture);
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
