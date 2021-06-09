package com.nrojiani.bartender.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    @Suppress("UnusedPrivateMember")
    savedStateHandle: SavedStateHandle,
) : ViewModel()
