package com.example.rhrn.RightHereRightNow;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.custom.view.UserEventView;
import com.example.rhrn.RightHereRightNow.custom.view.UserPostView;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Bradley Wang on 3/6/2017.
 */

public class ViewEventDialogFragment extends DialogFragment {
    UserEventView eventView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = inflater.inflate(R.layout.event_view_dialog_layout, container, false);

        eventView = (UserEventView) r.findViewById(R.id.user_event);
        eventView.getEvent(getArguments().getString("event_id")); // Can be modified

        return r;
    }

    public static ViewEventDialogFragment createInstance(String eventID) {
        Bundle args = new Bundle();
        args.putString("event_id", eventID);
        ViewEventDialogFragment r = new ViewEventDialogFragment();
        r.setArguments(args);

        return r;
    }
}
