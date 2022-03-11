package com.nrojiani.bartender.views.ingredient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nrojiani.bartender.R
import com.nrojiani.bartender.databinding.IngredientFragmentBinding
import com.nrojiani.bartender.viewmodels.ingredient.IngredientViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IngredientFragment : Fragment() {

    private val viewModel by viewModels<IngredientViewModel>()

    private lateinit var binding: IngredientFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.ingredient_fragment, container, false
        )

        binding.ingredientViewModel = viewModel
        binding.setLifecycleOwner { this.lifecycle }

        viewModel.loadIngredient()

        return binding.root
    }
}
