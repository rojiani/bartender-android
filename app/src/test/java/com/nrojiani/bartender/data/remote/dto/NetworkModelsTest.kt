package com.nrojiani.bartender.data.remote.dto

import com.nrojiani.bartender.data.domain.IbaCategory
import com.nrojiani.bartender.data.domain.Ingredient
import com.nrojiani.bartender.data.test.utils.NETWORK_MOCKS_PATH
import com.nrojiani.bartender.data.test.utils.readMockJson
import com.nrojiani.bartender.data.test.utils.readMockSearchByDrinkNameJson
import com.nrojiani.bartender.di.NetworkModule
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import net.lachlanmckee.timberjunit.TimberTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NetworkModelsTest {
    private lateinit var moshi: Moshi

    @get:Rule
    var logAllAlwaysRule: TimberTestRule = TimberTestRule.logAllAlways()

    @Before
    fun setUp() {
        moshi = NetworkModule.provideMoshi()
    }

    @Test
    fun test_NetworkCocktail_deserialization() {
        val adapter: JsonAdapter<NetworkCocktail> = moshi.adapter(NetworkCocktail::class.java)
        val networkCocktail: NetworkCocktail? = adapter.fromJson(NETWORK_COCKTAIL_JSON)
        networkCocktail.shouldNotBeNull()
            .apply {
                id.shouldBe("11403")
                drinkName.shouldBe("Gin And Tonic")
                category.shouldBe("Ordinary Drink")
                iba.shouldBeNull()
                alcoholic.shouldBe("Alcoholic")
                glass.shouldBe("Highball glass")
                instructions.shouldBe(
                    "Pour the gin and the tonic water into a highball " +
                        "glass almost filled with ice cubes. Stir well. Garnish with the lime wedge."
                )
                imageUrl.shouldBe("https://www.thecocktaildb.com/images/media/drink/z0omyp1582480573.jpg")
                videoUrl.shouldBeNull()
                ingredient1.shouldBe("Gin")
                ingredient2.shouldBe("Tonic water")
                ingredient3.shouldBe("Lime")
                measure1.shouldBe("2 oz ")
                measure2.shouldBe("5 oz ")
                measure3.shouldBe("1 ")
            }
    }

    @Test
    fun test_NetworkCocktail_toDomainModel() {
        val adapter: JsonAdapter<NetworkCocktailSearchResults> =
            moshi.adapter(NetworkCocktailSearchResults::class.java)
        val networkCocktailResultsJson: String = readMockSearchByDrinkNameJson("gin-and-tonic.json")
        val networkCocktailResults = adapter.fromJson(networkCocktailResultsJson)

        networkCocktailResults.shouldNotBeNull()
        networkCocktailResults.drinks.shouldHaveSize(1)
        val networkGinAndTonic = networkCocktailResults.drinks.first()
        val ginAndTonic = networkGinAndTonic.toDomainModel()
        ginAndTonic
            .apply {
                id.shouldBe("11403")
                drinkName.shouldBe("Gin And Tonic")
                ibaCategory.shouldBe(IbaCategory.NON_IBA)
                alcoholic.shouldBe("Alcoholic")
                glass.shouldBe("Highball glass")
                instructions.shouldBe(
                    "Pour the gin and the tonic water into a " +
                        "highball glass almost filled with ice cubes. Stir well. Garnish with the lime wedge."
                )
                imageUrl.shouldBe("https://www.thecocktaildb.com/images/media/drink/z0omyp1582480573.jpg")
                videoUrl.shouldBeEmpty()
                ingredients.shouldBe(
                    listOf(
                        Ingredient("Gin", "2 oz"),
                        Ingredient("Tonic water", "5 oz"),
                        Ingredient("Lime", "1"),
                    )
                )
            }
    }

    companion object {
        private val NETWORK_COCKTAIL_JSON = readMockJson(
            NETWORK_MOCKS_PATH.resolve("cocktail/gin-and-tonic.json")
        )
    }
}
