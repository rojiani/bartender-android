package com.nrojiani.bartender.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nrojiani.bartender.utils.viewmodel.navArgs
import com.nrojiani.bartender.views.CocktailFragmentArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CocktailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /**
     * Workaround for Hilt not playing nicely with SafeArgs.
     * @see [navArgs]
     */
    private val fragmentArgs: CocktailFragmentArgs by savedStateHandle.navArgs()

    private var _selectedCocktailName = MutableLiveData(fragmentArgs.cocktailName)
    val selectedCocktailName: LiveData<String>
        get() = _selectedCocktailName
}
