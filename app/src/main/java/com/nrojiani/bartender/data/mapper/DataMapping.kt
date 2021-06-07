@file:JvmName("DataMapping")

package com.nrojiani.bartender.data.mapper

import com.nrojiani.bartender.data.domain.IbaCategory
import com.nrojiani.bartender.data.domain.IngredientMeasure
import com.nrojiani.bartender.data.remote.dto.NetworkDrink

internal fun parseTags(commaSeparatedString: String?): List<String> =
    commaSeparatedString?.split(',') ?: emptyList()

internal val NetworkDrink.ingredientMeasures: List<IngredientMeasure>
    get() = allIngredients.zip(allMeasures)
        .map { (ingredient, measure) ->
            ingredient ?: return@map null

            // Some ingredients (like "Salt") don't always have a measure.
            // Measures often have trailing whitespace.
            IngredientMeasure(ingredient, measure?.trim() ?: "to taste")
        }.filterNotNull()

private val NetworkDrink.allIngredients: List<String?>
    get() = listOf(
        this.ingredient1,
        this.ingredient2,
        this.ingredient3,
        this.ingredient4,
        this.ingredient5,
        this.ingredient6,
        this.ingredient7,
        this.ingredient8,
        this.ingredient9,
        this.ingredient10,
        this.ingredient11,
        this.ingredient12,
        this.ingredient13,
        this.ingredient14,
        this.ingredient15,
    )

private val NetworkDrink.allMeasures: List<String?>
    get() = listOf(
        this.measure1,
        this.measure2,
        this.measure3,
        this.measure4,
        this.measure5,
        this.measure6,
        this.measure7,
        this.measure8,
        this.measure9,
        this.measure10,
        this.measure11,
        this.measure12,
        this.measure13,
        this.measure14,
        this.measure15,
    )

fun categorizeIba(strIba: String?): IbaCategory = IbaCategory.values().firstOrNull {
    it.ibaString == strIba
} ?: IbaCategory.NON_IBA
