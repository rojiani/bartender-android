package com.nrojiani.bartender.data.domain

/**
 * The name of an ingredient along with the amount of the ingredient to use in
 * a recipe.
 */
data class IngredientMeasure(val ingredientName: String, val measure: String) {
    val iconUrl: String
        get() = Ingredient.imageUrl(ingredientName, ImageSize.SMALL)
}
