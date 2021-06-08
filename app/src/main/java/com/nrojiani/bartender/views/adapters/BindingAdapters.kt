@file:JvmName("BindingAdapters")

package com.nrojiani.bartender.views.adapters

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.nrojiani.bartender.R
import com.nrojiani.bartender.data.Resource

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, url: String) {
    Glide.with(imageView.context)
        .load(url.toUri().buildUpon().scheme("https").build())
        .apply(
            RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
        )
        .into(imageView)
}

@BindingAdapter("isGone")
fun <T> bindIsGone(view: View, resource: Resource<T>) {
    view.visibility = if (resource is Resource.Success) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("loadingIndicator")
fun <T> bindLoadingIndicator(
    progressIndicator: LinearProgressIndicator,
    resource: Resource<T>?
) {
    progressIndicator.isVisible = resource is Resource.Loading
}
