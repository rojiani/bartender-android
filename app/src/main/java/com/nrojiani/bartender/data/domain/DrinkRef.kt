package com.nrojiani.bartender.data.domain

/**
 * Drink model without the full details - see [Drink].
 */
data class DrinkRef(
    val id: String,
    val drinkName: String,
    val imageUrl: String,
)
