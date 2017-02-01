package com.example.matt.RightHereRightNow;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem bottom_navigation) {
                        switch (bottom_navigation.getItemId()) {
                            case R.id.map_pin:
                                //do something when user clicks on each
                                Intent intent1 = new Intent (getApplicationContext(), MapsActivity.class);
                                startActivity(intent1);
                                break;
                            case R.id.megaphone:
                                Intent intent2 = new Intent (getApplicationContext(), PostActivity.class);
                                startActivity(intent2);
                                break;
                            case R.id.menu:

                                break;
                            case R.id.music_social_group:
                                Intent intent4 = new Intent (getApplicationContext(), SocialActivity.class);
                                startActivity(intent4);
                                break;
                            case R.id.identity_card:
                                Intent intent5 = new Intent (getApplicationContext(), MainActivity.class);
                                startActivity(intent5);
                                break;
                        }
                        return true;
                    }
                });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
