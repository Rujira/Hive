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
import kotlinx.android.synthetic.main.fragment_members.*

class MemberListFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_members, container, false)

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
                membersTitleTextView.text = getString(R.string.members_empty_title_teacher)
                membersSubtitleTextView.text = getString(R.string.members_empty_subtitle_teacher)
            }

            UserRole.PARENT -> {
                membersTitleTextView.text = getString(R.string.members_empty_title_parent)
                membersSubtitleTextView.text = getString(R.string.members_empty_subtitle_parent)
                addMemberButton.visibility = View.GONE
            }
        }
    }


}
