@file:JvmName("BindingAdapters")
package com.nrojiani.bartender.views.adapters

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nrojiani.bartender.R

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
