package com.example.rhrn.RightHereRightNow;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.authentication.Constants;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

//import static com.example.rhrn.RightHereRightNow.Messages.STATUS_SENT;
import static java.security.AccessController.getContext;

/**
 * Created by Matt on 3/2/2017.
 */

public class Chat extends AppCompatActivity implements View.OnClickListener,
        MessageSource.MessagesCallbacks{

    public static final String USER_EXTRA = "USER";
    public static final String TAG = "ChatActivity";
    private ArrayList<Messages> mMessages;
    private ArrayList<User> mUsers;
    private MessagesAdapter mAdapter;
    public String mRecipient;
    private ListView mListView;
    private Date mLastMessageDate = new Date();
    private String mConvoId;
    private MessageSource.MessagesListener mListener;

    private DatabaseReference users;
    public String key;


    //User (Sender)
    public User sender;
    public User receiver;
    public FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_thread);

        sender = new User();
        mListView = (ListView)findViewById(R.id.message_list);
        mMessages = new ArrayList<>();
        mUsers = new ArrayList<>();
        mAdapter = new MessagesAdapter(mMessages);
        mListView.setAdapter(mAdapter);
        //setTitle(mRecipient);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Button sendMessage = (Button)findViewById(R.id.send_message);
        sendMessage.setOnClickListener(this);

        mRecipient = getKeyFromFB();
        getAllUsers();

        //Establish a link between two ids, this link will not only be unique
        //but will continue if both users continue chatting, could use a smaller key though...
        //Maybe prefix the keys? -> append the first 10 letters of the uids
        //TODO: find a way to get the receiver's id so we can store their info..., restore the comments later
        if(Objects.equals(mRecipient,"izkeNH4WZycI9OnkFqnTQLfirY93"))
            mConvoId = mRecipient+"eWT87QuPHNVzgvBPgO2EBAaswqe2";
        else
            mConvoId = "izkeNH4WZycI9OnkFqnTQLfirY93"+mRecipient;
        //String[] ids = {mRecipient, "lWu5KorihgeSSO21xWXMunq25Cl2"};
        //Arrays.sort(ids);
        //If there are Group chats, then add their uids together
        //mConvoId = ids[0]+ids[1];

        mListener = MessageSource.addMessagesListener(mConvoId, this);
    }

    public void onClick(View v) {
        EditText newMessageView = (EditText)findViewById(R.id.new_message);
        String newMessage = newMessageView.getText().toString();
        newMessageView.setText("");
        Messages msg = new Messages();
        msg.setDate(new Date());
        msg.setMessage(newMessage);
        //set the sender to the sender's id
        msg.setSender(mRecipient);
        MessageSource.saveMessage(msg, mConvoId);
    }

    @Override
    public void onMessageAdded(Messages message) {
        mMessages.add(message);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageSource.stop(mListener);
    }

    public class MessagesAdapter extends ArrayAdapter<Messages> {
        MessagesAdapter(ArrayList<Messages> messages){

            super(Chat.this, R.layout.msg_item, R.id.msg, messages);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
            convertView = super.getView(position, convertView, parent);
            Messages message = getItem(position);
            TextView nameView = (TextView)convertView.findViewById(R.id.msg);
            nameView.setText(message.getMessage());
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();
            int sdk = android.os.Build.VERSION.SDK_INT;
            //if the sender matches the id of the sender, then bubble appears right side and green
            if ( message.getSender().equals(fbuser.getUid())){
                    nameView.setBackground(getDrawable(R.drawable.bubble_right_green));
                    layoutParams.gravity = Gravity.END;
            //else it appears left side and gray
                }else{
                    nameView.setBackground(getDrawable(R.drawable.bubble_left_gray));
                    layoutParams.gravity = Gravity.LEFT;
                }
            nameView.setLayoutParams(layoutParams);
            return convertView;
        }
    }

    public String getKeyFromFB()
    {
        user = FirebaseAuth.getInstance().getCurrentUser();
        users = FirebaseDatabase.getInstance().getReference("User");
        //found user by user's unique id, can now link to the sender.
        users.getRef().orderByChild("Email").equalTo(user.getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    //I queried correctly, but i cant seem to set it to some value and return it... - matt
                    sender = userSnapshot.getValue(User.class);
                    key = userSnapshot.getKey();
                    //Log.d(sender.id,"SENDER ID!");
                    //Log.d(key,"key !");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        return user.getUid();
    }



    //This function is used to get all the list of users except the current user
    public void getAllUsers()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userKey = user.getUid();
        FirebaseDatabase.getInstance().getReference().child("User")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            User other = userSnapshot.getValue(User.class);
                            if (!TextUtils.equals(other.uid, userKey)) {
                                mUsers.add(other);
                                Log.d(other.id,"OTHER ID");
                                //TODO: find the person the current user wants to talk to.
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to retrieve the users.
                    }
                });
    } //getAllUsers()

}
