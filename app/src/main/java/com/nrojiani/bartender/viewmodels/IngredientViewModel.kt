package com.nrojiani.bartender.viewmodels

import androidx.lifecycle.*
import com.nrojiani.bartender.data.Resource
import com.nrojiani.bartender.data.domain.ImageSize
import com.nrojiani.bartender.data.domain.Ingredient
import com.nrojiani.bartender.data.repository.IDrinksRepository
import com.nrojiani.bartender.views.ingredient.IngredientFragmentArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class IngredientViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: IDrinksRepository
) : ViewModel() {

    private val fragmentArgs = IngredientFragmentArgs.fromSavedStateHandle(savedStateHandle)
    val name: String = fragmentArgs.ingredientName
    val imageUrl: String = Ingredient.imageUrl(fragmentArgs.ingredientName, ImageSize.MEDIUM)

    // https://medium.com/androiddevelopers/migrating-from-livedata-to-kotlins-flow-379292f419fb
    // For Data Binding, you should use Flows everywhere and simply add asLiveData() to expose them
    // to the view. Data Binding will be updated when lifecycle-runtime-ktx 2.4.0 goes stable.
    val ingredientResource: LiveData<Resource<Ingredient>> = flow {
        Timber.d("ingredient flow ($name)")
        emit(Resource.Loading)
        kotlin.runCatching {
            repository.getIngredientByName(name)
        }.mapCatching {
            when {
                it.isNullOrEmpty() -> throw NoSuchElementException(
                    "Ingredient with name $name not found on server"
                )
                else -> it.first()
            }
        }.onSuccess {
            emit(Resource.Success(it))
        }.onFailure { e ->
            emit(Resource.Failure(e))
        }
    }.asLiveData()

    val description: LiveData<String> = ingredientResource.map {
        it.dataOrNull()
            ?.description
            ?.ifBlank { "No description provided for this ingredient." }
            .orEmpty()
    }
}
