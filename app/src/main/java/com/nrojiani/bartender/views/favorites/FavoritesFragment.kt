package com.nrojiani.bartender.views.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nrojiani.bartender.R
import com.nrojiani.bartender.databinding.FavoritesFragmentBinding
import com.nrojiani.bartender.viewmodels.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {
    private val viewModel by viewModels<FavoritesViewModel>()

    private lateinit var binding: FavoritesFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.favorites_fragment, container, false)

        binding.favoritesViewModel = viewModel
        binding.setLifecycleOwner { this.lifecycle }

        return binding.root
    }
}
