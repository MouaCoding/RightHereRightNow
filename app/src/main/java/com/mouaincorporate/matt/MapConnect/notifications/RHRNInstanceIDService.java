package com.mouaincorporate.matt.MapConnect.notifications;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class RHRNInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseInstanceId.getInstance().getToken();
        // TODO put token in list of logged in tokens
    }
    //
}
