package com.jay.edvorarideshare.ui.fragments

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.jay.edvorarideshare.R
import com.jay.edvorarideshare.data.models.Ride
import com.jay.edvorarideshare.data.models.User
import com.jay.edvorarideshare.databinding.RidesListBinding
import com.jay.edvorarideshare.ui.MainActivity
import com.jay.edvorarideshare.ui.adapters.RideAdapter
import com.jay.edvorarideshare.ui.viewmodels.RidesViewModel
import com.jay.edvorarideshare.utils.NetworkResult
import com.jay.edvorarideshare.utils.TAG
import kotlinx.coroutines.flow.collect

class NearestFragment : Fragment() {

    private var _binding: RidesListBinding? = null
    private val binding get() = _binding!!
    private val ridesViewModel: RidesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = RidesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rideAdapter = RideAdapter(ridesViewModel.user.value.data?.stationCode ?: 0) {}

        binding.recyclerView.adapter = rideAdapter
        lifecycleScope.launchWhenCreated {
            ridesViewModel.nearestRides.collect {
                binding.notifyGroup.isVisible = it is NetworkResult.Empty || it is NetworkResult.Error
                when (it) {
                    is NetworkResult.Empty -> {
                        binding.icon.setImageResource(R.drawable.ic_error)
                        binding.message.text = it.message
                    }
                    is NetworkResult.Success -> rideAdapter.submitList(it.data)
                    is NetworkResult.Error -> {
                        binding.icon.setImageResource(R.drawable.ic_error)
                        binding.message.text = it.message
                    }
                    else -> {}
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}