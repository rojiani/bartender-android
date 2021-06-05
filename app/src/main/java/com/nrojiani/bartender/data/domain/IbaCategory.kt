package com.nrojiani.bartender.data.domain

/**
 * [International Bartenders Association (IBA) Categories](https://en.wikipedia.org/wiki/List_of_IBA_official_cocktails)
 */
enum class IbaCategory(val ibaString: String) {
    IBA_UNFORGETTABLES("Unforgettables"),
    IBA_CONTEMPORARY_CLASSICS("Contemporary Classics"),
    IBA_NEW_ERA_DRINKS("New Era Drinks"),
    NON_IBA("")
}
