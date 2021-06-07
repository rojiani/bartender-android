package com.nrojiani.bartender.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nrojiani.bartender.R
import com.nrojiani.bartender.databinding.DrinkFragmentBinding
import com.nrojiani.bartender.viewmodels.DrinkViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * UI Controller for the displaying drink details/recipes
 */
@AndroidEntryPoint
class DrinkFragment : Fragment() {

    private val viewModel by viewModels<DrinkViewModel>()

    private lateinit var binding: DrinkFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.drink_fragment, container, false
        )

        binding.drinkViewModel = viewModel
        binding.setLifecycleOwner { this.lifecycle }

        return binding.root
    }
}
