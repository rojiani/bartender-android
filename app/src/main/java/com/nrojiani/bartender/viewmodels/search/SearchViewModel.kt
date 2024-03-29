package com.nrojiani.bartender.viewmodels.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrojiani.bartender.data.Resource
import com.nrojiani.bartender.data.domain.Drink
import com.nrojiani.bartender.data.domain.DrinkRef
import com.nrojiani.bartender.data.repository.IDrinksRepository
import com.nrojiani.bartender.utils.connectivity.NetworkStatus
import com.nrojiani.bartender.utils.connectivity.NetworkStatusMonitor
import com.nrojiani.bartender.utils.flow.WhileViewSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.LinkedHashMap
import kotlin.collections.List
import kotlin.collections.MutableMap
import kotlin.collections.emptyList
import kotlin.collections.set

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: IDrinksRepository,
    networkStatusMonitor: NetworkStatusMonitor,
) : ViewModel() {

    private var currentDrinkNameQuery = ""

    private val _drinkNameSearchResource = MutableStateFlow<Resource<List<Drink>>>(Resource.Loading)
    val drinkNameSearchResource: StateFlow<Resource<List<Drink>>>
        get() = _drinkNameSearchResource

    val drinksByNameSearchResults: StateFlow<List<Drink>> = drinkNameSearchResource.mapLatest {
        it.dataOrNull() ?: emptyList()
    }.stateIn(
        scope = viewModelScope,
        started = WhileViewSubscribed,
        initialValue = emptyList()
    )

    val networkStatus: StateFlow<NetworkStatus> =
        networkStatusMonitor.networkEventsFlow
            .onEach { status ->
                Timber.d("[NetworkStatus] $status")
                if (status == NetworkStatus.CONNECTED && drinkNameSearchResource.value !is Resource.Loading) {
                    searchForDrinksByName()
                }
            }.stateIn(
                scope = viewModelScope,
                started = WhileViewSubscribed,
                initialValue = NetworkStatus.UNDETERMINED
            )

    // TODO: Use Paging library. https://developer.android.com/topic/libraries/architecture/paging/v3-overview
    private val inMemoryDrinkNameSearchCache: MutableMap<String, List<Drink>> = LinkedHashMap()

    fun searchForDrinksByName() {
        val drinkName = currentDrinkNameQuery
        Timber.d("searchForDrinksByName($drinkName)")

        val cached = inMemoryDrinkNameSearchCache[drinkName]
        if (cached != null) {
            Timber.d("$drinkName in cache")
            _drinkNameSearchResource.value = Resource.Success(cached)
            return
        }

        _drinkNameSearchResource.value = Resource.Loading

        viewModelScope.launch {
            val drinks = Resource.from {
                repository.getDrinksByName(drinkName)
            }
            _drinkNameSearchResource.value = drinks
            if (drinks is Resource.Success<List<Drink>>) {
                val matches = drinks.data
                Timber.d("Found ${matches.size} matches")
                inMemoryDrinkNameSearchCache[drinkName] = matches
                _drinkNameSearchResource.value = Resource.Success(matches)
            }
        }
    }

    fun getRandomDrink() {
        viewModelScope.launch {
            Timber.d("getRandomDrink()")

            val randomDrinkResource = Resource.from {
                repository.getRandomDrink()
            }
            when (randomDrinkResource) {
                is Resource.Failure -> Timber.e(
                    randomDrinkResource.exception,
                    "Error fetching random drink"
                )
                is Resource.Success<Drink> -> {
                    Timber.d("Fetch random drink ${randomDrinkResource.data.drinkName}")
                    displayDrinkDetails(randomDrinkResource.data)
                }
            }
        }
    }

    fun drinkQueryTextChanged(newValue: CharSequence) {
        currentDrinkNameQuery = newValue.trim().toString()
        searchForDrinksByName()
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

    fun displayDrinkDetails(drink: Drink) {
        val drinkRef = drink.toDrinkRef()
        viewModelScope.launch {
            Timber.d("sending NavigateToDrinkDetail($drinkRef) event")
            eventChannel.send(Event.NavigateToDrinkDetail(drinkRef))
        }
    }
}
