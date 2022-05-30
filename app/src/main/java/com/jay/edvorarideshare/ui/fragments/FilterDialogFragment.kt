package com.jay.edvorarideshare.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.jay.edvorarideshare.R
import com.jay.edvorarideshare.databinding.FragmentFilterDialogBinding
import com.jay.edvorarideshare.ui.viewmodels.RidesViewModel
import com.jay.edvorarideshare.utils.TAG
import kotlinx.coroutines.flow.collectLatest


class FilterDialogFragment: DialogFragment() {

    private var _binding: FragmentFilterDialogBinding? = null
    private val binding: FragmentFilterDialogBinding get() = _binding!!
    private val ridesViewModel: RidesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterDialogBinding.inflate(inflater, container, false)
        setupPosition()
        return binding.root
    }

    private fun setupPosition() {
        val window = dialog!!.window
        window!!.setGravity(Gravity.TOP or Gravity.START)
        val params = window.attributes
        params.x = 350
        params.y = 250
        window.attributes = params
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            val stateAdapter = ArrayAdapter(requireContext(), R.layout.filter_list_item, mutableListOf<String>())
            val cityAdapter = ArrayAdapter(requireContext(), R.layout.filter_list_item, mutableListOf<String>())
            (stateMenu.editText as? AutoCompleteTextView)?.setAdapter(stateAdapter)
            (cityMenu.editText as? AutoCompleteTextView)?.setAdapter(cityAdapter)

            lifecycleScope.launchWhenCreated {
                ridesViewModel.states.collectLatest {
                    stateAdapter.clear()
                    stateAdapter.addAll(it)
                }
            }
            lifecycleScope.launchWhenCreated {
                ridesViewModel.cities.collectLatest {
                    cityAdapter.clear()
                    Log.d(TAG, "collect cities: ${it.size}")
                    cityAdapter.addAll(it)
                    (cityMenu.editText as? AutoCompleteTextView)?.setText("")
                }
            }

            (stateMenu.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, i, _ ->
                val state = stateAdapter.getItem(i)
                ridesViewModel.filterCities( state ?: "")
                ridesViewModel.filterByState(state ?: "")
            }

            (cityMenu.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, i, _ ->
                ridesViewModel.filterByCity(cityAdapter.getItem(i) ?: "")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}