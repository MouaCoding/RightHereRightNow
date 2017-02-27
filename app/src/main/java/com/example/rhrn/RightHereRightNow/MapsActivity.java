package com.example.rhrn.RightHereRightNow;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    //Google maps current location variables
    private static final int INITIAL_REQUEST=1337;
    private static final int LOCATION_REQUEST=INITIAL_REQUEST+3;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    //google map to load
    private GoogleMap mMap;

    //displays the messages of the map's action
    public static final String TAG = MapsActivity.class.getSimpleName();

    //Variable for the api client
    private GoogleApiClient mGoogleApiClient;

    //longitude and latitude of location
    private double longitude;
    private double latitude;
    private LocationRequest mLocationRequest;

    //Radius of the User's circle
    private int radius = 100;

    private static final int REQUEST_LOCATION = 1;
    private EditText view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing googleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
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

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).draggable(true).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


    }
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                //Checks the permissions of user
                switch (requestCode) {
                    //Case 1 is Access_Fine_Location
                    case 1: {
                        //Permission granted for current location
                        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            //set location to my current location
                            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            if (location == null) {
                                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                            }
                            else {
                                //set the map to the current location
                                handleNewLocation(location);
                            }
                        }
                    }
                }
            }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //&& ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //if no access, then request access.
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{ACCESS_FINE_LOCATION},1);
            return;
        }
        //App then calls onRequestPermissionsResult

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
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        //Set the latitude and longitude to the current location
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //set the options of the marker
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("My Location");

        //Add the marker with the options to the map
        mMap.addMarker(options);

        //zoom in to 15, (10 is city view), but want user view.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));

        //Create a circle around the marker with the options
        final Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(currentLatitude,currentLongitude))
                .strokeColor(Color.CYAN)
                .radius(radius));

        //Animate the circle outward to create a beacon-like location
        ValueAnimator vAnimator = new ValueAnimator();
        //Repeat the animation forever
        vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        //Once animation ends, repeat it
        vAnimator.setRepeatMode(ValueAnimator.RESTART);
        //TODO: Implement radius change where user wants to change radius.
        //radius can be changed.
        vAnimator.setIntValues(0, radius);
        //This sets how long you want the duration of animation to be.
        vAnimator.setDuration(4000);
        //function calls to interpolate the animation
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
        //Begin the animation
        vAnimator.start();

    }


}
