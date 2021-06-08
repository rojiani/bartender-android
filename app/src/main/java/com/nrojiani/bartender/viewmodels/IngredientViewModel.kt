package com.nrojiani.bartender.viewmodels

import androidx.lifecycle.*
import com.nrojiani.bartender.data.Resource
import com.nrojiani.bartender.data.domain.ImageSize
import com.nrojiani.bartender.data.domain.Ingredient
import com.nrojiani.bartender.data.repository.IDrinksRepository
import com.nrojiani.bartender.utils.viewmodel.navArgs
import com.nrojiani.bartender.views.ingredient.IngredientFragmentArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class IngredientViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: IDrinksRepository
) : ViewModel() {

    /**
     * Workaround for Hilt not playing nicely with SafeArgs.
     * @see [navArgs]
     */
    private val fragmentArgs: IngredientFragmentArgs by savedStateHandle.navArgs()

    private val name: String = fragmentArgs.ingredientName

    // Use with databinding once stable
    private val ingredientFlow: StateFlow<Resource<Ingredient>> = flow {
        Timber.d("ingredient flow ($name)")
        kotlin.runCatching {
            repository.getIngredientByName(name)
        }.mapCatching {
            when {
                it.isNullOrEmpty() -> throw NoSuchElementException(
                    "Ingredient with name $ingredientName not found on server"
                )
                else -> it.first()
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

    val ingredientResource: LiveData<Resource<Ingredient>> = ingredientFlow.asLiveData()

    val ingredientName: LiveData<String> = liveData {
        emit(fragmentArgs.ingredientName)
    }

    val imageUrl: LiveData<String?> = liveData {
        emit(Ingredient.imageUrl(fragmentArgs.ingredientName, ImageSize.MEDIUM))
    }

    val description: LiveData<String> = ingredientResource.map {
        val desc = it.dataOrNull?.description
        when {
            desc.isNullOrBlank() -> "No description provided for this ingredient."
            else -> desc
        }
    }

    val type: LiveData<String> = ingredientResource.map {
        it.dataOrNull?.type.orEmpty()
    }
}
