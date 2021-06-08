package com.nrojiani.bartender.views.search

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nrojiani.bartender.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import io.mockk.verify
import it.czerwinski.android.hilt.fragment.testing.launchFragmentInContainer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SearchFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun testNavigateToCocktailFragment() {
        // GIVEN: SearchFragment is visible
        val scenario = launchFragmentInContainer<SearchFragment>(
            themeResId = R.style.Theme_Bartender
        )
        val mockNavController = mockk<NavController>(relaxUnitFun = true)

        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        // WHEN: Temporary Cocktail button is clicked
        onView(withId(R.id.temporary_mojito_button))
            .perform(click())

        // THEN: Verify navigation to the CocktailFragment screen
        verify {
            mockNavController.navigate(any<NavDirections>())
        }
    }
}
