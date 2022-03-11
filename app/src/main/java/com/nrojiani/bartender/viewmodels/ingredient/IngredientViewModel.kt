package com.nrojiani.bartender.viewmodels.ingredient

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrojiani.bartender.data.Resource
import com.nrojiani.bartender.data.domain.ImageSize
import com.nrojiani.bartender.data.domain.Ingredient
import com.nrojiani.bartender.data.repository.IDrinksRepository
import com.nrojiani.bartender.views.ingredient.IngredientFragmentArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    private val _ingredientResource = MutableStateFlow<Resource<Ingredient>>(Resource.Loading)
    val ingredientResource: StateFlow<Resource<Ingredient>> = _ingredientResource.asStateFlow()

    fun loadIngredient() {
        Timber.d("loading ingredient ($name)")
        viewModelScope.launch {
            val ingredient = Resource.from {
                repository.getIngredientByName(name)
                    .firstOrNull()
                    ?: throw NoSuchElementException("Ingredient with name $name not found on server")
            }
            _ingredientResource.emit(ingredient)
        }
    }
}
