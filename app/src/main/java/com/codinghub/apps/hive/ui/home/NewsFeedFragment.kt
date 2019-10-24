package com.codinghub.apps.hive.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders

import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.login.CurrentUser
import com.codinghub.apps.hive.model.login.LoginType
import com.codinghub.apps.hive.model.login.UserRole
import com.codinghub.apps.hive.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_news_feed.*

class NewsFeedFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_news_feed, container, false)

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureUI(homeViewModel.getCurrentUser().first())
    }

    private fun configureUI(currentUser: CurrentUser) {

        when(currentUser.role) {
            UserRole.ADMIN -> {
                newsFeedTitleTextView.text = getString(R.string.news_feed_empty_title_teacher)
                newsFeedSubtitleTextView.text = getString(R.string.news_feed_empty_subtitle_teacher)
            }

            UserRole.PARENT -> {
                newsFeedTitleTextView.text = getString(R.string.news_feed_empty_title_parent)
                newsFeedSubtitleTextView.text = getString(R.string.news_feed_empty_subtitle_parent)
                createNewsFeedButton.visibility = View.GONE

            }
        }
    }
}
