package com.nrojiani.bartender.viewmodels.drink

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.nrojiani.bartender.data.Resource
import com.nrojiani.bartender.data.domain.Drink
import com.nrojiani.bartender.data.domain.IngredientMeasure
import com.nrojiani.bartender.data.remote.dto.NetworkDrinksContainer
import com.nrojiani.bartender.data.remote.dto.toDomainModel
import com.nrojiani.bartender.data.repository.IDrinksRepository
import com.nrojiani.bartender.test.utils.MainCoroutineRule
import com.nrojiani.bartender.test.utils.mocks.DrinkRefs
import com.nrojiani.bartender.test.utils.mocks.fromMockJson
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import net.lachlanmckee.timberjunit.TimberTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class DrinkViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var logAllAlwaysRule: TimberTestRule = TimberTestRule.logAllAlways()

    private val mockRepository = mockk<IDrinksRepository>()

    private lateinit var drinkViewModel: DrinkViewModel

    @Before
    fun setUp() {
        coEvery {
            mockRepository.lookupDrinkDetails(DRINK_REF.id)
        } coAnswers {
            delay(999)
            DRINK_DATA
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun fragment_args_from_saved_state_handle() =
        runTest {
            drinkViewModel = DrinkViewModel(savedStateHandle = SAVED_STATE_HANDLE, mockRepository)

            drinkViewModel.drinkRef.shouldBe(DRINK_REF)
        }

    @Test
    fun drink_data_fetched_successfully() = runTest {
        drinkViewModel = DrinkViewModel(savedStateHandle = SAVED_STATE_HANDLE, mockRepository)

        drinkViewModel.drinkResource.test {
            awaitItem().shouldBe(Resource.Loading)
            awaitItem().shouldBe(Resource.Success(DRINK_DATA))

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun drink_data_fetched_failed() = runTest {
        val networkError = IOException("Failed to get data")
        coEvery {
            mockRepository.lookupDrinkDetails(DRINK_REF.id)
        } coAnswers {
            delay(999)
            throw networkError
        }
        drinkViewModel = DrinkViewModel(savedStateHandle = SAVED_STATE_HANDLE, mockRepository)

        drinkViewModel.drinkResource.test {
            awaitItem().shouldBe(Resource.Loading)
            awaitItem().shouldBe(Resource.Failure(networkError))

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun ingredient_measures() = runTest {
        drinkViewModel = DrinkViewModel(savedStateHandle = SAVED_STATE_HANDLE, mockRepository)

        drinkViewModel.ingredientMeasures.test {
            awaitItem().shouldBeEmpty() // initial state: emptyList()

            awaitItem().shouldBe(
                listOf(
                    IngredientMeasure("Gin", "2 oz"),
                    IngredientMeasure("Lemon juice", "1 oz"),
                    IngredientMeasure("Sugar", "1/2 tsp superfine"),
                    IngredientMeasure("Orange", "1"),
                    IngredientMeasure("Maraschino cherry", "1"),
                )
            )

            cancelAndConsumeRemainingEvents()
        }
    }

    companion object {
        private val DRINK_REF = DrinkRefs.GIN_SOUR
        private val DRINK_DATA: Drink? = fromMockJson<NetworkDrinksContainer>(
            mocksRelativePath = "cocktail/gin-sour.json"
        ).toDomainModel().firstOrNull()

        private val SAVED_STATE_HANDLE = SavedStateHandle(mapOf("drinkRef" to DRINK_REF))
    }
}
