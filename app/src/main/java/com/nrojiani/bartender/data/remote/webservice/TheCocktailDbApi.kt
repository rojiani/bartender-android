package com.nrojiani.bartender.data.remote.webservice

object TheCocktailDbApi {
    /**
     * 1 = Developer/test API key
     */
    private const val API_KEY = "1"

    internal const val COCKTAILDB_API_BASE_URL = "https://www.thecocktaildb.com/api/json/v1/$API_KEY/"

    internal const val INGREDIENT_IMAGES_BASE_URL = "https://www.thecocktaildb.com/images/ingredients"
}
