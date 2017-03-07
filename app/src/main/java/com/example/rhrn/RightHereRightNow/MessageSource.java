package com.example.rhrn.RightHereRightNow;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.example.rhrn.RightHereRightNow.firebase_entry.Messages;
import com.firebase.client.Firebase;
import com.firebase.client.ChildEventListener;
import com.firebase.client.FirebaseError;


/**
 * Created by Matt on 3/2/2017.
 */
public class MessageSource {
    private static final Firebase sRef = new Firebase("https://righthererightnow-72e20.firebaseio.com/");
    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddmmss");
    private static final String TAG = "MessageDataSource";
    private static final String COLUMN_TEXT = "Message";
    private static final String COLUMN_SENDER = "Sender ID:";

    public static void saveMessage(Messages message, String conversationId) {

        Date date = message.getDate();

        String key = sDateFormat.format(date);

        HashMap<String, String> msg = new HashMap<>();
        msg.put(COLUMN_TEXT, message.getMessage());
        msg.put(COLUMN_SENDER, message.getSender());
        sRef.child("ChatActivity").child(conversationId).child(key).setValue(msg);
    }

    public static MessagesListener addMessagesListener(String convoId,  MessagesCallbacks messagesCallbacks) {
        MessagesListener listener = new MessagesListener(messagesCallbacks);
        sRef.child("ChatActivity").child(convoId).addChildEventListener(listener);
        return listener;
    }

    public static void stop(MessagesListener listener) {
        sRef.removeEventListener(listener);
    }



    public static class MessagesListener implements ChildEventListener {

        private MessagesCallbacks messagesCallbacks;

        MessagesListener(MessagesCallbacks callbacks) {
            messagesCallbacks = callbacks;
        }

        @Override
        public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
            HashMap<String, String> msg = (HashMap) dataSnapshot.getValue();
            Messages message = new Messages();
            message.setSender(msg.get(COLUMN_SENDER));
            message.setMessage(msg.get(COLUMN_TEXT));
            try {
                message.setDate(sDateFormat.parse(dataSnapshot.getKey()));
            } catch (ParseException e) {
                Log.d(TAG, "Something went wrong");
                e.printStackTrace();
            }

            if(messagesCallbacks != null) {
                messagesCallbacks.onMessageAdded(message);
            }
        }

        @Override
        public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }

    }

    public interface MessagesCallbacks {
        void onMessageAdded(Messages message);
    }

}
