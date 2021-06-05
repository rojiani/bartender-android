package com.nrojiani.bartender.data.remote.dto

import com.nrojiani.bartender.data.test.utils.NETWORK_MOCKS_PATH
import com.nrojiani.bartender.data.test.utils.readMockJson
import com.nrojiani.bartender.di.NetworkModule
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import net.lachlanmckee.timberjunit.TimberTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.nio.file.Paths

class NetworkIngredientModelsTest {

    private lateinit var moshi: Moshi

    @get:Rule
    var logAllAlwaysRule: TimberTestRule = TimberTestRule.logAllAlways()

    @Before
    fun setUp() {
        moshi = NetworkModule.provideMoshi()
    }

    @Test
    fun network_ingredients_container() {
        val adapter: JsonAdapter<NetworkIngredientsContainer> =
            moshi.adapter(NetworkIngredientsContainer::class.java)

        val networkIngredientsJson: String = readIngredientByNameMockJson("gin.json")
        val networkIngredientsContainer = adapter.fromJson(networkIngredientsJson)

        networkIngredientsContainer.shouldNotBeNull()
        networkIngredientsContainer.ingredients?.shouldHaveSize(1)
        val networkGin = networkIngredientsContainer.ingredients?.first()
        networkGin.shouldNotBeNull()
        networkGin.apply {
            id.shouldBe("2")
            name.shouldBe("Gin")
            type.shouldBe("Gin")
            alcohol.shouldBe("Yes")
            abv.shouldBe("40")
        }

        val gin = networkGin?.toDomainModel()
        gin.shouldNotBeNull()
            .apply {
                id.shouldBe("2")
                name.shouldBe("Gin")
                type.shouldBe("Gin")
                alcoholic.shouldBeTrue()
                abv.shouldBe("40")
            }
    }

    @Test
    fun no_search_matches() {
        val adapter: JsonAdapter<NetworkIngredientsContainer> =
            moshi.adapter(NetworkIngredientsContainer::class.java)

        val networkNoIngredientsJson = """{ "ingredients": null }"""
        val networkIngredientsContainer = adapter.fromJson(networkNoIngredientsJson)

        networkIngredientsContainer.shouldNotBeNull()
        networkIngredientsContainer.ingredients?.shouldBeEmpty()

        val ingredients = networkIngredientsContainer.toDomainModel()
        ingredients
            .shouldNotBeNull()
            .shouldBeEmpty()
    }

    private fun readIngredientByNameMockJson(mockFilename: String): String =
        readMockJson(Paths.get("$NETWORK_MOCKS_PATH/ingredients/$mockFilename"))
}
