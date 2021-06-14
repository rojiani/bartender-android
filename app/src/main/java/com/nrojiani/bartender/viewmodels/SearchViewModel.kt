package com.nrojiani.bartender.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrojiani.bartender.data.Resource
import com.nrojiani.bartender.data.domain.Drink
import com.nrojiani.bartender.data.domain.DrinkRef
import com.nrojiani.bartender.data.repository.IDrinksRepository
import com.nrojiani.bartender.utils.connectivity.NetworkStatus
import com.nrojiani.bartender.utils.connectivity.NetworkStatusMonitor
import com.nrojiani.bartender.utils.flow.FLOW_STOP_TIMEOUT_MS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.LinkedHashMap
import kotlin.collections.List
import kotlin.collections.MutableMap
import kotlin.collections.emptyList
import kotlin.collections.set

@Suppress("ForbiddenComment")
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
        it.dataOrNull ?: emptyList()
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(FLOW_STOP_TIMEOUT_MS),
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
                started = WhileSubscribed(FLOW_STOP_TIMEOUT_MS),
                initialValue = NetworkStatus.UNDETERMINED
            )

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

    fun displayDrinkDetails(drinkRef: DrinkRef) {
        viewModelScope.launch {
            Timber.d("sending NavigateToDrinkDetail($drinkRef) event")
            eventChannel.send(Event.NavigateToDrinkDetail(drinkRef))
        }
    }

    fun displayDrinkDetails(drink: Drink) = displayDrinkDetails(drink.toDrinkRef())
}
