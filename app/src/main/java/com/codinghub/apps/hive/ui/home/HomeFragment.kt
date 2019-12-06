package com.codinghub.apps.hive.ui.home


import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import android.util.Log
import android.view.*

import com.codinghub.apps.hive.R

class HomeFragment : Fragment() {

    lateinit var homeViewPager: ViewPager
    lateinit var homeTabs: TabLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        activity?.title = "Home"

        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.setGroupVisible(R.id.main_menu_group, false)
        super.onPrepareOptionsMenu(menu)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d("Home", "Home on create")

        view?.let {
            homeViewPager = it.findViewById(R.id.homeViewPager)
            homeTabs = it.findViewById(R.id.homeTabs)
        }

        val adapter = HomeViewPagerAdapter(childFragmentManager)
        adapter.addFragment(NewsFeedFragment(), "News Feed")
        adapter.addFragment(MemberListFragment(), "Members")
        homeViewPager.adapter = adapter
        homeTabs.setupWithViewPager(homeViewPager)
    }

}
