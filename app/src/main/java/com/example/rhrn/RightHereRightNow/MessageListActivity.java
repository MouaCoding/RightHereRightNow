package com.example.rhrn.RightHereRightNow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.Messages;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static com.example.rhrn.RightHereRightNow.MapsFragment.getBitmapFromURL;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by Matt on 3/8/2017.
 */
public class MessageListActivity extends AppCompatActivity {
    private ImageView addMessage; // treating this as a button
    private ListView mListView; //List of messages
    private ArrayList<User> mUsers;
    private UserAdapter mAdapter;
    private ImageButton backButton, menuButton;
    public App mApp;
    Bundle extra;
    TextView messageView;
    private TextWatcher searchFriendsFilter;
    public EditText search;
    private static final int NEW_MESSAGE = 0;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging_list);

        menuButton = (ImageButton) findViewById(R.id.menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu();
            }
        });

        searchFriendsFilter = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {try{mAdapter.getFilter().filter(s);}catch (Exception e){}}
            @Override
            public void afterTextChanged(Editable s) {}
        };
        search = (EditText) findViewById(R.id.search_friends);
        search.addTextChangedListener(searchFriendsFilter);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // hide keyboard until clicked

        mApp = (App)getApplicationContext();
        mUsers = new ArrayList<>();
        mListView = (ListView)findViewById(R.id.message_list_view);

//        mListView.setAdapter(mAdapter);
        addMessage = (ImageView) findViewById(R.id.create_new_message);
        addMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewMessageActivity.class);
                startActivityForResult(intent,NEW_MESSAGE);
                //startActivity(intent);
            }
        });
        backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish the current activity
                finish();
            }
        });

        //Should display all the messages the user has
        extra = getIntent().getBundleExtra("extra");
        if(extra != null)
            getUsersMessaged();
        //TODO: Refresh list after messaging someone
        // else if(extra == null){
        //getCurrentUserInfo();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NEW_MESSAGE && resultCode == Activity.RESULT_OK) {
            //TODO: Update message list after user finishes chat activity
            //mAdapter = new UserAdapter(MessageListActivity.this, mUsers);
            //mAdapter.notifyDataSetChanged();
        }
    }

    public void getUsersMessaged()
    {
        final ArrayList<String> keys = (ArrayList<String>) extra.getSerializable("objects");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userKey = user.getUid();
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("User");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Iterates through Firebase database
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User other = userSnapshot.getValue(User.class);
                    //iterate through UsersMessaged list, if matched then add to list of users messaged
                    for(int i = 0; i < keys.size();i++){
                        if(TextUtils.equals(other.uid, keys.get(i))) {
                            mUsers.add(other);
                        }
                    }
                }
                mAdapter = new UserAdapter(MessageListActivity.this, mUsers);
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                        intent.putExtra(ChatActivity.RECEIVER_ID,mUsers.get(position).uid);
                        intent.putExtra("ReceiverName",mUsers.get(position).DisplayName);
                        //TODO: Add message preview
                        //intent.putExtra("MessageContent", messageContentHere);
                        startActivity(intent);
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Unable to retrieve the users.
            }
        });

    } //getAllUsers()

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
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }//getCurrentUserInfo()


    public static class UserAdapter extends ArrayAdapter<User> implements Filterable {

        private ArrayList<User> mUsers;
        private ArrayList<User> mUsersFilter;
        public TextView messageView;
        public TextView date;

        UserAdapter(Context context, ArrayList<User> users) {
            super(context, R.layout.conversation, R.id.username, users);
            mUsers = users;
            mUsersFilter = users;
            getFilter();
        }

        @Override
        public int getCount() {

            return mUsers.size();
        }

        //Get the data item associated with the specified position in the data set.
        @Override
        public User getItem(int position) {

            return mUsers.get(position);
        }

        //Get the row id associated with the specified position in the list.
        @Override
        public long getItemId(int position) {

            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
            convertView = super.getView(position, convertView, parent);
            User user = getItem(position);
            TextView nameView = (TextView) convertView.findViewById(R.id.username);
            messageView = (TextView) convertView.findViewById(R.id.message_conversation_preview);
            date = (TextView) convertView.findViewById(R.id.date);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.messaging_user_profile_picture);
            nameView.setText(user.DisplayName);
            setExtraValues(user.uid, fbuser.getUid());
            //LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) nameView.getLayoutParams();

            try {
                if (user.ProfilePicture != null)
                    Picasso.with(getContext()).load(user.ProfilePicture).into(imageView);
                    //imageView.setImageBitmap(getBitmapFromURL(user.ProfilePicture));
                else
                    Picasso.with(getContext()).load(R.mipmap.ic_launcher).into(imageView);
                //imageView.setImageResource(R.mipmap.ic_launcher);

            } catch (Exception e) {
            }
            //nameView.setLayoutParams(layoutParams);
            //previewMessage(user.uid);
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults results = new FilterResults();

                    //If there's nothing to filter on, return the original data for your list
                    if (charSequence != null && charSequence.length() > 0) {
                        ArrayList<User> filterList = new ArrayList<User>();
                        for (int i = 0; i < mUsersFilter.size(); i++) {

                            if (mUsersFilter.get(i).DisplayName.contains(charSequence)) {
                                filterList.add(mUsersFilter.get(i));
                            }
                        }


                        results.count = filterList.size();

                        results.values = filterList;

                    } else {

                        results.count = mUsersFilter.size();

                        results.values = mUsersFilter;

                    }

                    return results;
                }


                //Invoked in the UI thread to publish the filtering results in the user interface.
                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,
                                              FilterResults results) {

                    mUsers = (ArrayList<User>) results.values;

                    notifyDataSetChanged();


                }
            };
        }

        public void setExtraValues(final String receiverID, final String senderID)
        {
            String[] ids = {senderID, receiverID};
            Arrays.sort(ids);
            String conversationKey = ids[0] + ids[1];
            Log.d("MESSAGEKEY", conversationKey);
            DatabaseReference msgRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(conversationKey);
            msgRef.orderByChild("Sender ID").equalTo(senderID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                        Messages msg = messageSnapshot.getValue(Messages.class);
                        messageView.setText((String) messageSnapshot.child("Message").getValue());
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTime(msg.getDate());
//                        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
//                        String month_name = month_date.format(calendar.getTime());
//                        date.setText(month_name + " " + calendar.DAY_OF_MONTH);
                        //Calendar myCal = new GregorianCalendar();
                        //myCal.setTime(msg.getDate());
                        date.setText((String) messageSnapshot.child("Date").getValue());
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }

    }

    private void popupMenu()
    {
        PopupMenu popup = new PopupMenu(MessageListActivity.this, menuButton);
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
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ); // Clear all activities above it
                    startActivity(intent);
                    finish();
                    return true;
                }
                else {
                    return onMenuItemClick(item);
                }
            }
        });
        popup.show();
    }


    /*
    public static class ConversationAdapter extends ArrayAdapter<Messages> implements Filterable {

        private ArrayList<Messages> mUsers;
        private ArrayList<Messages> mUsersFilter;

        ConversationAdapter(Context context, ArrayList<Messages> users) {
            super(context, R.layout.conversation, R.id.username, users);
            mUsers = users;
            mUsersFilter = users;
            getFilter();
        }

        @Override
        public int getCount() {

            return mUsers.size();
        }

        //Get the data item associated with the specified position in the data set.
        @Override
        public Messages getItem(int position) {

            return mUsers.get(position);
        }

        //Get the row id associated with the specified position in the list.
        @Override
        public long getItemId(int position) {

            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
            convertView = super.getView(position, convertView, parent);
            Messages msg = getItem(position);
            setExtraValues(msg.getReceiver(),msg.getSender());
            TextView nameView = (TextView) convertView.findViewById(R.id.user);
            TextView messageView = (TextView) convertView.findViewById(R.id.message_preview);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.messaging_profile_picture);
            nameView.setText(msg.DisplayName);
            //TODO: Populate the message preview with the most recent message
            //messageView.setText(user.FirstName); // placeholder for now...
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) nameView.getLayoutParams();

            try {
                if (msg.ProfilePicture != null)
                    Picasso.with(getContext()).load(msg.ProfilePicture).into(imageView);
                    //imageView.setImageBitmap(getBitmapFromURL(user.ProfilePicture));
                else
                    Picasso.with(getContext()).load(R.mipmap.ic_launcher).into(imageView);
                //imageView.setImageResource(R.mipmap.ic_launcher);

            } catch (Exception e) {
            }
            nameView.setLayoutParams(layoutParams);
            //previewMessage(user.uid);
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults results = new FilterResults();

                    //If there's nothing to filter on, return the original data for your list
                    if (charSequence != null && charSequence.length() > 0) {
                        ArrayList<Messages> filterList = new ArrayList<Messages>();
                        for (int i = 0; i < mUsersFilter.size(); i++) {

                            if (mUsersFilter.get(i).DisplayName.contains(charSequence)) {
                                filterList.add(mUsersFilter.get(i));
                            }
                        }


                        results.count = filterList.size();

                        results.values = filterList;

                    } else {

                        results.count = mUsersFilter.size();

                        results.values = mUsersFilter;

                    }

                    return results;
                }


                //Invoked in the UI thread to publish the filtering results in the user interface.
                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,
                                              FilterResults results) {

                    mUsers = (ArrayList<Messages>) results.values;

                    notifyDataSetChanged();


                }
            };
        }

        public void setExtraValues(final String receiverID, final String senderID)
        {
            String[] ids = {senderID, receiverID};
            Arrays.sort(ids);
            final String conversationKey = ids[0] + ids[1];
            final DatabaseReference msgRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(conversationKey);
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(receiverID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User receiver = dataSnapshot.getValue(User.class);
                    msgRef.child("Messages").child(conversationKey).child("DisplayName").setValue(receiver.DisplayName);
                    msgRef.child("Messages").child(conversationKey).child("handle").setValue(receiver.handle);
                    try{
                        msgRef.child("Messages").child(conversationKey).child("ProfilePicture").setValue(receiver.ProfilePicture);
                    }catch (Exception e){}
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }*/




}
