package com.nrojiani.bartender.data.domain

import com.google.common.net.UrlEscapers
import com.nrojiani.bartender.data.remote.webservice.TheCocktailDbApi

/**
 * The name of an ingredient along with the amount of the ingredient to use in
 * a recipe.
 */
data class IngredientMeasure(val ingredientName: String, val measure: String) {
    val iconUrl: String
        get() {
            val encodedIngredientName = UrlEscapers.urlFragmentEscaper().escape(ingredientName)
            val imageName = "$encodedIngredientName-small.png"
            return "${TheCocktailDbApi.INGREDIENT_IMAGES_BASE_URL}/$imageName"
        }
}
