package com.codinghub.apps.hive.ui.notifications


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager

import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.ui.notifications.messages.MessageListFragment
import com.codinghub.apps.hive.ui.notifications.requests.RequestListFragment
import com.codinghub.apps.hive.viewmodel.NotificationsViewModel
import com.google.android.material.tabs.TabLayout

class NotificationsFragment : Fragment() {

    lateinit var notificationsViewPager: ViewPager
    lateinit var notificationTabs: TabLayout

    private lateinit var notificationsViewModel: NotificationsViewModel

    private val TAG = NotificationsFragment::class.qualifiedName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = getString(R.string.title_notifications)

        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel::class.java)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d(TAG, "On Activity Create : Notification")

        view?.let {
            notificationsViewPager = it.findViewById(R.id.notificationsViewPager)
            notificationTabs = it.findViewById(R.id.notificationsTabs)
        }

        val adapter = NotificationsViewPagerAdapter(childFragmentManager)
        adapter.addFragment(MessageListFragment(), "Messages")
        adapter.addFragment(RequestListFragment(), "Requests")
        notificationsViewPager.adapter = adapter
        notificationTabs.setupWithViewPager(notificationsViewPager)

        when(AppPrefs.getNotificationFlag()) {
            "REQUEST" -> {
                notificationsViewPager.currentItem = 1
            }
            "PICKUP" -> {
                notificationsViewPager.currentItem = 0
            } else -> {
                notificationsViewPager.currentItem = 0
            }

        }
        AppPrefs.saveNotificationFlag("Non")


    }

}
