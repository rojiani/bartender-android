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
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.nrojiani.bartender.R
import com.nrojiani.bartender.databinding.SearchFragmentBinding
import com.nrojiani.bartender.viewmodels.SearchViewModel
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

        consumeEvents()
        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.apply {

            drinkNameText.observe(viewLifecycleOwner) {
                Timber.d("[drinkNameText]: $it")
            }

            drinkNameSearchResults.observe(viewLifecycleOwner) { matches ->
                Timber.d("[drinkNameSearchResults]: $matches")
            }

            drinkFirstLetterSearchResults.observe(viewLifecycleOwner) { matches ->
                Timber.d("[drinkFirstCharSearchResults]: $matches")
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
