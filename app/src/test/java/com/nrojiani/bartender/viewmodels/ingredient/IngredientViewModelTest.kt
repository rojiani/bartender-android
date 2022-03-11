package com.nrojiani.bartender.viewmodels.ingredient

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.nrojiani.bartender.data.Resource
import com.nrojiani.bartender.data.domain.Ingredient
import com.nrojiani.bartender.data.repository.IDrinksRepository
import com.nrojiani.bartender.test.utils.MainCoroutineRule
import com.nrojiani.bartender.test.utils.mocks.Ingredients
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.lachlanmckee.timberjunit.TimberTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import kotlin.time.Duration.Companion.seconds

class IngredientViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var logAllAlwaysRule: TimberTestRule = TimberTestRule.logAllAlways()

    private lateinit var mockRepository: IDrinksRepository
    private lateinit var ingredientViewModel: IngredientViewModel

    @Before
    fun setUp() {
        mockRepository = mockk()
        coEvery {
            mockRepository.getIngredientByName("Sugar")
        } coAnswers {
            listOf(Ingredients.SUGAR)
        }
        ingredientViewModel = IngredientViewModel(SAVED_STATE_HANDLE, mockRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // https://github.com/cashapp/turbine/issues/19
    // https://github.com/spiral123/GM_challenge/search?q=loadArtistData
    // https://github.com/cashapp/turbine/issues/33
    @Test
    fun test_ingredient_loading_then_success() = runTest {
        ingredientViewModel.apply {
            ingredientResource.test {
                expectMostRecentItem().shouldBe(Resource.Loading)

                loadIngredient()

                awaitItem().shouldBeInstanceOf<Resource.Success<Ingredient>>()
                    .data.shouldBe(Ingredients.SUGAR)

                cancelAndConsumeRemainingEvents()
            }

            // Verify data only fetched from network once
            coVerify(exactly = 1) {
                mockRepository.getIngredientByName(any())
            }
        }
    }

    @Test
    fun test_ingredient_fetch_failure() = runTest {
        coEvery {
            mockRepository.getIngredientByName("Sugar")
        } coAnswers {
            throw IOException("Failed to get data")
        }

        ingredientViewModel.apply {

            ingredientResource.test(timeout = 3.seconds) {
                expectMostRecentItem().shouldBe(Resource.Loading)

                loadIngredient()

                awaitItem().shouldBeInstanceOf<Resource.Failure>()
                    .exception
                    .shouldBeInstanceOf<IOException>()
                    .shouldHaveMessage("Failed to get data")
            }
        }
    }

    companion object {
        private val SAVED_STATE_HANDLE = SavedStateHandle(mapOf("ingredientName" to "Sugar"))
    }
}
