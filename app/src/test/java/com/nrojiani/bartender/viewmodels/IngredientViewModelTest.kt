package com.nrojiani.bartender.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.nrojiani.bartender.data.Resource
import com.nrojiani.bartender.data.domain.Ingredient
import com.nrojiani.bartender.data.repository.IDrinksRepository
import com.nrojiani.bartender.test.utils.MainCoroutineRule
import com.nrojiani.bartender.test.utils.livedata.getOrAwaitValue
import com.nrojiani.bartender.test.utils.mocks.Ingredients
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.lachlanmckee.timberjunit.TimberTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

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

    @Test
    fun test_ingredient_loading_then_success_livedata() {
        ingredientViewModel.ingredientResource
            .getOrAwaitValue()
            .shouldBe(Resource.Loading)

        val success: Resource<Ingredient> = ingredientViewModel.ingredientResource.getOrAwaitValue()
        success.shouldBeInstanceOf<Resource.Success<Ingredient>>()
            .data.shouldBe(Ingredients.SUGAR)

        // Ingredient description
        ingredientViewModel.description
            .getOrAwaitValue()
            .shouldBe(
                "Sugar is the generic name for sweet-tasting, soluble carbohydrates, many of which are used in food."
            )

        // Verify data only fetched from network once
        coVerify(exactly = 1) {
            mockRepository.getIngredientByName(any())
        }
    }

    @Test
    fun test_ingredient_fetch_failure_livedata() {
        coEvery {
            mockRepository.getIngredientByName("Sugar")
        } coAnswers { throw IOException("Failed to get data") }

        ingredientViewModel.ingredientResource
            .getOrAwaitValue()
            .shouldBe(Resource.Loading)

        val failure = ingredientViewModel.ingredientResource
            .getOrAwaitValue()
            .shouldBeInstanceOf<Resource.Failure>()

        failure.exception
            .shouldBeInstanceOf<IOException>()
            .shouldHaveMessage("Failed to get data")
    }

    companion object {
        private val SAVED_STATE_HANDLE = SavedStateHandle(mapOf("ingredientName" to "Sugar"))
    }
}
