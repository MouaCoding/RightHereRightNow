package com.example.rhrn.RightHereRightNow;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.util.LocationUtils;
import com.firebase.client.ServerValue;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import static android.content.Context.LOCATION_SERVICE;

public class CreateEventFragment extends Fragment implements OnMapReadyCallback {

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

    GoogleMap mMap;
    LatLng createLoc;

    private MapView event_location;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
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

        event_location = (MapView) r.findViewById(R.id.event_location_map_view);
        event_location.onCreate(savedInstanceState);
        event_location.getMapAsync(this);

        Location loc = LocationUtils.getBestAvailableLastKnownLocation(getContext());
        createLoc = new LatLng(loc.getLatitude(), loc.getLongitude());

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
                .title("Event Location");

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(createLoc,16));

        mMap.addMarker(x).showInfoWindow();
    }

    @Override
    public void onStart() {
        event_location.onStart();
        super.onStart();
    }

    @Override
    public void onResume() {
        event_location.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        event_location.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        event_location.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        event_location.onDestroy();
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
//            Location location = LocationUtils.getBestAvailableLastKnownLocation(getContext());

            //ProgressDialog progressDialog = new ProgressDialog(getActivity());
            //progressDialog.setMessage("Creating Event, Please Wait...");
            //progressDialog.show();

            Toast.makeText(getContext(), "Creating Event...", Toast.LENGTH_SHORT).show();
            DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference gettingKey = RootRef.child("Event").push();
            DatabaseReference createdEvent = RootRef.child("Event").child("Event_" + gettingKey.getKey());
            gettingKey.setValue(null);

            DatabaseReference eventLocation = RootRef.child("EventLocations");
            GeoFire geoFireLocation = new GeoFire(eventLocation);


            // TODO: BB: include all fields from Event rather than just some, and get actual coordinates
            createdEvent.setValue(new Event(str_event_name, FirebaseAuth.getInstance().getCurrentUser().getUid(), str_eventSDate,
                    str_eventEDate, str_eventSTime, str_eventETime, str_eventAddr,
                    str_event_description, 10, 0, 0, 0));
            createdEvent.child("timestamp_create").setValue(ServerValue.TIMESTAMP);

            // public Event(String aName, String aOwner, String aStartDate, String aEndDate, String aStartTime,
            //              String aEndTime, String aAddress, String aDescription,
            //              double aViewRadius, int aLikes, int aComments, int aRSVPs)

<<<<<<< HEAD




            geoFireLocation.setLocation(createdEvent.getKey(), new GeoLocation(location.getLatitude(), location.getLongitude()));

            progressDialog.dismiss();
=======
            geoFireLocation.setLocation(createdEvent.getKey(), new GeoLocation(createLoc.latitude, createLoc.longitude));
>>>>>>> master

            //progressDialog.dismiss();
            Toast.makeText(getContext(), "Event Created!", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {}

        getActivity().getSupportFragmentManager().popBackStack();
    }

}
