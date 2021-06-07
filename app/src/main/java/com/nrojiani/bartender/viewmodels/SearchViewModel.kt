package com.nrojiani.bartender.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrojiani.bartender.data.Resource
import com.nrojiani.bartender.data.domain.Drink
import com.nrojiani.bartender.data.domain.DrinkRef
import com.nrojiani.bartender.data.repository.IDrinksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.IllegalArgumentException
import javax.inject.Inject

@Suppress("ForbiddenComment")
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: IDrinksRepository
) : ViewModel() {

    private val _drinkNameText = MutableLiveData("")
    val drinkNameText: LiveData<String>
        get() = _drinkNameText

    private val _drinkNameSearchResults = MutableLiveData<Resource<List<Drink>>>()
    val drinkNameSearchResults: LiveData<Resource<List<Drink>>>
        get() = _drinkNameSearchResults

    private val _drinkFirstLetterSearchResults = MutableLiveData<Resource<List<Drink>>>()
    val drinkFirstLetterSearchResults: LiveData<Resource<List<Drink>>>
        get() = _drinkFirstLetterSearchResults

    fun getDrinksWithName(drinkName: String) {
        viewModelScope.launch {
            Timber.d("getDrinksWithName($drinkName)")
            if (drinkName.isNullOrBlank()) {
                _drinkNameSearchResults.value =
                    Resource.Failure(IllegalArgumentException("must not be null/blank...TODO: validation"))
                return@launch
            }

            _drinkNameSearchResults.value = Resource.Loading

            kotlin.runCatching {
                repository.getDrinksByName(drinkName)
            }.onFailure { e ->
                Timber.e(e, "Error fetching drinks")
                _drinkNameSearchResults.value = Resource.Failure(e)
            }.onSuccess { matches ->
                Timber.d("Found ${matches.size} matches")
                _drinkNameSearchResults.value = Resource.Success(matches)
            }
        }
    }

    fun getDrinksStartingWithLetter(firstLetter: String) {
        viewModelScope.launch {
            if (firstLetter.isNullOrBlank()) {
                _drinkFirstLetterSearchResults.value =
                    Resource.Failure(IllegalArgumentException("must not be null/blank...TODO: validation"))
                return@launch
            }

            Timber.d("getDrinksStartingWithLetter($firstLetter)")

            if (firstLetter.length != 1) {
                _drinkFirstLetterSearchResults.value =
                    Resource.Failure(IllegalArgumentException("must be single char...TODO: validation"))
                return@launch
            }

            _drinkFirstLetterSearchResults.value = Resource.Loading

            kotlin.runCatching {
                repository.getDrinksByLetter(firstLetter.first())
            }.onFailure { e ->
                Timber.e(e, "Error fetching drinks")
                _drinkFirstLetterSearchResults.value = Resource.Failure(e)
            }.onSuccess { matches ->
                Timber.d("Found ${matches.size} matches")
                _drinkFirstLetterSearchResults.value = Resource.Success(matches)
            }
        }
    }

    fun drinkNameTextChanged(newValue: CharSequence) {
        _drinkNameText.value = newValue.toString()
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
