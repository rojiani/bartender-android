package com.nrojiani.bartender.viewmodels

import androidx.lifecycle.*
import com.nrojiani.bartender.data.Resource
import com.nrojiani.bartender.data.domain.Drink
import com.nrojiani.bartender.data.domain.DrinkRef
import com.nrojiani.bartender.data.repository.IDrinksRepository
import com.nrojiani.bartender.utils.connectivity.NetworkStatus
import com.nrojiani.bartender.utils.connectivity.NetworkStatusMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@Suppress("ForbiddenComment")
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: IDrinksRepository,
    networkStatusMonitor: NetworkStatusMonitor,
) : ViewModel() {

    private val _drinkNameText = MutableLiveData("")
    val drinkNameText: LiveData<String>
        get() = _drinkNameText

    private val _drinkNameSearchResource = MutableLiveData<Resource<List<Drink>>>()
    val drinkNameSearchResource: LiveData<Resource<List<Drink>>>
        get() = _drinkNameSearchResource

    val drinksByNameSearchResults: LiveData<List<Drink>> = drinkNameSearchResource.map {
        it.dataOrNull ?: emptyList()
    }

    val networkStatus: LiveData<NetworkStatus> =
        networkStatusMonitor.networkEventsFlow
            .onEach { status ->
                Timber.d("[NetworkStatus] $status")
                if (status == NetworkStatus.CONNECTED && drinkNameSearchResource.value !is Resource.Loading) {
                    retrySearchByName()
                }
            }.asLiveData()

    private val inMemoryDrinkNameSearchCache: MutableMap<String, List<Drink>> = LinkedHashMap()

    fun getDrinksWithName(drinkName: String) {
        val cached = inMemoryDrinkNameSearchCache[drinkName]
        if (cached != null) {
            Timber.d("$drinkName in cache")
            _drinkNameSearchResource.value = Resource.Success(cached)
            return
        }

        viewModelScope.launch {
            Timber.d("getDrinksWithName($drinkName)")

            _drinkNameSearchResource.value = Resource.Loading

            kotlin.runCatching {
                repository.getDrinksByName(drinkName)
            }.onFailure { e ->
                Timber.e(e, "Error fetching drinks")
                _drinkNameSearchResource.value = Resource.Failure(e)
            }.onSuccess { matches ->
                Timber.d("Found ${matches.size} matches")
                inMemoryDrinkNameSearchCache[drinkName] = matches
                _drinkNameSearchResource.value = Resource.Success(matches)
            }
        }
    }

    fun retrySearchByName() {
        Timber.d("retrySearchByName")
        getDrinksWithName(drinkNameText.value.orEmpty())
    }

    fun getRandomDrink() {
        viewModelScope.launch {
            Timber.d("getRandomDrink()")

            kotlin.runCatching {
                repository.getRandomDrink()
            }.onFailure { e ->
                Timber.e(e, "Error fetching random drink")
            }.onSuccess { randomDrink ->
                Timber.d("Fetch random drink ${randomDrink.drinkName}")
                displayDrinkDetails(randomDrink)
            }
        }
    }

    fun drinkNameTextChanged(newValue: CharSequence) {
        _drinkNameText.value = newValue.trim().toString()
    }

    sealed class Event {
        data class NavigateToDrinkDetail(val drinkRef: DrinkRef) : Event()
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

    fun displayDrinkDetails(drinkRef: DrinkRef) {
        viewModelScope.launch {
            Timber.d("sending NavigateToDrinkDetail($drinkRef) event")
            eventChannel.send(Event.NavigateToDrinkDetail(drinkRef))
        }
    }

    fun displayDrinkDetails(drink: Drink) = displayDrinkDetails(drink.toDrinkRef())
}
