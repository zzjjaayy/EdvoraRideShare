package com.jay.edvorarideshare.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.jay.edvorarideshare.R
import com.jay.edvorarideshare.data.models.Ride
import com.jay.edvorarideshare.data.models.User
import com.jay.edvorarideshare.databinding.ActivityMainBinding
import com.jay.edvorarideshare.ui.adapters.TabAdapter
import com.jay.edvorarideshare.ui.fragments.FilterDialogFragment
import com.jay.edvorarideshare.ui.viewmodels.RidesViewModel
import com.jay.edvorarideshare.utils.NetworkResult
import com.jay.edvorarideshare.utils.TAG
import kotlinx.coroutines.flow.collect

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val ridesViewModel: RidesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenCreated {
            ridesViewModel.user.collect {
                binding.progressBar.isVisible = it is NetworkResult.Loading
                if(it is NetworkResult.Success) {
                    val user = it.data as User
                    binding.username.text = user.name
                    Glide.with(this@MainActivity).load(user.imageUrl).circleCrop()
                        .error(R.drawable.ic_error).into(binding.userpic)
                    ridesViewModel.getRides(user.stationCode)

                    binding.username.setOnClickListener {
                        Toast.makeText(this@MainActivity,
                            "Your station code is ${user.stationCode}", Toast.LENGTH_SHORT).show()
                    }

                    ridesViewModel.allRides.collect {
                        binding.progressBar.isVisible = it is NetworkResult.Loading
                        if(it is NetworkResult.Success) setupTabLayoutWithViewPager()
                    }

                }
            }
        }

        binding.filter.setOnClickListener {
            FilterDialogFragment().show(supportFragmentManager, "FilterDialog")
        }
    }

    private fun setupTabLayoutWithViewPager() {
        binding.tabLayout.apply {
            addTab(this.newTab().setText("Nearest"))
            addTab(this.newTab().setText("Upcoming"))
            addTab(this.newTab().setText("Past"))
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    binding.viewPager.currentItem = tab.position
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }

        val adapter = TabAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.apply {
            this.adapter = adapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
                }
            })
        }
    }
}