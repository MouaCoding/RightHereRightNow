package com.example.rhrn.RightHereRightNow;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static java.security.AccessController.getContext;


public class MoreEventsActivity extends AppCompatActivity {
    public ImageButton backButton, options;
    public TextView eventTitle;
    public ListView eventList;
    public ArrayList<Event> eventArrayList;
    public TrendingFragment.EventAdapter eventAdapter;

    public ProgressBar loadMoreEvents;

    public int loadEvents = 0;
    public int scrollCount = 0;
    int first=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_events);
        backButton = (ImageButton) findViewById(R.id.back_button);
        options = (ImageButton) findViewById(R.id.profile_app_bar_options);
        eventTitle = (TextView) findViewById(R.id.profile_name_chat);
        eventList = (ListView) findViewById(R.id.user_all_events);
        eventArrayList = new ArrayList<>();
        eventAdapter = new TrendingFragment.EventAdapter(this, eventArrayList);
        eventList.setAdapter(eventAdapter);
        //loadMoreEvents = (ProgressBar) LayoutInflater.from(this).inflate(R.layout.progress_bar, null);
        //findViewById(R.id.load_more_events);
        View mProgressBarFooter = ((LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.progress_bar, null, false);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        eventList.addFooterView(mProgressBarFooter);
        eventList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if(findViewById(R.id.load_more_posts).isShown())//.getVisibility() == View.VISIBLE)
                {
                    loadEvents=0;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                if(loadEvents == 0)
                {
                    loadEvents = 1;
                    scrollCount++;
                    eventArrayList = new ArrayList<Event>();
                    try{getUserEvents(scrollCount*25);}catch (Exception e){}
                    eventAdapter.notifyDataSetChanged();
                    Log.i("counttt", Integer.toString(scrollCount));
                }



            }
        });


    }

    public void getUserEvents(final int n)
    {
        //final int numEventsToLoad = 3;
        //final int first = 0;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Event");
        ref.orderByChild("ownerID").equalTo(getIntent().getStringExtra("userKey")).limitToLast(n).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    eventArrayList.add(0,dataSnapshot1.getValue(Event.class));
                }
                eventTitle.setText(eventArrayList.get(0).DisplayName+"'s Events");
                eventAdapter = new TrendingFragment.EventAdapter(MoreEventsActivity.this, eventArrayList);
                eventList.setAdapter(eventAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
