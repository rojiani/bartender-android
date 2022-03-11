@file:JvmName("BindingAdapters")

package com.nrojiani.bartender.views.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.nrojiani.bartender.R
import com.nrojiani.bartender.data.Resource
import com.nrojiani.bartender.data.domain.Drink
import com.nrojiani.bartender.data.domain.Ingredient
import com.nrojiani.bartender.data.domain.IngredientMeasure
import com.nrojiani.bartender.utils.connectivity.NetworkStatus
import com.nrojiani.bartender.views.drink.ingredients.IngredientMeasureAdapter
import com.nrojiani.bartender.views.search.drinks.DrinkAdapter

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
fun <T> bindIsGoneWhileLoading(view: View, resource: Resource<T>?) {
    view.visibility = when {
        resource == null -> View.GONE
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

@BindingAdapter("ingredientsListData")
fun bindIngredientsListData(recyclerView: RecyclerView, data: List<IngredientMeasure>) {
    val adapter = recyclerView.adapter as IngredientMeasureAdapter
    adapter.submitList(data)
}

@BindingAdapter("drinksByNameSearchData")
fun <T> bindDrinksByNameSearchData(recyclerView: RecyclerView, data: List<Drink>) {
    val adapter = recyclerView.adapter as DrinkAdapter
    adapter.submitList(data)
}

@BindingAdapter("ingredientDescription")
fun bindIngredientDescription(textView: TextView, ingredientResource: Resource<Ingredient>) {
    textView.text = ingredientResource.dataOrNull()
        ?.description
        ?.ifBlank { "No description provided." }
}

@BindingAdapter("visibleWhenOnline")
fun bindVisibleWhenOnline(
    view: View,
    networkStatus: NetworkStatus
) {
    view.isVisible = networkStatus != NetworkStatus.NOT_CONNECTED
}

@BindingAdapter("visibleWhenOffline")
fun bindVisibleWhenOffline(
    view: View,
    networkStatus: NetworkStatus
) {
    view.isVisible = networkStatus == NetworkStatus.NOT_CONNECTED
}
