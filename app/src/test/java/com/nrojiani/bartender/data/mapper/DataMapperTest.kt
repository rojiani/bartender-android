package com.nrojiani.bartender.data.mapper

import com.nrojiani.bartender.data.domain.IbaCategory
import io.kotest.matchers.shouldBe
import org.junit.Test

class DataMapperTest {

    @Test
    fun parseTags_multiple() {
        parseTags("IBA,ContemporaryClassic")
            .shouldBe(
                listOf("IBA", "ContemporaryClassic")
            )
    }

    @Test
    fun parseTags_single() {
        parseTags("ContemporaryClassic")
            .shouldBe(listOf("ContemporaryClassic"))
    }

    @Test
    fun parseTags_null() {
        parseTags(null).shouldBe(emptyList())
    }

    @Test
    fun categorizeIba_iba_categories() {
        categorizeIba("Contemporary Classics").shouldBe(IbaCategory.IBA_CONTEMPORARY_CLASSICS)
        categorizeIba("New Era Drinks").shouldBe(IbaCategory.IBA_NEW_ERA_DRINKS)
        categorizeIba("Unforgettables").shouldBe(IbaCategory.IBA_UNFORGETTABLES)
    }

    @Test
    fun categorizeIba_non_iba() {
        categorizeIba(null).shouldBe(IbaCategory.NON_IBA)
        categorizeIba("Ordinary Drink").shouldBe(IbaCategory.NON_IBA)
    }
}
