package com.example.rhrn.RightHereRightNow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.rhrn.RightHereRightNow.MapsFragment.getBitmapFromURL;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Matt on 4/2/2017.
 */

public class TrendingFragment extends Fragment {

    public Button global, city;
    public TextView eventTitle, eventName, startTime, endTime, numLikes, numComments;
    String otherUserID;

    public ListView trendingList;
    public EventAdapter eventAdapter;
    public ArrayList<Event> eventList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = (View) inflater.inflate(R.layout.trending_posts, container, false);

        global = (Button) r.findViewById(R.id.global_button);
        global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventList = new ArrayList<>();
                eventAdapter = new EventAdapter(getContext(), eventList);
                trendingList.setAdapter(eventAdapter);
                queryAllEvents();
            }
        });
        city = (Button) r.findViewById(R.id.city_button);
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventList = new ArrayList<>();
                eventAdapter = new EventAdapter(getContext(), eventList);
                trendingList.setAdapter(eventAdapter);
                queryCityEvents();
            }
        });

        eventList = new ArrayList<>();
        trendingList = (ListView) r.findViewById(R.id.global_list_trending);

        queryAllEvents();
        return r;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void queryAllEvents()
    {
        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Event").orderByChild("likes").startAt(0).endAt(1000).limitToLast(3).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                    Event ev = dataSnapshot.getValue(Event.class);

                    eventList.add(0,ev);
                    eventAdapter = new EventAdapter(getContext(), eventList);
                    trendingList.setAdapter(eventAdapter);
                    //populateEventHeader(ev.ownerID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class EventAdapter extends ArrayAdapter<Event> {
        EventAdapter(Context context, ArrayList<Event> users){
            super(context, R.layout.user_event_layout, R.id.user_event_title, users);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
            convertView = super.getView(position, convertView, parent);
            Event event = getItem(position);
            TextView eventTitle = (TextView)convertView.findViewById(R.id.user_event_title);
            ImageView eventImage = (ImageView) convertView.findViewById(R.id.user_event_mini_image);
            TextView startTime = (TextView)convertView.findViewById(R.id.user_event_start_time);
            TextView endTime = (TextView)convertView.findViewById(R.id.user_event_end_time);
            TextView eventLoc = (TextView)convertView.findViewById(R.id.user_event_location);
            TextView numLikes = (TextView)convertView.findViewById(R.id.number_likes);
            TextView numComments = (TextView)convertView.findViewById(R.id.number_comments);

            TextView displayNameView = (TextView) convertView.findViewById(R.id.mini_name);
            ImageView profilePicture = (ImageView) convertView.findViewById(R.id.mini_profile_picture);
            TextView userHandleView = (TextView) convertView.findViewById(R.id.mini_user_handle);

            eventTitle.setText(event.eventName);
            startTime.setText(event.startTime);
            endTime.setText(event.endTime);
            eventLoc.setText(event.address);
            numLikes.setText(Integer.toString(event.likes));
            numComments.setText(Integer.toString(event.comments));

            displayNameView.setText(event.DisplayName);
            userHandleView.setText(event.handle);

            try {
                if (event.userProfilePicture != null)
                    profilePicture.setImageBitmap(getBitmapFromURL(event.userProfilePicture));
                else
                    profilePicture.setImageResource(R.mipmap.ic_launcher);
            }catch (Exception e){}
            try{
                if (event.userProfilePicture != null)
                    eventImage.setImageBitmap(getBitmapFromURL(event.ProfilePicture));
                else
                    profilePicture.setImageResource(R.drawable.ic_group_black_24dp);
            } catch (Exception e){}
            return convertView;
        }
    }


    public void queryCityEvents()
    {
        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Event").orderByChild("City").startAt("A").endAt("Z").limitToLast(3).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                    Event ev = dataSnapshot.getValue(Event.class);

                    eventList.add(0,ev);
                    eventAdapter = new EventAdapter(getContext(), eventList);
                    trendingList.setAdapter(eventAdapter);
                    //populateEventHeader(ev.ownerID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
