package com.nrojiani.bartender.viewmodels

import androidx.lifecycle.*
import com.nrojiani.bartender.data.Resource
import com.nrojiani.bartender.data.domain.Drink
import com.nrojiani.bartender.data.domain.DrinkRef
import com.nrojiani.bartender.data.repository.IDrinksRepository
import com.nrojiani.bartender.utils.viewmodel.navArgs
import com.nrojiani.bartender.views.DrinkFragmentArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DrinkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: IDrinksRepository
) : ViewModel() {

    /**
     * Workaround for Hilt not playing nicely with SafeArgs.
     * @see [navArgs]
     */
    private val fragmentArgs: DrinkFragmentArgs by savedStateHandle.navArgs()

    val drinkRef: LiveData<DrinkRef> = liveData {
        emit(fragmentArgs.drinkRef)
    }

    private val drinkId = fragmentArgs.drinkRef.id

    // Use with databinding once stable
    private val drinkFlow: StateFlow<Resource<Drink>> = flow {
        Timber.d("drink flow ($drinkId)")
        kotlin.runCatching {
            repository.lookupDrinkDetails(drinkId)
        }.mapCatching {
            when (it) {
                null -> throw NoSuchElementException("Drink with id $drinkId not found on server")
                else -> it
            }
        }.onSuccess {
            emit(Resource.Success(it))
        }.onFailure { e ->
            emit(Resource.Failure(e))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.Loading
    )

    val drinkResource: LiveData<Resource<Drink>> = drinkFlow.asLiveData()
}
