package com.example.rhrn.RightHereRightNow;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Bradley Wang on 2/13/2017.
 */
public class ProfilePageFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = inflater.inflate(R.layout.profile_page_layout, container, false);

        // restore any state here if necessary

        return r;
    }
}
