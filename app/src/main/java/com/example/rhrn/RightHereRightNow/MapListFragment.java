package com.example.rhrn.RightHereRightNow;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.rhrn.RightHereRightNow.firebase_entry.City;
import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MapListFragment extends Fragment {

    public Button eventButton, postButton;

    public ListView eventList, postList;
    public TrendingFragment.EventAdapter eventAdapter;
    public ArrayList<Event> eventArray;
    public ArrayList<Post> postArray;
    public NotificationFragment.PostAdapter postAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = inflater.inflate(R.layout.map_as_list, container, false);

        eventArray = new ArrayList<>();
        postArray = new ArrayList<>();
        eventList = (ListView) r.findViewById(R.id.global_list_trending);
        postList = (ListView) r.findViewById(R.id.global_list_trending);

        eventButton = (Button) r.findViewById(R.id.events_button);
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventArray = new ArrayList<>();
                eventAdapter = new TrendingFragment.EventAdapter(getContext(), eventArray);
                eventList.setAdapter(eventAdapter);
                queryEvents();
            }
        });
        postButton = (Button) r.findViewById(R.id.posts_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postArray = new ArrayList<>();
                postAdapter = new NotificationFragment.PostAdapter(getContext(), postArray);
                postList.setAdapter(postAdapter);
                queryPosts();
            }
        });

        return r;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setContentView(R.layout.map_as_list);

    }


    private void queryEvents() {

    }
    private void queryPosts(){

    }



/*
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

                //TODO replace this to list

            }  // have discovered an event, so put it in hashmap and put a marker for it

            @Override
            public void onKeyMoved(String s, GeoLocation l) {
                eventMarkerKeys.remove(eventKeyMarkers.remove(s));

                LatLng location = new LatLng(l.latitude, l.longitude);


            }  // event has been moved, so move marker

            @Override
            public void onKeyExited(String s) {
                eventMarkerKeys.remove(eventKeyMarkers.remove(s));
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
                //here

            }

            @Override
            public void onKeyMoved(String s, GeoLocation l) {
                postMarkerKeys.remove(postKeyMarkers.remove(s));

                LatLng location = new LatLng(l.latitude, l.longitude);


            }

            @Override
            public void onKeyExited(String s) {
                postMarkerKeys.remove(postKeyMarkers.remove(s));
            }

            @Override
            public void onGeoQueryError(DatabaseError e) {
                Log.d("ERROR", "GEOQUERY POST ERROR");
            }

            @Override
            public void onGeoQueryReady() {

            }
        });
    }*/


}
