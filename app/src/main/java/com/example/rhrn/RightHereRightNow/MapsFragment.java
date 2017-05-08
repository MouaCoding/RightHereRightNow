package com.example.rhrn.RightHereRightNow;

import android.Manifest;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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

import static com.facebook.FacebookSdk.getApplicationContext;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATIONS_PERMISSION = 0;
    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public int countNumber = 0;
    public boolean isEducation = false, isSports = false, isParty = false, isClubEvent = false, isOther = false, logout = false;
    private GoogleMap mMap;
    public MapView mapView;
    public static final String TAG = MapsFragment.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int radius = 1000;
    private FloatingActionButton button;
    private Menu menu;

    private double kmToMiles = 0.621371;
    private double curLatitude;
    private double curLongitude;
    boolean locationChanged = false;

    private String curUserID;
    private GeoLocation curLocation;

    private DatabaseReference   eventsOnMap,
                                postsOnMap;

    public BottomNavigationView topNavigationView;

    // eventually remove these
    ValueAnimator temp;
    Marker ourLoc;
    Circle removeCircle;

    GeoQuery eventQuery;
    GeoQuery postQuery;

    private Bitmap Marker;

    private HashMap<Marker, String> eventMarkerKeys = new HashMap<Marker, String>();
    private HashMap<String, Marker> eventKeyMarkers = new HashMap<String, Marker>();
    private HashMap<Marker, String> postMarkerKeys  = new HashMap<Marker, String>();
    private HashMap<String, Marker> postKeyMarkers  = new HashMap<String, Marker>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View r = (View) inflater.inflate(R.layout.maps_fragment_layout, container, false);

        mapView = (MapView) r.findViewById(R.id.primary_map);
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);

        drawPointsWithinUserRadius();
        //mapView = new MapView(getActivity());


        topNavigationView = (BottomNavigationView) r.findViewById(R.id.top_navigation);

        try {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) topNavigationView.getChildAt(0);
            BottomNavigationItemView city = (BottomNavigationItemView) menuView.getChildAt(2);
            city.setShiftingMode(false);
            city.setChecked(city.getItemData().isChecked());
        }catch (Exception e){}

        topNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem bottom_navigation) {
                        View menuItemView = r.findViewById(R.id.search);
                        View options = r.findViewById(R.id.refresh);
                        switch (bottom_navigation.getItemId()) {
                            case R.id.search:
                                filterMenu(menuItemView);
                                break;
                            case R.id.message:
                                getCurrentUserInfo();
                                break;
                            case R.id.current_city:
                                break;
                            case R.id.favorite:
                                promptFavorite();
                                break;
                            case R.id.refresh:
                                optionsMenu(options);
                                break;
                        }
                        return true;
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

        Marker = drawMarkerWithSize(100,100);

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
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses.size() > 0 & addresses != null) {
                //Toast.makeText(getApplicationContext(),addresses.get(0).getLocality(),Toast.LENGTH_LONG).show();
                MenuItem cityMenuItem = topNavigationView.getMenu().findItem(R.id.current_city);

                cityMenuItem.setTitle(addresses.get(0).getLocality());
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("My Location");
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.exc));
        ourLoc = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

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

    public void drawPointsWithinUserRadius() {
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

                Marker m = mMap.addMarker(new MarkerOptions()
                        .position(location).draggable(false)
                        //TODO: MM: Change marker size with our algorithm -> query likes and multiply
                        .icon(BitmapDescriptorFactory.fromBitmap(Marker)));
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.exclamation_point)));
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
                Marker m = mMap.addMarker(new MarkerOptions().position(location).draggable(false)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.exclamation_point)));
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

    public void getCurrentUserInfo()
    {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String userKey = user.getUid();
            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("User");
            rootRef.child(userKey).child("UsersMessaged").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> keys = new ArrayList<String>();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String other = userSnapshot.getKey();
                        keys.add(other);
                    }

                    Bundle extra = new Bundle();
                    extra.putSerializable("objects", keys);

                    Intent intent = new Intent(getApplicationContext(), MessageListActivity.class);
                    intent.putExtra("extra",extra);
                    startActivity(intent);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
    }

    public Bitmap drawMarkerWithSize(int width, int height)
    {
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources()
                .getDrawable(R.drawable.exclamation_point,null);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap marker = Bitmap.createScaledBitmap(b, width, height, false);
        return marker;
    }

    public void promptFavorite()
    {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getActivity());
        dlgAlert.setTitle("Would you like to save this location?");
        dlgAlert.setMessage("You can come back to this location later.");

        dlgAlert.setNegativeButton("Yes", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Opens the gallery of the phone if user clicked "Upload"
                Toast.makeText(getApplicationContext(), "Location Saved!", Toast.LENGTH_LONG).show();
            }
        });

        //if user cancels
        dlgAlert.setPositiveButton("No", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        dlgAlert.setCancelable(true);
        dlgAlert.create();
        dlgAlert.show();
    }

    public void optionsMenu(View r)
    {
        PopupMenu popup = new PopupMenu(getActivity(), r);
        popup.getMenuInflater().inflate(R.menu.options_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.action1) {
                    Toast.makeText(getApplicationContext(),"Hello, Welcome to RightHereRightNow!",Toast.LENGTH_LONG).show();
                    return true;
                }
                else if (i == R.id.action2){
                    Toast.makeText(getApplicationContext(),"Here are some quotes to brighten your day.",Toast.LENGTH_LONG).show();
                    return true;
                }
                else if (i == R.id.action3) {
                    Toast.makeText(getApplicationContext(),"Keep Calm and Never Give Up.",Toast.LENGTH_LONG).show();
                    return true;
                }
                else if (i == R.id.action4) {
                    Toast.makeText(getApplicationContext(),"The Sky is the Limit.",Toast.LENGTH_LONG).show();
                    return true;
                }
                else if (i == R.id.logout) {
                    logout = true;
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ); // Clear all activities above it
                    startActivity(intent);
                    getActivity().finish();
                    return true;
                }
                else {
                    return onMenuItemClick(item);
                }
            }
        });
        popup.show();
    }

    public void filterMenu(View r)
    {
        PopupMenu popup = new PopupMenu(getActivity(), r);
        popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.filter1) {
                    checkboxFilter(item);
                    isEducation = true;
                    return false;
                }
                else if (i == R.id.filter2){
                    checkboxFilter(item);
                    isSports = true;
                    return false;
                }
                else if (i == R.id.filter3) {
                    checkboxFilter(item);
                    isParty = true;
                    return false;
                }
                else if (i == R.id.filter4) {
                    checkboxFilter(item);
                    isClubEvent = true;
                    return false;
                }
                else if (i == R.id.filter5) {
                    checkboxFilter(item);
                    isOther = true;
                    return false;
                }
                else if (i == R.id.done_filter) {
                    boolean[] filters = new boolean[5];
                    filters[0] = isEducation;
                    filters[1] = isSports;
                    filters[2] = isParty;
                    filters[3] = isClubEvent;
                    filters[4] = isOther;

                    return true;
                }
                else {
                    return onMenuItemClick(item);
                }
            }
        });
        popup.show();
    }

    public void checkboxFilter(MenuItem item)
    {
        item.setChecked(!item.isChecked());
        SharedPreferences settings = getActivity().getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("checkbox", item.isChecked());
        editor.commit();
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        item.setActionView(new View(getApplicationContext()));
    }

}
