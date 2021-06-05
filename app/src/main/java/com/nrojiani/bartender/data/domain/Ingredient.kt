package com.nrojiani.bartender.data.domain

import com.google.common.net.UrlEscapers
import com.nrojiani.bartender.data.remote.CocktailApiConstants

data class Ingredient(
    val id: String,
    val name: String,
    val description: String,
    val abv: String,
    val alcoholic: Boolean,
    val type: String,
) {
    fun imageUrl(size: ImageSize): String {
        val encodedIngredientName = UrlEscapers.urlFragmentEscaper().escape(name)
        val imageName = when (size) {
            ImageSize.SMALL -> "$encodedIngredientName-small.png"
            ImageSize.MEDIUM -> "$encodedIngredientName-medium.png"
            ImageSize.LARGE -> "$encodedIngredientName.png"
        }
        return "${CocktailApiConstants.INGREDIENT_IMAGES_BASE_URL}/$imageName"
    }
}

enum class ImageSize {
    SMALL,
    MEDIUM,
    LARGE
}
