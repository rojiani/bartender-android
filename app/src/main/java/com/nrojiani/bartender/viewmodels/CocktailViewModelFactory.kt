package com.nrojiani.bartender.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CocktailViewModelFactory(private val cocktailName: String) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CocktailViewModel::class.java)) {
            return CocktailViewModel(cocktailName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
