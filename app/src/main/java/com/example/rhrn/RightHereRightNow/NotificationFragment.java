package com.example.rhrn.RightHereRightNow;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Matt on 4/2/2017.
 */

public class NotificationFragment extends Fragment {

    public Button following, you;
    public EditText search;
    private TextWatcher searchFriendsFilter, searchPostsByFriends;
    private ArrayList<Post> mPosts;
    private ArrayList<User> mUsers;
    private PostAdapter mAdapter;
    private MessageListActivity.UserAdapter mmAdapter;
    private ListView list, userList;
    NotificationManager notificationManager;
    int notifyFlag=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = (View) inflater.inflate(R.layout.user_notification, container, false);

        notificationManager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);

        following = (Button) r.findViewById(R.id.following_button);
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosts = new ArrayList<>();
                mUsers = new ArrayList<>();
                mAdapter = new PostAdapter(getContext(), mPosts);
                list.setAdapter(mAdapter);
                notifyFlag = 0;
                getPostsNotifications();//getUsers();
            }
        });
        you = (Button) r.findViewById(R.id.you_button);
        you.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosts = new ArrayList<>();
                mUsers = new ArrayList<>();
                mAdapter = new PostAdapter(getContext(), mPosts);
                list.setAdapter(mAdapter);
                notifyFlag = 1;
                getUsersFollowed();
            }
        });
        searchFriendsFilter = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {try{mmAdapter.getFilter().filter(s);}catch (Exception e){}}
            @Override
            public void afterTextChanged(Editable s) {}
        };
        searchPostsByFriends = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {try{mAdapter.getFilter().filter(s);}catch (Exception e){}}
            @Override
            public void afterTextChanged(Editable s) {}
        };
        search = (EditText) r.findViewById(R.id.search_friends);
        search.addTextChangedListener(searchFriendsFilter);
        search.addTextChangedListener(searchPostsByFriends);
        /*search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search.clearFocus();
                    InputMethodManager in = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(search.getWindowToken(), 0);
                    String searchedTerm = search.getText().toString().trim();
                    performSearch(searchedTerm);
                    return true;
                }
                return false;
            }
        });*/

        mAdapter = new PostAdapter(getContext(),mPosts);
        mPosts = new ArrayList<Post>();
        mUsers = new ArrayList<>();
        list = (ListView) r.findViewById(R.id.global_list);
        userList = (ListView) r.findViewById(R.id.global_list);

        //getPosts();
        getUsersFollowed();
        getPostsNotifications();



        return r;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static class PostAdapter extends ArrayAdapter<Post> {
        private ArrayList<Post> mPosts;
        private ArrayList<Post> mPostsFilter;

        PostAdapter(Context context, ArrayList<Post> users){
            super(context, R.layout.user_post_framed_layout/*user_item*/, R.id.mini_name, users);
            mPostsFilter = users;
            mPosts = users;
            getFilter();
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            final Post post = getItem(position);

            TextView postBodyTextView = (TextView) convertView.findViewById(R.id.user_post_body);
            ImageButton likeButton = (ImageButton) convertView.findViewById(R.id.user_post_like_button);
            ImageButton commentButton = (ImageButton) convertView.findViewById(R.id.user_post_comment_button);
            ImageButton shareButton = (ImageButton) convertView.findViewById(R.id.user_post_share_button);
            ImageView miniProfilePicView = (ImageView) convertView.findViewById(R.id.mini_profile_picture);;
            TextView displayNameView= (TextView) convertView.findViewById(R.id.mini_name);
            TextView userHandleView= (TextView) convertView.findViewById(R.id.mini_user_handle);
            TextView numLikes = (TextView) convertView.findViewById(R.id.number_likes);
            TextView numComments = (TextView) convertView.findViewById(R.id.number_comments);

            displayNameView.setText(post.DisplayName);
            userHandleView.setText(post.handle);
            postBodyTextView.setText(post.content);
            numLikes.setText(Integer.toString(post.likes));
            numComments.setText(Integer.toString(post.comments));
            try {
                if (post.ProfilePicture != null)
                    Picasso.with(getContext()).load(post.ProfilePicture).into(miniProfilePicView);
                    //miniProfilePicView.setImageBitmap(getBitmapFromURL(post.ProfilePicture));
                else
                    Picasso.with(getContext()).load(R.mipmap.ic_launcher).into(miniProfilePicView);
                    //miniProfilePicView.setImageResource(R.mipmap.ic_launcher);
            } catch(Exception e){}
            displayNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(),ViewPostActivity.class);
                    intent.putExtra("postid",post.postID);
                    getContext().startActivity(intent);
                }
            });
            miniProfilePicView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(),ViewPostActivity.class);
                    intent.putExtra("postid",post.postID);
                    getContext().startActivity(intent);
                }
            });
            return convertView;
        }
        @Override
        public int getCount() {

            return mPosts.size();
        }

        //Get the data item associated with the specified position in the data set.
        @Override
        public Post getItem(int position) {

            return mPosts.get(position);
        }

        //Get the row id associated with the specified position in the list.
        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults results = new FilterResults();

                    //If there's nothing to filter on, return the original data for your list
                    if (charSequence != null && charSequence.length() > 0) {
                        ArrayList<Post> filterList = new ArrayList<Post>();
                        for (int i = 0; i < mPostsFilter.size(); i++) {

                            if (mPostsFilter.get(i).DisplayName.contains(charSequence)) {
                                filterList.add(mPostsFilter.get(i));
                            }
                        }
                        results.count = filterList.size();
                        results.values = filterList;
                    } else {
                        results.count = mPostsFilter.size();
                        results.values = mPostsFilter;
                    }

                    return results;
                }

                //Invoked in the UI thread to publish the filtering results in the user interface.
                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,
                                              FilterResults results) {
                    mPosts = (ArrayList<Post>) results.values;
                    notifyDataSetChanged();
                }
            };
        }


    }

    public void getPosts(String following)
    {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Post");
        postRef.orderByChild("ownerID").equalTo(following).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post posts = dataSnapshot.getValue(Post.class);
                //TODO: since this saves all posts by each user, it might be inefficient... MM
                DatabaseReference notifyRequest = FirebaseDatabase.getInstance().getReference().child("NotificationRequest");
                DatabaseReference curUser = notifyRequest.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                curUser.child(posts.postID).setValue(posts);

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void getUsersFollowed()
    {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Following")
                .orderByChild("filler").getRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(notifyFlag == 0)
                    getPosts(dataSnapshot.getKey().toString());
                else if(notifyFlag == 1)
                    getUsers(dataSnapshot.getKey().toString());
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    public void getUsers(final String following)
    {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User");
        userRef.child(following).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User usr = dataSnapshot.getValue(User.class);
                mUsers.add(usr);
                mmAdapter = new MessageListActivity.UserAdapter(getContext(),mUsers);
                list.setAdapter(mmAdapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position
                        getContext().startActivity(intent);, long id) {
                            Intent intent = new Intent(getContext(), ViewUserActivity.class);
                            intent.putExtra("otherUserID",mUsers.get(position).uid);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void getPostsNotifications()
    {
        DatabaseReference notify = FirebaseDatabase.getInstance().getReference().child("NotificationRequest");
        notify.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                //.orderByChild("createDate").startAt("201701").endAt("201712").
                //Seems like firebase already sorts them in order of created date!
                .limitToLast(10).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post posts = dataSnapshot.getValue(Post.class);
                try {
                    mPosts.add(0,posts);
                    mAdapter = new PostAdapter(getContext(), mPosts);
                    list.setAdapter(mAdapter);
                }catch (Exception e){}
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void performSearch(String searchedTerm)
    {
        DatabaseReference notify = FirebaseDatabase.getInstance().getReference().child("");

    }

}

