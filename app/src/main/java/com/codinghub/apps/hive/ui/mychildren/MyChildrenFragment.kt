package com.codinghub.apps.hive.ui.mychildren


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.codinghub.apps.hive.R

class MyChildrenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        activity?.title = getString(R.string.title_my_children)

        return inflater.inflate(R.layout.fragment_my_children, container, false)
    }


}
