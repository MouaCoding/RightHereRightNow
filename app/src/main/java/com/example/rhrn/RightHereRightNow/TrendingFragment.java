package com.example.rhrn.RightHereRightNow;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.City;
import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.firebase_entry.Likes;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.example.rhrn.RightHereRightNow.util.CircleTransform;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.rhrn.RightHereRightNow.MapsFragment.getBitmapFromURL;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Matt on 4/2/2017.
 */

public class TrendingFragment extends Fragment {

    public Button global, city;

    public ListView trendingList, cityList;
    public EventAdapter eventAdapter;
    public ArrayList<Event> eventList;
    public ArrayList<City> cityArray;
    public CityAdapter cityAdapter;
    public ProgressDialog pd;

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
                //eventList = new ArrayList<>();
                //eventAdapter = new EventAdapter(getContext(), eventList);
                //trendingList.setAdapter(eventAdapter);
                cityArray = new ArrayList<>();
                showProgressDialog();
                queryCityEvents();
                pd.dismiss();
            }
        });

        eventList = new ArrayList<>();
        cityArray = new ArrayList<>();
        trendingList = (ListView) r.findViewById(R.id.global_list_trending);
        cityList = (ListView) r.findViewById(R.id.global_list_trending);

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
        RootRef.child("Event").orderByChild("likes").startAt(0).endAt(1000).limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                    Event ev = dataSnapshot.getValue(Event.class);
                    eventList.add(0,ev);
                }
                eventAdapter = new EventAdapter(getContext(), eventList);
                trendingList.setAdapter(eventAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class EventAdapter extends ArrayAdapter<Event> {
        private ImageButton likeButton;
        private ImageButton commentButton;
        private ImageButton shareButton;

        EventAdapter(Context context, ArrayList<Event> users){
            super(context, R.layout.user_event_framed_layout, R.id.user_event_title, users);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            final Event event = getItem(position);
            TextView eventTitle = (TextView)convertView.findViewById(R.id.user_event_title);
            ImageView eventImage = (ImageView) convertView.findViewById(R.id.user_event_mini_image);
            TextView startTime = (TextView)convertView.findViewById(R.id.user_event_start_time);
            TextView endTime = (TextView)convertView.findViewById(R.id.user_event_end_time);
            TextView eventLoc = (TextView)convertView.findViewById(R.id.user_event_location);
            TextView numLikes = (TextView)convertView.findViewById(R.id.user_event_like_count);
            TextView numComments = (TextView)convertView.findViewById(R.id.user_event_comment_count);

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

            setButtons(convertView, event.eventID, event.ownerID);
            try{setExtraValues(event.eventID, event.ownerID);}catch (Exception e){}

            try {
                if (event.userProfilePicture != null)
                    Picasso.with(getContext()).load(event.userProfilePicture).transform(new CircleTransform()).into(profilePicture);
                    //profilePicture.setImageBitmap(getBitmapFromURL(event.userProfilePicture));
                else
                    Picasso.with(getContext()).load(R.mipmap.ic_launcher).transform(new CircleTransform()).into(profilePicture);
                //profilePicture.setImageResource(R.mipmap.ic_launcher);
            }catch (Exception e){}
            try{
                if (event.ProfilePicture != null)
                    Picasso.with(getContext()).load(event.userProfilePicture).into(eventImage);
                    //eventImage.setImageBitmap(getBitmapFromURL(event.ProfilePicture));
                else
                    Picasso.with(getContext()).load(R.drawable.images).into(eventImage);
                //eventImage.setImageResource(R.drawable.ic_group_black_24dp);
            } catch (Exception e){}

            //On clicks to navigate to view user or event
            displayNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ViewUserActivity.class);
                    intent.putExtra("otherUserID",event.ownerID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }
            });
            profilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ViewUserActivity.class);
                    intent.putExtra("otherUserID",event.ownerID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }
            });
            eventTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ViewEventActivity.class);
                    intent.putExtra("eventid",event.eventID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }
            });
            eventImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ViewEventActivity.class);
                    intent.putExtra("eventid",event.eventID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }
            });
            return convertView;
        }

        public void setButtons(View view, final String EventID, final String currUsr)
        {
            likeButton = (ImageButton) view.findViewById(R.id.user_event_like_button);
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Likes.hasLiked(2, EventID, currUsr )){
                        likeButton.setColorFilter(ContextCompat.getColor(getContext(),R.color.colorTextDark));
                        Toast.makeText(getContext(), "Unliked", Toast.LENGTH_SHORT).show();
                        FirebaseDatabase.getInstance().getReference("Likes").child(EventID).child(currUsr).removeValue();
                        Event.changeCount("likes", EventID, false);
                    }
                    else{
                        likeButton.setColorFilter(ContextCompat.getColor(getContext(),R.color.crimson));
                        Likes.Like(2, EventID, currUsr);
                        Event.changeCount("likes", EventID, true);
                        Toast.makeText(getContext(), "Liked", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            commentButton = (ImageButton) view.findViewById(R.id.user_event_comment_button);
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = getContext();
                    Bundle params = new Bundle();
                    Intent intent = new Intent(context, CommentsListActivity.class);
                    intent.putExtra("postID", EventID.toString());
                    context.startActivity(intent);

                }
            });

            shareButton = (ImageButton) view.findViewById(R.id.user_event_share_button);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: increment shares, implement sharing
                }
            });

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
                public void onCancelled(DatabaseError databaseError) {}
            });
        }


}




    public class CityAdapter extends ArrayAdapter<City> {
        CityAdapter(Context context, ArrayList<City> cities){
            super(context, R.layout.city_layout, R.id.city_name, cities);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            final City city = getItem(position);
            TextView cityName = (TextView)convertView.findViewById(R.id.city_name);
            ImageView cityImage = (ImageView) convertView.findViewById(R.id.city_image);
            TextView cityLocation = (TextView)convertView.findViewById(R.id.city_location);

            cityName.setText(city.CityName);
            cityLocation.setText(city.CityName + ", " + city.State + ", " + city.Country);

            cityName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),CityEventsActivity.class);
                    intent.putExtra("CityName",city.CityName);
                    startActivity(intent);
                }
            });
            cityImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),CityEventsActivity.class);
                    intent.putExtra("CityName",city.CityName);
                    startActivity(intent);
                }
            });
            try {
                if (city.Picture != null)
                    Picasso.with(getContext()).load(city.Picture).into(cityImage);
                    //cityImage.setImageBitmap(getBitmapFromURL(city.Picture));
                else
                    Picasso.with(getContext()).load(R.drawable.cityscape).into(cityImage);
                //cityImage.setImageResource(R.drawable.cityscape);
            }catch (Exception e){}
            return convertView;
        }
    }

    public void queryCityEvents()
    {
        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("City").orderByChild("CityName").startAt("A").endAt("Z").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                    City cty = dataSnapshot.getValue(City.class);
                    cityArray.add(cty);
                }
                cityAdapter = new CityAdapter(getContext(),cityArray);
                cityList.setAdapter(cityAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void showProgressDialog()
    {
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();
    }
}
