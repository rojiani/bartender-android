@file:JvmName("NetworkDrinkModels")

package com.nrojiani.bartender.data.remote.dto

import com.nrojiani.bartender.data.domain.Drink
import com.nrojiani.bartender.data.domain.DrinkRef
import com.nrojiani.bartender.data.mapper.categorizeIba
import com.nrojiani.bartender.data.mapper.ingredientMeasures
import com.nrojiani.bartender.data.mapper.parseTags
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object (DTO) for search by cocktail name result:
 * `https://www.thecocktaildb.com/api/json/v1/1/search.php?s=${drink name}`
 */
@JsonClass(generateAdapter = true)
data class NetworkDrinksContainer(
    val drinks: List<NetworkDrink>?
)

/**
 * DTO for a single cocktail.
 */
@JsonClass(generateAdapter = true)
data class NetworkDrink(

    @Json(name = "idDrink")
    val id: String,

    @Json(name = "strDrink")
    val drinkName: String,

    @Json(name = "strDrinkAlternate")
    val alternateDrinkName: String?,

    @Json(name = "strTags")
    val tags: String?,

    @Json(name = "strVideo")
    val videoUrl: String?,

    @Json(name = "strCategory")
    val category: String?,

    @Json(name = "strIBA")
    val iba: String?,

    @Json(name = "strAlcoholic")
    val alcoholic: String?,

    @Json(name = "strGlass")
    val glass: String?,

    @Json(name = "strInstructions")
    val instructions: String?,

    @Json(name = "strDrinkThumb")
    val imageUrl: String?,

    @Json(name = "strIngredient1")
    val ingredient1: String?,

    @Json(name = "strIngredient2")
    val ingredient2: String?,

    @Json(name = "strIngredient3")
    val ingredient3: String?,

    @Json(name = "strIngredient4")
    val ingredient4: String?,

    @Json(name = "strIngredient5")
    val ingredient5: String?,

    @Json(name = "strIngredient6")
    val ingredient6: String?,

    @Json(name = "strIngredient7")
    val ingredient7: String?,

    @Json(name = "strIngredient8")
    val ingredient8: String?,

    @Json(name = "strIngredient9")
    val ingredient9: String?,

    @Json(name = "strIngredient10")
    val ingredient10: String?,

    @Json(name = "strIngredient11")
    val ingredient11: String?,

    @Json(name = "strIngredient12")
    val ingredient12: String?,

    @Json(name = "strIngredient13")
    val ingredient13: String?,

    @Json(name = "strIngredient14")
    val ingredient14: String?,

    @Json(name = "strIngredient15")
    val ingredient15: String?,

    @Json(name = "strMeasure1")
    val measure1: String?,

    @Json(name = "strMeasure2")
    val measure2: String?,

    @Json(name = "strMeasure3")
    val measure3: String?,

    @Json(name = "strMeasure4")
    val measure4: String?,

    @Json(name = "strMeasure5")
    val measure5: String?,

    @Json(name = "strMeasure6")
    val measure6: String?,

    @Json(name = "strMeasure7")
    val measure7: String?,

    @Json(name = "strMeasure8")
    val measure8: String?,

    @Json(name = "strMeasure9")
    val measure9: String?,

    @Json(name = "strMeasure10")
    val measure10: String?,

    @Json(name = "strMeasure11")
    val measure11: String?,

    @Json(name = "strMeasure12")
    val measure12: String?,

    @Json(name = "strMeasure13")
    val measure13: String?,

    @Json(name = "strMeasure14")
    val measure14: String?,

    @Json(name = "strMeasure15")
    val measure15: String?,
)

fun NetworkDrink.toDomainModel(): Drink =
    Drink(
        id = id,
        drinkName = drinkName,
        alternateDrinkName = alternateDrinkName.orEmpty(),
        tags = parseTags(tags),
        videoUrl = videoUrl.orEmpty(),
        ibaCategory = categorizeIba(iba),
        alcoholic = alcoholic.orEmpty(),
        glass = glass.orEmpty(),
        instructions = instructions.orEmpty(),
        imageUrl = imageUrl.orEmpty(),
        ingredientMeasures = ingredientMeasures
    )

fun NetworkDrinksContainer.toDomainModel(): List<Drink> =
    drinks?.map { it.toDomainModel() } ?: emptyList()

/**
 * Results for filter endpoints.
 */
@JsonClass(generateAdapter = true)
data class NetworkDrinkRefsContainer(
    val drinks: List<NetworkDrinkRef>?
)

data class NetworkDrinkRef(
    @Json(name = "idDrink")
    val id: String,

    @Json(name = "strDrink")
    val drinkName: String,

    @Json(name = "strDrinkThumb")
    val imageUrl: String?,
)

fun NetworkDrinkRef.toDomainModel(): DrinkRef =
    DrinkRef(
        id = id,
        drinkName = drinkName,
        imageUrl = imageUrl.orEmpty(),
    )

fun NetworkDrinkRefsContainer.toDomainModel(): List<DrinkRef> =
    drinks?.map { it.toDomainModel() } ?: emptyList()
