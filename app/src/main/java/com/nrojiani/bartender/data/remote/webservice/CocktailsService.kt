package com.nrojiani.bartender.data.remote.webservice

import com.nrojiani.bartender.data.remote.dto.NetworkDrinkRefsContainer
import com.nrojiani.bartender.data.remote.dto.NetworkDrinksContainer
import com.nrojiani.bartender.data.remote.dto.NetworkIngredientsContainer
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API interface for [TheCocktailDB API](https://www.thecocktaildb.com/api.php)
 */
interface CocktailsService {
    /**
     * Search cocktail by name
     * www.thecocktaildb.com/api/json/v1/1/search.php?s=margarita
     */
    @GET("search.php")
    suspend fun getDrinksByName(@Query(value = "s") drinkName: String): NetworkDrinksContainer

    /**
     * List all cocktails by first letter
     * www.thecocktaildb.com/api/json/v1/1/search.php?f=a
     */
    @GET("search.php")
    suspend fun getDrinksByLetter(@Query(value = "f") letter: Char): NetworkDrinksContainer

    /**
     * Search ingredient by name
     * www.thecocktaildb.com/api/json/v1/1/search.php?i=vodka
     */
    @GET("search.php")
    suspend fun getIngredientByName(@Query(value = "i") ingredientName: String): NetworkIngredientsContainer

    /**
     * Lookup full cocktail details by id
     * www.thecocktaildb.com/api/json/v1/1/lookup.php?i=11007
     */
    @GET("lookup.php")
    suspend fun lookupDrinkDetails(@Query(value = "i") drinkId: String): NetworkDrinksContainer

    /**
     * Lookup ingredient by ID
     * www.thecocktaildb.com/api/json/v1/1/lookup.php?iid=552
     */
    @GET("lookup.php")
    suspend fun lookupIngredientDetails(@Query(value = "iid") ingredientId: String): NetworkIngredientsContainer

    /**
     * Lookup a random cocktail
     * www.thecocktaildb.com/api/json/v1/1/random.php
     */
    @GET("random.php")
    suspend fun getRandomDrink(): NetworkDrinksContainer

    /**
     * Search by ingredient
     * www.thecocktaildb.com/api/json/v1/1/filter.php?i=Gin
     */
    @GET("filter.php")
    suspend fun getDrinksWithIngredient(@Query(value = "i") ingredientName: String): NetworkDrinkRefsContainer

    /**
     * Get alcoholic drinks
     * www.thecocktaildb.com/api/json/v1/1/filter.php?a=Alcoholic
     */
    @GET("filter.php?a=Alcoholic")
    suspend fun getAlcoholicDrinks(): NetworkDrinkRefsContainer

    /**
     * Get non-alcoholic drinks
     * www.thecocktaildb.com/api/json/v1/1/filter.php?a=Non_Alcoholic
     */
    @GET("filter.php?a=Non_Alcoholic")
    suspend fun getNonAlcoholicDrinks(): NetworkDrinkRefsContainer

    /**
     * Filter by Category
     * www.thecocktaildb.com/api/json/v1/1/filter.php?c=Ordinary_Drink
     * www.thecocktaildb.com/api/json/v1/1/filter.php?c=Cocktail
     */
    @GET("filter.php")
    suspend fun getDrinksFilteredByCategory(@Query("c") category: String): NetworkDrinkRefsContainer

    /**
     * Filter by Glass
     * www.thecocktaildb.com/api/json/v1/1/filter.php?g=Cocktail_glass
     */
    @GET("filter.php")
    suspend fun getDrinksFilteredByGlass(@Query("g") glass: String): NetworkDrinkRefsContainer

    /*
     * To be implemented if needed:
     * List the categories, glasses, ingredients or alcoholic filters
     * www.thecocktaildb.com/api/json/v1/1/list.php?c=list
     * www.thecocktaildb.com/api/json/v1/1/list.php?g=list
     * www.thecocktaildb.com/api/json/v1/1/list.php?i=list
     * www.thecocktaildb.com/api/json/v1/1/list.php?a=list
     */
}
