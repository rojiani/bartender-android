package com.nrojiani.bartender.data.remote.datasource

import com.nrojiani.bartender.data.domain.Drink
import com.nrojiani.bartender.data.domain.DrinkRef
import com.nrojiani.bartender.data.domain.Ingredient
import com.nrojiani.bartender.data.remote.dto.toDomainModel
import com.nrojiani.bartender.data.remote.webservice.CocktailsService
import com.squareup.moshi.JsonDataException
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * Retrieves Drink data vended by a web service.
 *
 * @param webService Retrofit service for drink data.
 */
class DrinksRemoteDataSource @Inject constructor(
    private val webService: CocktailsService
) : IDrinksRemoteDataSource {

    /**
     * Search drink by name
     *
     * @throws HttpException for unexpected, non-2xx HTTP response (error responses from server).
     * @throws IOException for various network-related exceptions where the server can't be reached.
     * @throws JsonDataException when an error occurs during JSON deserialization
     */
    override suspend fun getDrinksByName(drinkName: String): List<Drink> {
        Timber.d("Searching for drinks with name $drinkName")
        return webService.getDrinksByName(drinkName).toDomainModel()
            .also {
                Timber.i("Fetched ${it.size} drinks from web service")
            }
    }

    /**
     * List all drinks by first letter
     *
     * @throws HttpException for unexpected, non-2xx HTTP response (error responses from server).
     * @throws IOException for various network-related exceptions where the server can't be reached.
     * @throws JsonDataException when an error occurs during JSON deserialization
     */
    override suspend fun getDrinksByLetter(letter: Char): List<Drink> {
        Timber.d("Searching for drinks starting with letter $letter")
        return webService.getDrinksByLetter(letter).toDomainModel()
            .also {
                Timber.i("Fetched ${it.size} drinks from web service")
            }
    }

    /**
     * Search ingredient by name
     *
     * @throws HttpException for unexpected, non-2xx HTTP response (error responses from server).
     * @throws IOException for various network-related exceptions where the server can't be reached.
     * @throws JsonDataException when an error occurs during JSON deserialization
     */
    override suspend fun getIngredientByName(ingredientName: String): List<Ingredient> {
        Timber.d("Searching for ingredients with name $ingredientName")
        return webService.getIngredientByName(ingredientName).toDomainModel()
            .also {
                Timber.i("Fetched ${it.size} ingredient(s) from web service")
            }
    }

    /**
     * Lookup full drink details by id
     *
     * @throws HttpException for unexpected, non-2xx HTTP response (error responses from server).
     * @throws IOException for various network-related exceptions where the server can't be reached.
     * @throws JsonDataException when an error occurs during JSON deserialization
     */
    override suspend fun lookupDrinkDetails(drinkId: String): Drink? {
        Timber.d("Looking up details for drink with id $drinkId")
        val drinks: List<Drink> = webService.lookupDrinkDetails(drinkId).toDomainModel()
        Timber.i("Fetched ${drinks.size} drink(s) from web service")
        return drinks.firstOrNull()
    }

    /**
     * Lookup ingredient by ID
     *
     * @throws HttpException for unexpected, non-2xx HTTP response (error responses from server).
     * @throws IOException for various network-related exceptions where the server can't be reached.
     * @throws JsonDataException when an error occurs during JSON deserialization
     */
    override suspend fun lookupIngredientDetails(ingredientId: String): Ingredient? {
        Timber.d("Looking up details for ingredient with id $ingredientId")
        val ingredients: List<Ingredient> = webService.lookupIngredientDetails(ingredientId).toDomainModel()
        Timber.i("Fetched ${ingredients.size} ingredient(s) from web service")
        return ingredients.firstOrNull()
    }

    /**
     * Lookup a random drink
     *
     * @throws HttpException for unexpected, non-2xx HTTP response (error responses from server).
     * @throws IOException for various network-related exceptions where the server can't be reached.
     * @throws JsonDataException when an error occurs during JSON deserialization
     * @throws NoSuchElementException if the server returns an empty list (this should not happen)
     */
    override suspend fun getRandomDrink(): Drink {
        Timber.d("Fetching a random drink...")
        val drinks: List<Drink> = webService.getRandomDrink().toDomainModel()
        Timber.d("Fetched a random drink from web service")
        return drinks.first()
    }

    /**
     * Search for drinks containing an ingredient
     *
     * @throws HttpException for unexpected, non-2xx HTTP response (error responses from server).
     * @throws IOException for various network-related exceptions where the server can't be reached.
     * @throws JsonDataException when an error occurs during JSON deserialization
     */
    override suspend fun getDrinksWithIngredient(ingredientName: String): List<DrinkRef> {
        Timber.d("Getting drinks with ingredient $ingredientName")
        return webService.getDrinksWithIngredient(ingredientName).toDomainModel()
            .also {
                Timber.d("Fetched ${it.size} drinks from web service")
            }
    }

    /**
     * Get alcoholic drinks
     *
     * @throws HttpException for unexpected, non-2xx HTTP response (error responses from server).
     * @throws IOException for various network-related exceptions where the server can't be reached.
     * @throws JsonDataException when an error occurs during JSON deserialization
     */
    override suspend fun getAlcoholicDrinks(): List<DrinkRef> {
        Timber.d("Fetching alcoholic drinks")
        return webService.getAlcoholicDrinks().toDomainModel()
            .also {
                Timber.d("Fetched ${it.size} drinks from web service")
            }
    }

    /**
     * Get non-alcoholic drinks
     *
     * @throws HttpException for unexpected, non-2xx HTTP response (error responses from server).
     * @throws IOException for various network-related exceptions where the server can't be reached.
     * @throws JsonDataException when an error occurs during JSON deserialization
     */
    override suspend fun getNonAlcoholicDrinks(): List<DrinkRef> {
        Timber.d("Fetching non-alcoholic drinks")
        return webService.getNonAlcoholicDrinks().toDomainModel()
            .also {
                Timber.d("Fetched ${it.size} non-alcoholic drinks from web service")
            }
    }

    /**
     * Filter by Category
     *
     * @throws HttpException for unexpected, non-2xx HTTP response (error responses from server).
     * @throws IOException for various network-related exceptions where the server can't be reached.
     * @throws JsonDataException when an error occurs during JSON deserialization
     */
    override suspend fun getDrinksFilteredByCategory(category: String): List<DrinkRef> {
        Timber.d("Fetching drinks with category $category")
        return webService.getDrinksFilteredByCategory(category).toDomainModel()
            .also {
                Timber.d("Fetched ${it.size} drinks with category $category from web service")
            }
    }

    /**
     * Filter by Glass
     *
     * @throws HttpException for unexpected, non-2xx HTTP response (error responses from server).
     * @throws IOException for various network-related exceptions where the server can't be reached.
     * @throws JsonDataException when an error occurs during JSON deserialization
     */
    override suspend fun getDrinksFilteredByGlass(glass: String): List<DrinkRef> {
        Timber.d("Fetching drinks with glass $glass")
        return webService.getDrinksFilteredByCategory(glass).toDomainModel()
            .also {
                Timber.d("Fetched ${it.size} drinks with glass $glass from web service")
            }
    }
}
