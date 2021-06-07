package com.nrojiani.bartender.data.domain

/**
 * Domain model for a cocktail with full details.
 */
data class Drink(
    val id: String,
    val drinkName: String,
    val alternateDrinkName: String,
    val tags: List<String>,
    val videoUrl: String,
    val ibaCategory: IbaCategory,
    val alcoholic: String,
    val glass: String,
    val instructions: String,
    val imageUrl: String,
    val ingredientMeasures: List<IngredientMeasure>,
) {
    fun toDrinkRef(): DrinkRef = DrinkRef(
        id = id,
        drinkName = drinkName,
        imageUrl = imageUrl
    )
}
