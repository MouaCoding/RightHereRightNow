package com.example.rhrn.RightHereRightNow;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.firebase_entry.Likes;
import com.example.rhrn.RightHereRightNow.firebase_entry.Post;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.example.rhrn.RightHereRightNow.util.CircleTransform;
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
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Matt on 4/2/2017.
 */

public class NotificationFragment extends Fragment {
    static App app = (App) getApplicationContext();
    public Button following, you;
    public EditText search;
    private TextWatcher searchFriendsFilter, searchPostsByFriends;
    private ArrayList<Post> mPosts;
    private ArrayList<User> mUsers;
    private static PostAdapter mAdapter;
    private MessageListActivity.UserAdapter mmAdapter;
    private ListView list, userList;
    NotificationManager notificationManager;
    int notifyFlag=0;
    int numUsersFollowed = 0;

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

        private ImageButton likeButton;
        private ImageButton commentButton;
        private ImageButton shareButton;
        private ImageButton options;
        int postDeleted = 0;

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
            ImageView miniProfilePicView = (ImageView) convertView.findViewById(R.id.mini_profile_picture);;
            TextView displayNameView= (TextView) convertView.findViewById(R.id.mini_name);
            TextView userHandleView= (TextView) convertView.findViewById(R.id.mini_user_handle);
            TextView numLikes = (TextView) convertView.findViewById(R.id.user_post_like_count);
            TextView numComments = (TextView) convertView.findViewById(R.id.user_post_comment_count);

            setButtons(convertView, post.postID, post.ownerID);
            if(postDeleted == 0)
                setExtraValues(post.postID, post.ownerID);

            displayNameView.setText(post.DisplayName);
            userHandleView.setText(post.handle);
            postBodyTextView.setText(post.content);
            numLikes.setText(Integer.toString(post.likes));
            numComments.setText(Integer.toString(post.comments));
            try {
                if (post.ProfilePicture != null)
                    Picasso.with(getContext()).load(post.ProfilePicture).transform(new CircleTransform()).into(miniProfilePicView);
                else
                    Picasso.with(getContext()).load(R.mipmap.ic_launcher).transform(new CircleTransform()).into(miniProfilePicView);
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

        public void setButtons(final View view, final String postID, final String ownerID)
        {
            likeButton = (ImageButton) view.findViewById(R.id.user_post_like_button);
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            commentButton = (ImageButton) view.findViewById(R.id.user_post_comment_button);
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            shareButton = (ImageButton) view.findViewById(R.id.user_post_share_button);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: increment shares, implement sharing
                }
            });

            options = (ImageButton) view.findViewById(R.id.mini_profile_more_button);
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu(view, ownerID, postID);
                }
            });

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

        public void setExtraValues(final String postID, final String ownerID)
        {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("User").child(ownerID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User owner = dataSnapshot.getValue(User.class);
                    ref.child("Post").child(postID).child("DisplayName").setValue(owner.DisplayName);
                    ref.child("Post").child(postID).child("handle").setValue(owner.handle);
                    try{
                        ref.child("Post").child(postID).child("ProfilePicture").setValue(owner.ProfilePicture);
                    }catch (Exception e){}
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }

        public void popupMenu(View view, final String ownerID, final String postID)
        {
//            MenuItem menuItem = (MenuItem) view.findViewById(R.id.delete);
            options = (ImageButton) view.findViewById(R.id.mini_profile_more_button);
            final PopupMenu popup = new PopupMenu(view.getContext(), options);
            popup.getMenuInflater().inflate(R.menu.post_options, popup.getMenu());
            if(String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid()).equals(ownerID))
                popup.getMenu().findItem(R.id.delete).setVisible(true);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    int i = item.getItemId();
                    if (i == R.id.delete) {
                        promptDelete(ownerID, postID);
                        return true;
                    }
                    if (i == R.id.report_post) {
                        Toast.makeText(getApplicationContext(),"Reporting Post...",Toast.LENGTH_SHORT).show();
                        reportPost(ownerID, postID);
                        return true;
                    }
                    else {
                        return onMenuItemClick(item);
                    }
                }
            });
            popup.show();
        }

        public void promptDelete(final String ownerID, final String postID)
        {
            android.support.v7.app.AlertDialog.Builder dlgAlert = new android.support.v7.app.AlertDialog.Builder(getContext());
            dlgAlert.setMessage("Are you sure you want to delete this post? This action cannot be undone!");
            dlgAlert.setTitle("Delete Post?");

            dlgAlert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Perform delete
                    Toast.makeText(getContext(), "Deleting Post...", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference().child("Post").child(postID).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("PostLocations").child(postID).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("NotificationRequest").child(ownerID).child(postID).removeValue();
                    Toast.makeText(getContext(), "Post Deleted!", Toast.LENGTH_SHORT).show();

                    postDeleted = 1;
                    mAdapter.notifyDataSetChanged();
                    //TODO: update likes received...
                }
            });

            //if user cancels
            dlgAlert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });

            dlgAlert.setCancelable(true);
            dlgAlert.create();
            dlgAlert.show();
        }

        public void reportPost(final String ownerID, final String postID)
        {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Post");
            ref.child(postID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if((String) dataSnapshot.child("content").getValue() == null) return;
                    else{
                        if(!dataSnapshot.child("numberOfReports").exists())
                            ref.child(postID).child("numberOfReports").setValue(0);
                        else {
                            long numberOfReports = (long) dataSnapshot.child("numberOfReports").getValue();
                            //parse whitespace
                            String[] content = ((String) dataSnapshot.child("content").getValue()).split("\\s+");
                            if (hasBadWord(content)) {
                                numberOfReports++;
                                ref.child(postID).child("numberOfReports").setValue(numberOfReports);
                                //TODO: set the amount of reports before a post is deleted
                                if(numberOfReports > 5) {
                                    FirebaseDatabase.getInstance().getReference().child("Post").child(postID).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("PostLocations").child(postID).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("NotificationRequest").child(ownerID).child(postID).removeValue();
                                }
                            } //Has bad word
                        }//else number of reports exists
                    }//else post has content
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public boolean hasBadWord(String[] content)
        {
            int i = 0;
            for(String badWord : app.badWords){
                content[i] = content[i].toLowerCase();
                if(content[i].contains(badWord)) {
                    Toast.makeText(getContext(), "Post has been reported.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                i++;
            }
            Toast.makeText(getContext(), "There is nothing to report.", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public void getPosts(String following)
    {
        if(following == null)
            return;

        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Post");
        postRef.orderByChild("ownerID").equalTo(following).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(!dataSnapshot.exists())
                    return;
                else {
                    Post posts = dataSnapshot.getValue(Post.class);
                    //TODO: since this saves all posts by each user, it might be inefficient... MM
                    DatabaseReference notifyRequest = FirebaseDatabase.getInstance().getReference().child("NotificationRequest");
                    DatabaseReference curUser = notifyRequest.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    curUser.child(posts.postID).setValue(posts);
                }
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
                if(!dataSnapshot.exists())
                    return;
                else {
                    if (notifyFlag == 0)
                        getPosts(dataSnapshot.getKey().toString());
                    else if (notifyFlag == 1)
                        getUsers(dataSnapshot.getKey().toString());
                }
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
        if(following == null)
            return;

        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User");
        userRef.child(following).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) return;
                else {
                    final User usr = dataSnapshot.getValue(User.class);
                    mUsers.add(usr);

                    try{
                        mmAdapter = new MessageListActivity.UserAdapter(getContext(), mUsers);
                        list.setAdapter(mmAdapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getContext(), ViewUserActivity.class);
                                intent.putExtra("otherUserID", mUsers.get(position).uid);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getContext().startActivity(intent);
                            }
                        });
                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("NumberFollowing").setValue(mUsers.size());
                    }catch(Exception e){}
                }
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
                if(!dataSnapshot.exists()) return;
                else {
                    Post posts = dataSnapshot.getValue(Post.class);
                    try {
                        mPosts.add(0, posts);
                        mAdapter = new PostAdapter(getContext(), mPosts);
                        list.setAdapter(mAdapter);
                        //TODO Not working....... - MM
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getActivity(),ViewPostActivity.class);
                                intent.putExtra("postid",mPosts.get(position).postID);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getContext().startActivity(intent);
                            }
                        });
                    } catch (Exception e) {}
                }
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

