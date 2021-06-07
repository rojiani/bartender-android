package com.nrojiani.bartender.data.remote.datasource

import com.nrojiani.bartender.data.remote.dto.NetworkDrinksContainer
import com.nrojiani.bartender.data.remote.dto.toDomainModel
import com.nrojiani.bartender.data.remote.webservice.CocktailsService
import com.nrojiani.bartender.data.test.utils.readMockSearchByDrinkNameJson
import com.nrojiani.bartender.di.NetworkModule
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runBlockingTest
import net.lachlanmckee.timberjunit.TimberTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException

class DrinksRemoteDataSourceTest {

    @get:Rule
    var logAllAlwaysRule: TimberTestRule = TimberTestRule.logAllAlways()

    private val mockWebService = mockk<CocktailsService>()
    private val moshi: Moshi = NetworkModule.provideMoshi()
    private lateinit var remoteDataSource: IDrinksRemoteDataSource

    @Before
    fun setUp() {
        remoteDataSource = DrinksRemoteDataSource(mockWebService)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun throws_when_webService_throws() = runBlockingTest {
        val exception = UnknownHostException("Unable to resolve host")

        coEvery {
            mockWebService.getDrinksByName("gin and tonic")
        } coAnswers {
            throw exception
        }

        shouldThrow<UnknownHostException> {
            remoteDataSource.getDrinksByName("gin and tonic")
        }.message.shouldBe("Unable to resolve host")
    }

    @Test
    fun maps_to_domain_model_when_successful() = runBlockingTest {
        val adapter: JsonAdapter<NetworkDrinksContainer> =
            moshi.adapter(NetworkDrinksContainer::class.java)
        val networkDrinkResultsJson: String = readMockSearchByDrinkNameJson("gin-and-tonic.json")
        val networkDrinkResultsContainer = adapter.fromJson(networkDrinkResultsJson)
            ?: fail("Moshi fromJson returned null")

        coEvery {
            mockWebService.getDrinksByName("gin and tonic")
        } coAnswers {
            networkDrinkResultsContainer
        }

        remoteDataSource.getDrinksByName("gin and tonic")
            .shouldHaveSize(1)
            .shouldBe(networkDrinkResultsContainer.toDomainModel())
    }
}
