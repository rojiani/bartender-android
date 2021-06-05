package com.nrojiani.bartender.data.domain

/**
 * Domain model for a cocktail with a recipe.
 */
data class Cocktail(
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
    val ingredients: List<Ingredient>,
)

data class Ingredient(val ingredient: String, val measure: String)
