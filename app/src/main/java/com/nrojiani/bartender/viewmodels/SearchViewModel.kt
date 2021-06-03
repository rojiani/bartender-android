package com.nrojiani.bartender.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@Suppress("ForbiddenComment")
// TODO: add @HiltViewModel - @Inject constructor(searchRepository) when repository added
class SearchViewModel : ViewModel() {

    sealed class Event {
        data class NavigateToCocktailDetail(val cocktailName: String) : Event()
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

    fun displayCocktailDetails(cocktailName: String) {
        viewModelScope.launch {
            Timber.d("sending event")
            eventChannel.send(Event.NavigateToCocktailDetail(cocktailName))
        }
    }
}
