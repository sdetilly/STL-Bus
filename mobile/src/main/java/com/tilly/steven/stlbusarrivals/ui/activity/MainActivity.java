package com.tilly.steven.stlbusarrivals.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tilly.steven.stlbusarrivals.R;
import com.tilly.steven.stlbusarrivals.ui.fragments.FavoritesFragment;
import com.tilly.steven.stlbusarrivals.ui.fragments.RouteSearchFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    FragmentPagerAdapter adapterViewPager;
    ViewPager vpPager;
    int vpagerItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("onCreate", "OK");
        MobileAds.initialize(getApplicationContext(), getString(R.string.banner_ad_unit_id));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build(); //REAL ads
        /*AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("F7DD4649A4F0FA78878A1E9BA595B8C6")  // My OnePlus 3
                .build();*/
        mAdView.loadAd(adRequest);

        vpPager = (ViewPager) findViewById(R.id.pager_main);

        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        if (savedInstanceState != null) {
            vpPager.setCurrentItem(savedInstanceState.getInt("currentItem"));
            Log.d("MainAct loading...", ""+ savedInstanceState.getInt("currentItem"));
        }
        vpPager.setAdapter(adapterViewPager);
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int pos) {
                vpagerItem = pos;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }




    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("currenItem", vpagerItem);
        Log.d("MainAct saving...", "" + vpagerItem);
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 2;

        private String tabTitles[] = new String[] { getString(R.string.favorites), getString(R.string.search) };

        public MyPagerAdapter(android.support.v4.app.FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FavoritesFragment
                    return new FavoritesFragment();
                case 1: // Fragment # 1 - This will show RouteSearchFragment
                    return new RouteSearchFragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }
}
