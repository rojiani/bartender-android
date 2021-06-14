package com.nrojiani.bartender.views.search

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
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import com.nrojiani.bartender.R
import com.nrojiani.bartender.data.Resource
import com.nrojiani.bartender.data.domain.Drink
import com.nrojiani.bartender.databinding.SearchFragmentBinding
import com.nrojiani.bartender.viewmodels.SearchViewModel
import com.nrojiani.bartender.views.search.drinks.DrinkAdapter
import com.nrojiani.bartender.views.search.drinks.DrinkClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel by viewModels<SearchViewModel>()

    private lateinit var binding: SearchFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.search_fragment, container, false)

        binding.searchViewModel = viewModel
        binding.setLifecycleOwner { this.lifecycle }

        val adapter: ListAdapter<Drink, DrinkAdapter.ViewHolder> =
            DrinkAdapter(
                clickListener = DrinkClickListener { drink ->
                    viewModel.displayDrinkDetails(drink = drink)
                }
            )

        binding.drinkSearchResultsList.adapter = adapter

        consumeEvents()
        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.drinkNameSearchResource.collect {
                    val state = when (it) {
                        is Resource.Success -> "Success"
                        is Resource.Failure -> "Failure"
                        is Resource.Loading -> "Loading"
                    }
                    Timber.d("[drinkNameSearchResource]: $state")
                }
            }
        }
    }

    private fun consumeEvents() {
        // https://medium.com/androiddevelopers/a-safer-way-to-collect-flows-from-android-uis-23080b1f8bda
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventsFlow
                .flowWithLifecycle(
                    lifecycle = viewLifecycleOwner.lifecycle,
                    minActiveState = Lifecycle.State.STARTED
                )
                .collect { event ->
                    Timber.d("Received event: $event")
                    when (event) {
                        is SearchViewModel.Event.NavigateToDrinkDetail -> {
                            val action: NavDirections = SearchFragmentDirections
                                .actionSearchFragmentToDrinkFragment(event.drinkRef)
                            findNavController().navigate(action)
                        }
                    }
                }
        }
    }
}
