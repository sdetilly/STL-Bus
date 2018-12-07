package com.tilly.steven.stlbusarrivals.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.tilly.steven.stlbusarrivals.R
import com.tilly.steven.stlbusarrivals.ui.fragments.DetailsFragment
import com.tilly.steven.stlbusarrivals.ui.fragments.MapsFragment

class StopDetailsActivity : AppCompatActivity() {
    lateinit var adapterViewPager: FragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_details)

        val vpPager = findViewById<View>(R.id.pager_details) as ViewPager
        adapterViewPager = MyPagerAdapter(supportFragmentManager)
        vpPager.adapter = adapterViewPager

        val extras = intent.extras
        routeTag = extras?.getString("routeTag")
        stopId = extras?.getString("stopId")
        stopName = extras?.getString("stopName")
        routeName = extras?.getString("routeName")

        setTitle(R.string.stop_details)
    }

    inner class MyPagerAdapter(fragmentManager: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fragmentManager) {
        private val NUM_ITEMS = 2

        private val tabTitles = arrayOf(getString(R.string.details), getString(R.string.map))

        // Returns total number of pages
        override fun getCount(): Int {
            return NUM_ITEMS
        }

        // Returns the fragment to display for that page

        override fun getItem(position: Int): androidx.fragment.app.Fragment? {
            when (position) {
                0 // Fragment # 0 - This will show FavoritesFragment
                -> {
                    val detailsFragment = DetailsFragment()
                    val bundle = Bundle()
                    bundle.putString("stopId", stopId)
                    bundle.putString("routeTag", routeTag)
                    bundle.putString("stopName", stopName)
                    bundle.putString("routeName", routeName)
                    detailsFragment.arguments = bundle
                    return detailsFragment
                }
                1 // Fragment # 1 - This will show RouteSearchFragment
                -> {
                    val mapsFragment = MapsFragment()
                    val bundleMaps = Bundle()
                    bundleMaps.putString("routeTag", routeTag)
                    mapsFragment.arguments = bundleMaps
                    return mapsFragment
                }
                else -> return null
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            // Generate title based on item position
            return tabTitles[position]
        }
    }

    companion object {

        internal var routeTag: String? = null
        internal var stopId: String? = null
        internal var stopName: String? = null
        internal var routeName: String? = null
    }
}
