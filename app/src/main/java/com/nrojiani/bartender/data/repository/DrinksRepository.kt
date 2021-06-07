package com.nrojiani.bartender.data.repository

import com.nrojiani.bartender.data.domain.Drink
import com.nrojiani.bartender.data.domain.DrinkRef
import com.nrojiani.bartender.data.domain.Ingredient
import com.nrojiani.bartender.data.remote.datasource.IDrinksRemoteDataSource
import javax.inject.Inject

/**
 * Repository for drinks data.
 * Currently just delegates to remote data source since DB not implemented yet.
 */
class DrinksRepository @Inject constructor(
    private val remoteDataSource: IDrinksRemoteDataSource
) : IDrinksRepository {

    override suspend fun getDrinksByName(drinkName: String): List<Drink> {
        return remoteDataSource.getDrinksByName(drinkName)
    }

    override suspend fun getDrinksByLetter(letter: Char): List<Drink> {
        return remoteDataSource.getDrinksByLetter(letter)
    }

    override suspend fun getIngredientByName(ingredientName: String): List<Ingredient> {
        return remoteDataSource.getIngredientByName(ingredientName)
    }

    override suspend fun lookupDrinkDetails(drinkId: String): Drink? {
        return remoteDataSource.lookupDrinkDetails(drinkId)
    }

    override suspend fun lookupIngredientDetails(ingredientId: String): Ingredient? {
        return remoteDataSource.lookupIngredientDetails(ingredientId)
    }

    override suspend fun getRandomDrink(): Drink {
        return remoteDataSource.getRandomDrink()
    }

    override suspend fun getDrinksWithIngredient(ingredientName: String): List<DrinkRef> {
        return remoteDataSource.getDrinksWithIngredient(ingredientName)
    }

    override suspend fun getAlcoholicDrinks(): List<DrinkRef> {
        return remoteDataSource.getAlcoholicDrinks()
    }

    override suspend fun getNonAlcoholicDrinks(): List<DrinkRef> {
        return remoteDataSource.getNonAlcoholicDrinks()
    }

    override suspend fun getDrinksFilteredByCategory(category: String): List<DrinkRef> {
        return remoteDataSource.getDrinksFilteredByCategory(category)
    }

    override suspend fun getDrinksFilteredByGlass(glass: String): List<DrinkRef> {
        return remoteDataSource.getDrinksFilteredByGlass(glass)
    }
}
