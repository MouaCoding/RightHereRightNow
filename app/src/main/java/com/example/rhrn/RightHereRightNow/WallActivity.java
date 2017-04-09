package com.example.rhrn.RightHereRightNow;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Bradley Wang on 4/6/2017.
 */

public class WallActivity extends FragmentActivity {
    String targetUserID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        targetUserID = getIntent().getStringExtra("userID");
        // get listview and set adapter
    }

    private static class WallAdapter extends BaseAdapter /*implements*/ {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            // TODO get what should go at position i
            // TODO reuse View view if possible
            return null;
        }
        //
    }
}
