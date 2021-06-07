package com.nrojiani.bartender.data.repository

import com.nrojiani.bartender.data.domain.Drink
import com.nrojiani.bartender.data.domain.DrinkRef
import com.nrojiani.bartender.data.domain.Ingredient

interface IDrinksRepository {

    /**
     * Search drink by name
     */
    suspend fun getDrinksByName(drinkName: String): List<Drink>

    /**
     * List all drinks by first letter
     */
    suspend fun getDrinksByLetter(letter: Char): List<Drink>

    /**
     * Search ingredient by name
     */
    suspend fun getIngredientByName(ingredientName: String): List<Ingredient>

    /**
     * Lookup full drink details by id
     */
    suspend fun lookupDrinkDetails(drinkId: String): Drink?

    /**
     * Lookup ingredient by ID
     */
    suspend fun lookupIngredientDetails(ingredientId: String): Ingredient?

    /**
     * Lookup a random drink
     */
    suspend fun getRandomDrink(): Drink

    /**
     * Search for drinks containing an ingredient
     */
    suspend fun getDrinksWithIngredient(ingredientName: String): List<DrinkRef>

    /**
     * Get alcoholic drinks
     */
    suspend fun getAlcoholicDrinks(): List<DrinkRef>

    /**
     * Get non-alcoholic drinks
     */
    suspend fun getNonAlcoholicDrinks(): List<DrinkRef>

    /**
     * Filter by Category
     */
    suspend fun getDrinksFilteredByCategory(category: String): List<DrinkRef>

    /**
     * Filter by Glass
     */
    suspend fun getDrinksFilteredByGlass(glass: String): List<DrinkRef>
}
