package com.example.rhrn.RightHereRightNow.custom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rhrn.RightHereRightNow.R;

/**
 * Created by Bradley Wang on 3/6/2017.
 */

public class UserMiniHeaderView extends FrameLayout {
    private ImageView miniProfilePicView;
    private TextView displayNameView;
    private TextView userHandleView;
    private ImageButton moreButton;
    private ImageButton addButton;

    public UserMiniHeaderView(Context context) {
        super(context);
        createView();
    }

    public UserMiniHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView();
    }

    public void createView() {
        inflate(getContext(), R.layout.user_mini_header_layout, this);
        miniProfilePicView = (ImageView) findViewById(R.id.mini_profile_picture);
        displayNameView = (TextView) findViewById(R.id.mini_name);
        userHandleView = (TextView) findViewById(R.id.mini_user_handle);
        moreButton = (ImageButton) findViewById(R.id.mini_profile_more_button);
        addButton = (ImageButton) findViewById(R.id.mini_profile_add_button);
    }

    public void getUser(String userID) {
        // TODO use params to get user data and fill fields.
    }
}
