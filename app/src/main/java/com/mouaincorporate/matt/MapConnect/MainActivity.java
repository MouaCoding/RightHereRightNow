package com.mouaincorporate.matt.MapConnect;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;


public class MainActivity extends AppCompatActivity {
    MainPagerAdapter pagerAdapter;
    ViewPager mainViewPager;
    public static PinpointManager pinpointManager; //AWS Analytics


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AWSMobileClient.getInstance().initialize(this).execute();

        PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                getApplicationContext(),
                AWSMobileClient.getInstance().getCredentialsProvider(),
                AWSMobileClient.getInstance().getConfiguration());

        pinpointManager = new PinpointManager(pinpointConfig);

        // Start a session with Pinpoint
        pinpointManager.getSessionClient().startSession();

        // Stop the session and submit the default app started event
        pinpointManager.getSessionClient().stopSession();
        pinpointManager.getAnalyticsClient().submitEvents();

        // populate fragments
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        // populate pager with fragments
        mainViewPager = (ViewPager) findViewById(R.id.main_content_view_pager);
        //mainViewPager.setOffscreenPageLimit(4);
        mainViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.main_bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem bottom_navigation) {
                        switch (bottom_navigation.getItemId()) {
                            case R.id.map_pin:
                                mainViewPager.setCurrentItem(0, true);
                                break;
                            case R.id.menu:
                                mainViewPager.setCurrentItem(1, true);
                                break;
                            case R.id.megaphone:
                                mainViewPager.setCurrentItem(2, true);
                                break;

//                            case R.id.music_social_group:
//                                mainViewPager.setCurrentItem(3, true);
//                                break;
                            case R.id.identity_card:
                                mainViewPager.setCurrentItem(3, true);
                                break;
                        }
                        return true;
                    }
                });
    }

    private class MainPagerAdapter extends FragmentStatePagerAdapter {
        private static final int NUM_PAGES = 4;

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MapsFragment();
                case 1:
                    return new NotificationFragment();
                case 2:
                    return new TrendingFragment();
//                case 3:
//                    return new NotificationFragment();
                case 3:
                    return new ProfilePageFragment();// return profile page fragment
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
