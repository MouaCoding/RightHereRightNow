package com.example.rhrn.RightHereRightNow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.firebaseEntry.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Matt on 3/8/2017.
 */

public class MessageList extends AppCompatActivity {

    private ImageView addMessage; // treating this as a button
    private ListView mListView; //List of messages
    private ArrayList<User> mUsers;
    private UserAdapter mAdapter;
    private ImageButton backButton;
    public App mApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_list);
        mApp = (App)getApplicationContext();
        mUsers = new ArrayList<>();
        mListView = (ListView)findViewById(R.id.message_list_view);

//        mListView.setAdapter(mAdapter);
        addMessage = (ImageView) findViewById(R.id.create_new_message);
        addMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewMessage.class);
                startActivity(intent);
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
        /*mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: Check user id, then change activity and populate based on message between sender and rcver
                Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                startActivity(intent);
            }
        });*/


        //Should display all the messages the user has
        getAllUsers();
    }
    public void getAllUsers()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userKey = user.getUid();
        FirebaseDatabase.getInstance().getReference().child("User")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    //@Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Iterates through Firebase database
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            User other = userSnapshot.getValue(User.class);
                            //If the user is not themself, then add it to the list of other users
                            if (!TextUtils.equals(other.uid, userKey)) {
                                mUsers.add(other);
                            }
                        }
                        mAdapter = new UserAdapter(mUsers);
                        mListView.setAdapter(mAdapter);
                        //Log.d(mApp.getHandle(),"HANDLE");

                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //TODO: Check user id, then change activity and populate based on message between sender and rcver
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

    public class UserAdapter extends ArrayAdapter<User> {
        UserAdapter(ArrayList<User> users){

            super(MessageList.this, R.layout.user_item, R.id.user, users);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
            convertView = super.getView(position, convertView, parent);
            User user = getItem(position);
            TextView nameView = (TextView)convertView.findViewById(R.id.user);
            TextView messageView = (TextView)convertView.findViewById(R.id.message_preview);
            nameView.setText(user.DisplayName);
            //TODO: Populate the message preview with the most recent message
            messageView.setText(user.FirstName); // placeholder for now...
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();

            //nameView.setBackground(getDrawable(R.drawable.bubble_left_gray));
            //layoutParams.gravity = Gravity.LEFT;

            nameView.setLayoutParams(layoutParams);
            return convertView;
        }


    }


}
