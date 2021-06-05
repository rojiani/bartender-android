package com.nrojiani.bartender.data.remote.dto

import com.nrojiani.bartender.data.domain.Ingredient
import com.squareup.moshi.Json

/**
 * Response returned by:
 *
 * * [Search ingredient by name](www.thecocktaildb.com/api/json/v1/1/search.php?i=vodka)
 * * [Lookup Ingredient by id](www.thecocktaildb.com/api/json/v1/1/lookup.php?iid=552)
 */
data class NetworkIngredientsContainer(
    val ingredients: List<NetworkIngredient>?
)

/**
 * DTO for an ingredient.
 */
data class NetworkIngredient(
    @Json(name = "idIngredient")
    val id: String,

    @Json(name = "strIngredient")
    val name: String,

    @Json(name = "strDescription")
    val description: String,

    @Json(name = "strABV")
    val abv: String?,

    /**
     * "Yes" or null...
     */
    @Json(name = "strAlcohol")
    val alcohol: String?,

    @Json(name = "strType")
    val type: String?
)

fun NetworkIngredient.toDomainModel(): Ingredient = Ingredient(
    id = id,
    name = name,
    description = description,
    abv = abv ?: "0",
    alcoholic = alcohol.equals("Yes", ignoreCase = true),
    type = type.orEmpty()
)

fun NetworkIngredientsContainer.toDomainModel(): List<Ingredient> =
    ingredients?.map { it.toDomainModel() } ?: emptyList()
