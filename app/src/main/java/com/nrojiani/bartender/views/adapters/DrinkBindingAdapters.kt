package com.nrojiani.bartender.views.adapters

import android.widget.TextView
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

@BindingAdapter("drinkInstructions")
fun bindInstructions(
    textView: TextView,
    drinkResource: Resource<Drink>?
) {
    textView.text = (drinkResource as? Resource.Success<Drink>)?.data?.instructions?.orEmpty()
}
