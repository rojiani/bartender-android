package com.nrojiani.bartender.views.drink

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import com.nrojiani.bartender.R
import com.nrojiani.bartender.data.domain.IngredientMeasure
import com.nrojiani.bartender.databinding.DrinkFragmentBinding
import com.nrojiani.bartender.viewmodels.DrinkViewModel
import com.nrojiani.bartender.views.drink.ingredients.IngredientMeasureAdapter
import com.nrojiani.bartender.views.drink.ingredients.IngredientMeasureClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

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

        val adapter: ListAdapter<IngredientMeasure, IngredientMeasureAdapter.ViewHolder> =
            IngredientMeasureAdapter(
                clickListener = IngredientMeasureClickListener { ingredientMeasure ->
                    viewModel.displayIngredient(ingredientMeasure.ingredientName)
                }
            )

        binding.ingredientsList.adapter = adapter

        consumeEvents()
        subscribeUi(adapter = adapter)

        return binding.root
    }

    private fun subscribeUi(adapter: ListAdapter<IngredientMeasure, IngredientMeasureAdapter.ViewHolder>) {
        viewModel.ingredientMeasures.observe(viewLifecycleOwner) { measures ->
            adapter.submitList(measures)
        }
    }

    private fun consumeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventsFlow
                .flowWithLifecycle(
                    lifecycle = viewLifecycleOwner.lifecycle,
                    minActiveState = Lifecycle.State.STARTED
                )
                .collect { event ->
                    Timber.d("Received event: $event")
                    when (event) {
                        is DrinkViewModel.Event.NavigateToIngredientDetail -> {
                            val action: NavDirections = DrinkFragmentDirections
                                .actionDrinkFragmentToIngredientFragment(event.ingredientName)
                            findNavController().navigate(action)
                        }
                    }
                }
        }
    }
}
