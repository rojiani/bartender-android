package com.nrojiani.bartender.data.domain

import com.google.common.net.UrlEscapers
import com.nrojiani.bartender.data.remote.webservice.TheCocktailDbApi

data class Ingredient(
    val id: String,
    val name: String,
    val description: String,
    val abv: String,
    val alcoholic: Boolean,
    val type: String,
) {
    companion object {
        fun imageUrl(ingredientName: String, size: ImageSize): String {
            val encodedIngredientName = UrlEscapers.urlFragmentEscaper().escape(ingredientName)
            val imageName = when (size) {
                ImageSize.SMALL -> "$encodedIngredientName-small.png"
                ImageSize.MEDIUM -> "$encodedIngredientName-medium.png"
                ImageSize.LARGE -> "$encodedIngredientName.png"
            }
            return "${TheCocktailDbApi.INGREDIENT_IMAGES_BASE_URL}/$imageName"
        }
    }
}
