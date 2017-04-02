package com.example.rhrn.RightHereRightNow;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Matt on 4/2/2017.
 */

public class TrendingFragment extends Fragment {

    public Button global, city;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = (View) inflater.inflate(R.layout.trending_posts, container, false);

        global = (Button) r.findViewById(R.id.global_button);
        global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display global list of events/posts
            }
        });
        city = (Button) r.findViewById(R.id.city_button);
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display city list of events/posts
            }
        });


        return r;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
