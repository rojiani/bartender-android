package com.nrojiani.bartender.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.nrojiani.bartender.data.Resource
import com.nrojiani.bartender.data.domain.Drink
import com.nrojiani.bartender.data.remote.dto.NetworkDrinksContainer
import com.nrojiani.bartender.data.remote.dto.toDomainModel
import com.nrojiani.bartender.data.repository.IDrinksRepository
import com.nrojiani.bartender.test.utils.MainCoroutineScopeRule
import com.nrojiani.bartender.test.utils.mocks.fromMockJson
import com.nrojiani.bartender.utils.connectivity.NetworkStatus
import com.nrojiani.bartender.utils.connectivity.NetworkStatusMonitor
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import net.lachlanmckee.timberjunit.TimberTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class SearchViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineScopeRule = MainCoroutineScopeRule()

    @get:Rule
    var logAllAlwaysRule: TimberTestRule = TimberTestRule.logAllAlways()

    private val mockRepository = mockk<IDrinksRepository>()
    private val mockNetworkStatusMonitor = mockk<NetworkStatusMonitor>()

    @Before
    fun setUp() {
        every { mockNetworkStatusMonitor.networkEventsFlow } returns flow {
            emit(NetworkStatus.UNDETERMINED)
            emit(NetworkStatus.CONNECTED)
        }

        coEvery {
            mockRepository.getDrinksByName("")
        } answers {
            EMPTY_STRING_DRINKS_RESULTS
        }

        coEvery {
            mockRepository.getDrinksByName("gin and tonic")
        } answers {
            GIN_AND_TONIC_RESULTS
        }

        coEvery {
            mockRepository.getDrinksByName("gin and toni")
        } answers {
            emptyList()
        }
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun network_status_stateflow() = mainCoroutineScopeRule.dispatcher.runBlockingTest {
        every { mockNetworkStatusMonitor.networkEventsFlow } returns flow {
            // Delay so that not emitted before VM creation/subscription
            delay(999)
            emit(NetworkStatus.UNDETERMINED)
            emit(NetworkStatus.CONNECTED)
            emit(NetworkStatus.NOT_CONNECTED)
            emit(NetworkStatus.CONNECTED)
        }

        val searchViewModel = SearchViewModel(mockRepository, mockNetworkStatusMonitor)

        searchViewModel.networkStatus.test {
            awaitItem().shouldBe(NetworkStatus.UNDETERMINED)
            awaitItem().shouldBe(NetworkStatus.CONNECTED)
            awaitItem().shouldBe(NetworkStatus.NOT_CONNECTED)
            awaitItem().shouldBe(NetworkStatus.CONNECTED)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun network_status_stateflow_gets_last_value_of_hot_flow() =
        mainCoroutineScopeRule.dispatcher.runBlockingTest {
            every { mockNetworkStatusMonitor.networkEventsFlow } returns flow {
                emit(NetworkStatus.UNDETERMINED)
                emit(NetworkStatus.CONNECTED)
                emit(NetworkStatus.NOT_CONNECTED)
                emit(NetworkStatus.CONNECTED)
                emit(NetworkStatus.NOT_CONNECTED)
                emit(NetworkStatus.CONNECTED)
                emit(NetworkStatus.NOT_CONNECTED)
                emit(NetworkStatus.CONNECTED)
            }

            val searchViewModel = SearchViewModel(mockRepository, mockNetworkStatusMonitor)

            searchViewModel.networkStatus.test {
                awaitItem().shouldBe(NetworkStatus.CONNECTED)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun search_by_name_results_flow() = mainCoroutineScopeRule.dispatcher.runBlockingTest {
        val searchViewModel = SearchViewModel(mockRepository, mockNetworkStatusMonitor)

        searchViewModel.drinkNameSearchResource.test {
            searchViewModel.searchForDrinksByName()
            awaitItem().shouldBe(Resource.Loading)
            val drinkSearchResource: Resource<List<Drink>> = awaitItem()
            drinkSearchResource.apply {
                shouldBe(Resource.Success(EMPTY_STRING_DRINKS_RESULTS))
                dataOrNull()?.shouldHaveSize(6)
            }

            // when search text changes, results updated
            searchViewModel.drinkQueryTextChanged("gin and tonic")
            awaitItem().shouldBe(Resource.Loading)
            awaitItem().shouldBe(Resource.Success(GIN_AND_TONIC_RESULTS))

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun search_by_name_results_are_cached() = mainCoroutineScopeRule.dispatcher.runBlockingTest {
        val searchViewModel = SearchViewModel(mockRepository, mockNetworkStatusMonitor)

        searchViewModel.drinkNameSearchResource.test {
            // when search text changes, results updated
            searchViewModel.drinkQueryTextChanged("gin and tonic")
            awaitItem().shouldBe(Resource.Loading)
            awaitItem().shouldBe(Resource.Success(GIN_AND_TONIC_RESULTS))

            searchViewModel.drinkQueryTextChanged("gin and toni")
            awaitItem().shouldBe(Resource.Loading)
            awaitItem().dataOrNull().shouldNotBeNull()

            searchViewModel.drinkQueryTextChanged("gin and tonic")
            // no loading...
            // awaitItem().shouldBe(Resource.Loading)
            awaitItem().shouldBe(Resource.Success(GIN_AND_TONIC_RESULTS))

            // verify network not called twice (first result is cached)
            coVerify(exactly = 1) {
                mockRepository.getDrinksByName("gin and tonic")
            }

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun search_by_name_results_failed() = mainCoroutineScopeRule.dispatcher.runBlockingTest {
        val networkError = IOException("Failed to get data")
        coEvery {
            mockRepository.getDrinksByName("")
        } throws networkError

        val searchViewModel = SearchViewModel(mockRepository, mockNetworkStatusMonitor)

        searchViewModel.drinkNameSearchResource.test {
            // when search text changes, results updated
            searchViewModel.searchForDrinksByName()
            awaitItem().shouldBe(Resource.Loading)
            awaitItem().shouldBe(Resource.Failure(networkError))
            cancelAndConsumeRemainingEvents()
        }
    }

    companion object {
        private val EMPTY_STRING_DRINKS_RESULTS: List<Drink> = fromMockJson<NetworkDrinksContainer>(
            mocksRelativePath = "search/name/empty-string.json"
        ).toDomainModel()

        private val GIN_AND_TONIC_RESULTS = fromMockJson<NetworkDrinksContainer>(
            mocksRelativePath = "search/name/gin-and-tonic.json"
        ).toDomainModel()
    }
}
