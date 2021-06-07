package com.nrojiani.bartender.data.domain

/**
 * Drink filters for whether a drink includes alcohol.
 * See [TheCocktailDB API](https://www.thecocktaildb.com/api.php)
 * [alcoholic filter](www.thecocktaildb.com/api/json/v1/1/list.php?a=list)
 */
enum class AlcoholContent {
    ALCOHOLIC,
    NON_ALCOHOLIC,
    OPTIONAL_ALCOHOL
}
