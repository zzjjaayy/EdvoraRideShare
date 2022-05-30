package com.jay.edvorarideshare.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jay.edvorarideshare.ui.fragments.NearestFragment
import com.jay.edvorarideshare.ui.fragments.PastFragment
import com.jay.edvorarideshare.ui.fragments.UpcomingFragment

class TabAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> UpcomingFragment()
            2 -> PastFragment()
            else -> NearestFragment()
        }
    }
}