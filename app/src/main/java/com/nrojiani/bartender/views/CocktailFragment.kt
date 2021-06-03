package com.nrojiani.bartender.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nrojiani.bartender.R
import com.nrojiani.bartender.databinding.CocktailFragmentBinding
import com.nrojiani.bartender.viewmodels.CocktailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CocktailFragment : Fragment() {

    private val viewModel by viewModels<CocktailViewModel>()

    private lateinit var binding: CocktailFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.cocktail_fragment, container, false
        )

        binding.cocktailViewModel = viewModel
        binding.setLifecycleOwner { this.lifecycle }

        return binding.root
    }
}
