package com.example.rhrn.RightHereRightNow;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.example.rhrn.RightHereRightNow.firebase_entry.Shares;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MoreSharedPostsActivity extends AppCompatActivity {
    public ImageButton backButton, options;
    public TextView postTitle;
    public ListView postList;
    public ArrayList<Post> postArrayList;
    public NotificationFragment.PostAdapter postAdapter;
    public ProgressBar loadMorePosts;
    public int loadPosts = 0;
    public int scrollCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_posts);
        backButton = (ImageButton) findViewById(R.id.back_button);
        options = (ImageButton) findViewById(R.id.profile_app_bar_options);
        postTitle = (TextView) findViewById(R.id.profile_name_chat);
        postList = (ListView) findViewById(R.id.user_all_posts);
        postArrayList = new ArrayList<>();
        postAdapter = new NotificationFragment.PostAdapter(this, postArrayList);
        postList.setAdapter(postAdapter);
        //loadMorePosts = (ProgressBar) LayoutInflater.from(this).inflate(R.layout.progress_bar, null);
        //findViewById(R.id.load_more_posts);
        View mProgressBarFooter = ((LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.progress_bar, null, false);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        postList.addFooterView(mProgressBarFooter);
        postList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (findViewById(R.id.load_more_posts).isShown())
                    loadPosts = 0;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (loadPosts == 0) {
                    loadPosts = 1;
                    scrollCount++;
                    postArrayList = new ArrayList<Post>();
                    try {
                        getUserPosts(scrollCount * 25);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    postAdapter.notifyDataSetChanged();
                }
            }
        });


    }

    public void getUserPosts(final int n) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Shares").child(getIntent().getStringExtra("userKey"));
        ref.orderByChild("type").equalTo("Post").limitToLast(n).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Shares share = dataSnapshot1.getValue(Shares.class);
                    Post.requestPost(share.id, "auth", new Post.PostReceivedListener() {
                        @Override
                        public void onPostReceived(Post... posts) {
                            android.util.Log.e("nat", posts[0].ownerID);
                            postArrayList.add(posts[0]);
                            android.util.Log.e("nat", String.valueOf(postArrayList.size()));

                        }
                    });

                }

                try {
                    postTitle.setText(postArrayList.get(0).DisplayName + "'s Posts");
                    postAdapter = new NotificationFragment.PostAdapter(MoreSharedPostsActivity.this, postArrayList);
                    android.util.Log.e("nat", String.valueOf(postAdapter.getCount()));
                    postList.setAdapter(postAdapter);
                } catch (Exception e) {
                    android.util.Log.e("nat", e.toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
