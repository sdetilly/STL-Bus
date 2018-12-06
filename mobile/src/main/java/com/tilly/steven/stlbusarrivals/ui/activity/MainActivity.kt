package com.tilly.steven.stlbusarrivals.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.tilly.steven.stlbusarrivals.R
import com.tilly.steven.stlbusarrivals.ui.fragments.FavoritesFragment
import com.tilly.steven.stlbusarrivals.ui.fragments.RouteSearchFragment

class MainActivity : AppCompatActivity() {

    lateinit var adapterViewPager: androidx.fragment.app.FragmentPagerAdapter
    lateinit var vpPager: androidx.viewpager.widget.ViewPager
    var vpagerItem: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("onCreate", "OK")
        MobileAds.initialize(applicationContext, getString(R.string.banner_ad_unit_id))
        val mAdView = findViewById<AdView>(R.id.adView)
        //val adRequest = AdRequest.Builder().build() //REAL ads
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("F7DD4649A4F0FA78878A1E9BA595B8C6")  // My OnePlus 3
                .build()
        mAdView.loadAd(adRequest)

        vpPager = findViewById(R.id.pager_main)

        adapterViewPager = MyPagerAdapter(supportFragmentManager)
        if (savedInstanceState != null) {
            vpPager.currentItem = savedInstanceState.getInt("currentItem")
            Log.d("MainAct loading...", "" + savedInstanceState.getInt("currentItem"))
        }
        vpPager.adapter = adapterViewPager
        vpPager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {

            override fun onPageSelected(pos: Int) {
                vpagerItem = pos
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}

            override fun onPageScrollStateChanged(arg0: Int) {}
        })
    }


    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt("currenItem", vpagerItem)
        Log.d("MainAct saving...", "" + vpagerItem)
    }


    inner class MyPagerAdapter(fragmentManager: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fragmentManager) {
        private val NUM_ITEMS = 2

        private val tabTitles = arrayOf(getString(R.string.favorites), getString(R.string.search))

        // Returns total number of pages
        override fun getCount(): Int {
            return NUM_ITEMS
        }

        // Returns the fragment to display for that page

        override fun getItem(position: Int): androidx.fragment.app.Fragment? {
            when (position) {
                0 // Fragment # 0 - This will show FavoritesFragment
                -> return FavoritesFragment()
                1 // Fragment # 1 - This will show RouteSearchFragment
                -> return RouteSearchFragment()
                else -> return null
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            // Generate title based on item position
            return tabTitles[position]
        }
    }
}
