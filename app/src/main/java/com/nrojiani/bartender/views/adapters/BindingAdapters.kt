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

/**
 * VISIBLE only if Resource.Success, else GONE
 */
@BindingAdapter("visibleOnSuccess")
fun <T> bindVisibleOnSuccess(view: View, resource: Resource<T>) {
    view.visibility = when {
        resource.isSuccess -> View.VISIBLE
        else -> View.GONE
    }
}

/**
 * VISIBLE if Resource.Success or Resource.Failure, GONE while Loading
 */
@BindingAdapter("isGoneWhileLoading")
fun <T> bindIsGoneWhileLoading(view: View, resource: Resource<T>) {
    view.visibility = when {
        resource.isLoading -> View.GONE
        else -> View.VISIBLE
    }
}

@BindingAdapter("loadingIndicator")
fun <T> bindLoadingIndicator(
    progressIndicator: LinearProgressIndicator,
    resource: Resource<T>?
) {
    progressIndicator.isVisible = resource?.isLoading ?: false
}
