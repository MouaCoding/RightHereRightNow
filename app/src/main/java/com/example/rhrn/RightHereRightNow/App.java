package com.example.rhrn.RightHereRightNow;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Matt on 3/2/2017.
 */

public class App extends Application {
    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

}