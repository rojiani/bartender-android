package com.nrojiani.bartender.views.adapters

import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.nrojiani.bartender.data.Resource
import com.nrojiani.bartender.data.domain.Drink

@BindingAdapter("loadingIndicator")
fun bindLoadingIndicator(
    progressIndicator: LinearProgressIndicator,
    drinkResource: Resource<Drink>?
) {
    progressIndicator.isVisible = drinkResource is Resource.Loading
}
