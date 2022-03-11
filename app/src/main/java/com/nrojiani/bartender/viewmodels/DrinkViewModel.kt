package com.nrojiani.bartender.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrojiani.bartender.data.Resource
import com.nrojiani.bartender.data.domain.Drink
import com.nrojiani.bartender.data.domain.DrinkRef
import com.nrojiani.bartender.data.domain.IngredientMeasure
import com.nrojiani.bartender.data.repository.IDrinksRepository
import com.nrojiani.bartender.utils.flow.FLOW_STOP_TIMEOUT_MS
import com.nrojiani.bartender.views.drink.DrinkFragmentArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DrinkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: IDrinksRepository
) : ViewModel() {

    private val fragmentArgs = DrinkFragmentArgs.fromSavedStateHandle(savedStateHandle)

    val drinkRef: DrinkRef = fragmentArgs.drinkRef ?: DEFAULT_DRINK_REF

    val drinkResource: StateFlow<Resource<Drink>> = flow {
        Timber.d("drink flow (${drinkRef.id})")

        val drinkDetailsResource = Resource.from {
            when (val details = repository.lookupDrinkDetails(drinkRef.id)) {
                null -> throw NoSuchElementException("Drink with id ${drinkRef.id} not found on server")
                else -> details
            }
        }
        emit(drinkDetailsResource)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = Resource.Loading
    )

    val ingredientMeasures: StateFlow<List<IngredientMeasure>> = drinkResource.mapNotNull {
        it.dataOrNull()?.ingredientMeasures
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(FLOW_STOP_TIMEOUT_MS),
        initialValue = emptyList()
    )

    val glass: StateFlow<String> = drinkResource.mapNotNull {
        it.dataOrNull()?.glass
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(FLOW_STOP_TIMEOUT_MS),
        initialValue = ""
    )

    val instructions: StateFlow<String> = drinkResource.mapNotNull {
        it.dataOrNull()?.instructions
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(FLOW_STOP_TIMEOUT_MS),
        initialValue = ""
    )

    fun displayIngredient(ingredientName: String) {
        viewModelScope.launch {
            Timber.d("sending NavigateToIngredientDetail($ingredientName) event")
            eventChannel.send(Event.NavigateToIngredientDetail(ingredientName))
        }
    }

    sealed class Event {
        data class NavigateToIngredientDetail(val ingredientName: String) : Event()
    }

    /**
     * Channel used for events produced by SearchViewModel and
     * consumed by the SearchFragment.
     */
    private val eventChannel = Channel<Event>(Channel.BUFFERED)

    /**
     * Exposed to SearchFragment, which will use consume
     * one-time events (e.g., Navigation events, notifications, etc.)
     */
    val eventsFlow: Flow<Event> = eventChannel.receiveAsFlow()

    companion object {
        /**
         * Solely to enable unit testing of ViewModel.
         */
        private val DEFAULT_DRINK_REF = DrinkRef(
            id = "178336",
            imageUrl = "https://www.thecocktaildb.com/images/media/drink/07iep51598719977.jpg",
            drinkName = "Blueberry Mojito"
        )
    }
}
