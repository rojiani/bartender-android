package com.nrojiani.bartender.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Drink model without the full details - see [Drink].
 */
@Parcelize
data class DrinkRef(
    val id: String,
    val drinkName: String,
    val imageUrl: String,
) : Parcelable
