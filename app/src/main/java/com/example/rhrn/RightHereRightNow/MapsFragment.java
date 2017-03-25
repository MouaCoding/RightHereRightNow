package com.example.rhrn.RightHereRightNow;

import android.Manifest;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.facebook.FacebookSdk.getApplicationContext;

import java.util.HashMap;

public class MapsFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATIONS_PERMISSION = 0;
    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleMap mMap;
    private MapView mapView;
    public static final String TAG = MapsFragment.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int radius = 1000;
    private FloatingActionButton button;

    private double kmToMiles = 0.621371;
    private double curLatitude;
    private double curLongitude;

    private String curUserID;
    private GeoLocation curLocation;

    private DatabaseReference   eventsOnMap,
                                postsOnMap;

    // eventually remove these
    ValueAnimator temp;
    Marker ourLoc;
    Circle removeCircle;

    GeoQuery eventQuery;
    GeoQuery postQuery;

    private HashMap<Marker, String> eventMarkerKeys = new HashMap<Marker, String>();
    private HashMap<String, Marker> eventKeyMarkers = new HashMap<String, Marker>();
    private HashMap<Marker, String> postMarkerKeys  = new HashMap<Marker, String>();
    private HashMap<String, Marker> postKeyMarkers  = new HashMap<String, Marker>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = (View) inflater.inflate(R.layout.maps_fragment_layout, container, false);

        mapView = (MapView) r.findViewById(R.id.primary_map);
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);

        drawPointsWithinUserRadius();
        //mapView = new MapView(getActivity());

        button = (FloatingActionButton) r.findViewById(R.id.message_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MessageList.class);
                startActivity(intent);
            }
        });


        // restore any state here if necessary

        return r;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing googleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        //button = (Button) findViewById(R.id.message_button);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@Nullable Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        curUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (eventMarkerKeys.containsKey(marker)) {
                    ViewEventDialogFragment.createInstance(eventMarkerKeys.get(marker)).show(getChildFragmentManager(), null);
                }  // if marker clicked is an event

                else if (postMarkerKeys.containsKey(marker)) {
                    ViewPostDialogFragment.createInstance(postMarkerKeys.get(marker)).show(getChildFragmentManager(), null);
                }  // if marker clicked is a post

                return true;
            }
        });  // add listeners for clicking markers

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragEnd(Marker marker) {
                ourLoc.remove();
                removeCircle.remove();
                temp.removeAllListeners();
                temp.end();
                temp.cancel();

                Location newPos = new Location(LocationManager.GPS_PROVIDER);
                newPos.setLatitude(marker.getPosition().latitude);
                newPos.setLongitude(marker.getPosition().longitude);
                handleNewLocation(newPos);
            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }
        });

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).draggable(true).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");

        //if app has permission to use current location,
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //finds the current location
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                //if it cannot, then it requests for the location from client
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                //set the map to the current location
                handleNewLocation(location);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        eventQuery.setCenter(new GeoLocation(curLatitude = location.getLatitude(), curLongitude = location.getLongitude()));
        postQuery.setCenter(new GeoLocation(curLatitude, curLongitude));

        LatLng latLng = new LatLng(curLatitude, curLongitude);

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("My Location");
        ourLoc = mMap.addMarker(options);
        //zoom in to 15, (10 is city view), but want user view.
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));

        final Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(curLatitude, curLongitude))
                .strokeColor(Color.CYAN)
                .radius(radius));
        removeCircle = circle;

        ValueAnimator vAnimator = new ValueAnimator();
        temp = vAnimator;
        vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        vAnimator.setRepeatMode(ValueAnimator.RESTART);
        //TODO: Implement radius change where user wants to change radius.
        //radius can be changed.
        vAnimator.setIntValues(0, radius);
        //This sets how long you want the duration of animation to be.
        vAnimator.setDuration(4000);
        vAnimator.setEvaluator(new IntEvaluator());
        vAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                // Log.e("", "" + animatedFraction);
                circle.setRadius(animatedFraction * radius);
            }
        });
        vAnimator.start();


    }

    private void drawPointsWithinUserRadius() {
        eventsOnMap = FirebaseDatabase.getInstance().getReference("EventLocations");
        GeoFire eventFire = new GeoFire(eventsOnMap);

        postsOnMap = FirebaseDatabase.getInstance().getReference("PostLocations");
        GeoFire postFire = new GeoFire(postsOnMap);

        eventQuery = eventFire.queryAtLocation(new GeoLocation(curLatitude, curLongitude), radius / 1000); // 12800.0);
                //(radius * 0.001) * kmToMiles * 70);
        postQuery = postFire.queryAtLocation(eventQuery.getCenter(), radius / 1000);//12800.0);
                // (radius * 0.001) * kmToMiles * 70);

        // might be something to do with initialization

        Log.d("CENTER", Double.toString(eventQuery.getCenter().latitude));

        eventQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String s, GeoLocation l) {

                LatLng location = new LatLng(l.latitude, l.longitude);
                Marker m = mMap.addMarker(new MarkerOptions().position(location).draggable(false));
                eventMarkerKeys.put(m, s);
                eventKeyMarkers.put(s, m);
            }  // have discovered an event, so put it in hashmap and put a marker for it

            @Override
            public void onKeyMoved(String s, GeoLocation l) {
                Marker toRemove = eventKeyMarkers.get(s);
                eventMarkerKeys.remove(eventKeyMarkers.remove(s));
                toRemove.remove();

                LatLng location = new LatLng(l.latitude, l.longitude);
                Marker m = mMap.addMarker(new MarkerOptions().position(location).draggable(false));

                eventMarkerKeys.put(m, s);
                eventKeyMarkers.put(s, m);
            }  // event has been moved, so move marker

            @Override
            public void onKeyExited(String s) {
                Marker m = eventKeyMarkers.get(s);
                eventMarkerKeys.remove(eventKeyMarkers.remove(s));
                m.remove();
            }  // event no longer in range, so remove it from hashmaps and map

            @Override
            public void onGeoQueryError(DatabaseError e) {
                Log.d("ERROR", "GEOQUERY EVENT ERROR");
            }

            @Override
            public void onGeoQueryReady() {

            }
        });

        postQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String s, GeoLocation l) {
                LatLng location = new LatLng(l.latitude, l.longitude);

                Marker m = mMap.addMarker(new MarkerOptions().position(location).draggable(false));
                postMarkerKeys.put(m, s);
                postKeyMarkers.put(s, m);
            }

            @Override
            public void onKeyMoved(String s, GeoLocation l) {
                Marker toRemove = postKeyMarkers.get(s);
                postMarkerKeys.remove(postKeyMarkers.remove(s));
                toRemove.remove();

                LatLng location = new LatLng(l.latitude, l.longitude);
                Marker m = mMap.addMarker(new MarkerOptions().position(location).draggable(false));

                postMarkerKeys.put(m, s);
                postKeyMarkers.put(s, m);
            }

            @Override
            public void onKeyExited(String s) {
                Marker m = postKeyMarkers.get(s);
                postMarkerKeys.remove(postKeyMarkers.remove(s));
                m.remove();
            }

            @Override
            public void onGeoQueryError(DatabaseError e) {
                Log.d("ERROR", "GEOQUERY POST ERROR");
            }

            @Override
            public void onGeoQueryReady() {

            }
        });
    }
}
