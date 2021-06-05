package com.nrojiani.bartender.data.remote.dto

import com.nrojiani.bartender.data.domain.IbaCategory
import com.nrojiani.bartender.data.domain.IngredientMeasure
import com.nrojiani.bartender.data.test.utils.NETWORK_MOCKS_PATH
import com.nrojiani.bartender.data.test.utils.readMockJson
import com.nrojiani.bartender.data.test.utils.readMockSearchByDrinkNameJson
import com.nrojiani.bartender.di.NetworkModule
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import net.lachlanmckee.timberjunit.TimberTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.nio.file.Paths

class NetworkDrinkModelsTest {
    private lateinit var moshi: Moshi

    @get:Rule
    var logAllAlwaysRule: TimberTestRule = TimberTestRule.logAllAlways()

    @Before
    fun setUp() {
        moshi = NetworkModule.provideMoshi()
    }

    @Test
    fun test_NetworkDrink_deserialization() {
        val adapter: JsonAdapter<NetworkDrink> = moshi.adapter(NetworkDrink::class.java)
        val networkDrink: NetworkDrink? = adapter.fromJson(NETWORK_DRINK_JSON)
        networkDrink.shouldNotBeNull()
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
    fun test_NetworkDrink_toDomainModel() {
        val adapter: JsonAdapter<NetworkDrinksContainer> =
            moshi.adapter(NetworkDrinksContainer::class.java)
        val networkDrinkResultsJson: String = readMockSearchByDrinkNameJson("gin-and-tonic.json")
        val networkDrinkResults = adapter.fromJson(networkDrinkResultsJson)

        networkDrinkResults.shouldNotBeNull()
        networkDrinkResults.drinks?.shouldHaveSize(1)
        val networkGinAndTonic = networkDrinkResults.drinks?.first()
        val ginAndTonic = networkGinAndTonic?.toDomainModel()
        ginAndTonic.shouldNotBeNull()
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
                ingredientMeasures.shouldBe(
                    listOf(
                        IngredientMeasure("Gin", "2 oz"),
                        IngredientMeasure("Tonic water", "5 oz"),
                        IngredientMeasure("Lime", "1"),
                    )
                )
            }
    }

    @Test
    fun no_search_matches() {
        val adapter: JsonAdapter<NetworkDrinksContainer> =
            moshi.adapter(NetworkDrinksContainer::class.java)
        val networkNoDrinksJson = readMockSearchByDrinkNameJson("no-matches.json")
        val networkDrinkResults = adapter.fromJson(networkNoDrinksJson)

        networkDrinkResults.shouldNotBeNull()
        networkDrinkResults.drinks?.shouldBeEmpty()

        val cocktails = networkDrinkResults.toDomainModel()
        cocktails
            .shouldNotBeNull()
            .shouldBeEmpty()
    }

    @Test
    fun network_drink_refs() {
        val adapter: JsonAdapter<NetworkDrinkRefsContainer> =
            moshi.adapter(NetworkDrinkRefsContainer::class.java)
        val networkDrinkRefsJson: String = readMockJson(
            Paths.get("$NETWORK_MOCKS_PATH/filter/ingredient/gin-drinks.json")
        )
        val networkDrinkRefs = adapter.fromJson(networkDrinkRefsJson)

        networkDrinkRefs.shouldNotBeNull()
        networkDrinkRefs.drinks?.shouldHaveSize(100)
        val ginAndTonicRef = networkDrinkRefs.drinks?.firstOrNull {
            it.drinkName == "Gin And Tonic"
        }
        ginAndTonicRef.shouldNotBeNull()
        ginAndTonicRef.id.shouldBe("11403")
        ginAndTonicRef.imageUrl.shouldBe("https://www.thecocktaildb.com/images/media/drink/z0omyp1582480573.jpg")
    }

    companion object {
        private val NETWORK_DRINK_JSON = readMockJson(
            NETWORK_MOCKS_PATH.resolve("cocktail/gin-and-tonic.json")
        )
    }
}
