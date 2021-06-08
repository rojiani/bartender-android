package com.nrojiani.bartender.data.remote.datasource

import com.nrojiani.bartender.data.remote.dto.*
import com.nrojiani.bartender.data.remote.webservice.CocktailsService
import com.nrojiani.bartender.data.test.utils.HttpRequestMethod
import com.nrojiani.bartender.data.test.utils.MockWebServerTest
import com.nrojiani.bartender.data.test.utils.shouldBeHttpMethod
import com.nrojiani.bartender.data.test.utils.shouldHavePath
import com.nrojiani.bartender.di.NetworkModule
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

/**
 * Test TheCocktailDB Retrofit Service using a Mock Web Server that returns
 * JSON responses read from file.
 *
 * Adapted from:
 * [GitHub: Karumi/KataTODOApiClientKotlin - MockWebServerTest.kt](https://github.com/Karumi/KataTODOApiClientKotlin/blob/master/src/test/kotlin/com/karumi/todoapiclient/MockWebServerTest.kt)
 */
class DrinksRemoteDataSourceMockWebServerTest : MockWebServerTest() {

    private enum class MockApiResponse(val filename: String, val code: Int) {
        SEARCH_DRINKS_BY_NAME_SUCCESS("search/name/margarita.json", 200),
        SEARCH_DRINKS_BY_NAME_NO_MATCHES("search/name/no-matches.json", 200),
        SEARCH_BY_LETTER("search/letter/a/a-drinks.json", 200),
        SEARCH_INGREDIENTS_BY_NAME("ingredients/gin.json", 200),
        FILTER_DRINKS_BY_INGREDIENT_SUCCESS("filter/ingredient/gin-drinks.json", 200),
        FILTER_DRINKS_BY_INGREDIENT_NO_MATCHES("filter/ingredient/no-matches.json", 200)
    }

    private lateinit var webService: CocktailsService

    @Before
    override fun setUp() {
        super.setUp()

        webService = Retrofit.Builder()
            .baseUrl(baseEndpoint)
            .client(NetworkModule.provideOkHttpClient())
            .addConverterFactory(NetworkModule.provideMoshiConverterFactory(NetworkModule.provideMoshi()))
            .build()
            .create(CocktailsService::class.java)
    }

    @Test
    fun `getDrinksByName call sends to correct resource path endpoint and deserialized`() {
        enqueueMockApiResponse(MockApiResponse.SEARCH_DRINKS_BY_NAME_SUCCESS)
        runBlocking {
            val dto: NetworkDrinksContainer = webService.getDrinksByName("margarita")
            val margaritas: List<NetworkDrink> = dto.drinks.shouldNotBeNull()
            margaritas.shouldHaveSize(6)
        }
        val recordedRequest = takeRequest()
        recordedRequest.shouldBeHttpMethod(HttpRequestMethod.GET)
        recordedRequest.shouldHavePath("search.php?s=margarita")
    }

    @Test
    fun `getDrinksByName no matches`() {
        enqueueMockApiResponse(MockApiResponse.SEARCH_DRINKS_BY_NAME_NO_MATCHES)
        runBlocking {
            val dto: NetworkDrinksContainer = webService.getDrinksByName("asdf")
            dto.drinks.shouldBeNull()
            dto.toDomainModel().shouldBeEmpty()
        }
        val recordedRequest = takeRequest()
        recordedRequest.shouldBeHttpMethod(HttpRequestMethod.GET)
        recordedRequest.shouldHavePath("search.php?s=asdf")
    }

    @Test
    fun `search by letter`() {
        enqueueMockApiResponse(MockApiResponse.SEARCH_BY_LETTER)
        runBlocking {
            val dto: NetworkDrinksContainer = webService.getDrinksByLetter('a')
            val drinks: List<NetworkDrink> = dto.drinks.shouldNotBeNull()
            drinks.shouldHaveSize(25)
        }
        val recordedRequest = takeRequest()
        recordedRequest.shouldBeHttpMethod(HttpRequestMethod.GET)
        recordedRequest.shouldHavePath("search.php?f=a")
    }

    @Test
    fun `search ingredients - gin`() {
        enqueueMockApiResponse(MockApiResponse.SEARCH_INGREDIENTS_BY_NAME)
        runBlocking {
            val dto: NetworkIngredientsContainer = webService.getIngredientByName("gin")
            val ingredients: List<NetworkIngredient> = dto.ingredients.shouldNotBeNull()
            ingredients.shouldHaveSize(1)
        }
        val recordedRequest = takeRequest()
        recordedRequest.shouldBeHttpMethod(HttpRequestMethod.GET)
        recordedRequest.shouldHavePath("search.php?i=gin")
    }

    @Test
    fun `filterDrinksByIngredient call sends to correct resource path endpoint and deserialized`() {
        enqueueMockApiResponse(MockApiResponse.FILTER_DRINKS_BY_INGREDIENT_SUCCESS)
        runBlocking {
            val dto: NetworkDrinkRefsContainer = webService.getDrinksWithIngredient("Gin")
            val ginDrinks: List<NetworkDrinkRef> = dto.drinks.shouldNotBeNull()
            ginDrinks.shouldHaveSize(100)
        }
        val recordedRequest = takeRequest()
        recordedRequest.shouldBeHttpMethod(HttpRequestMethod.GET)
        recordedRequest.shouldHavePath("filter.php?i=Gin")
    }

    @Test
    fun `filterDrinksByIngredient no matches`() {
        enqueueMockApiResponse(MockApiResponse.FILTER_DRINKS_BY_INGREDIENT_NO_MATCHES)
        runBlocking {
            val dto: NetworkDrinkRefsContainer = webService.getDrinksWithIngredient("asdf")
            dto.drinks.shouldBeNull()
            dto.toDomainModel().shouldBeEmpty()
        }
        val recordedRequest = takeRequest()
        recordedRequest.shouldBeHttpMethod(HttpRequestMethod.GET)
        recordedRequest.shouldHavePath("filter.php?i=asdf")
    }
    private fun enqueueMockApiResponse(mockResponse: MockApiResponse) =
        enqueueMockResponse(code = mockResponse.code, fileName = mockResponse.filename)
}
