package com.mouaincorporate.matt.MapConnect.notifications;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class RHRNMessagingService  extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);

//        Toast.makeText(this, message.getNotification().getBody(), Toast.LENGTH_LONG).show();
    }
    //
}
