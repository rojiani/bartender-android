package com.nrojiani.bartender.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CocktailViewModel(cocktailName: String) : ViewModel() {

    private var _selectedCocktailName = MutableLiveData<String>()
    val selectedCocktailName: LiveData<String>
        get() = _selectedCocktailName

    init {
        _selectedCocktailName.value = cocktailName
    }
}
