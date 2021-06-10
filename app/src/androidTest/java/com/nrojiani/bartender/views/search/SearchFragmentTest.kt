package com.nrojiani.bartender.views.search

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nrojiani.bartender.R
import com.nrojiani.bartender.data.domain.DrinkRef
import com.nrojiani.bartender.test.utils.espresso.BaseRobot
import com.nrojiani.bartender.test.utils.espresso.withRecyclerView
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

    private val robot = BaseRobot()

    @Test
    fun when_drink_search_result_is_clicked_navigate_to_the_DrinkFragment() {
        // GIVEN: SearchFragment is visible
        val scenario = launchFragmentInContainer<SearchFragment>(
            themeResId = R.style.Theme_Bartender
        )
        val mockNavController = mockk<NavController>(relaxUnitFun = true)

        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        // Wait for network fetch
        onView(withId(R.id.search_drink_by_name_text_input))
            .perform(typeText("gin and tonic"))
            .perform(closeSoftKeyboard())

        // WHEN: A Drink list item is clicked
        robot.waitForView(
            withRecyclerView(R.id.drink_search_results_list)
                .atPositionOnView(0, R.id.drink_name_text),
            ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
        ).perform(click())

        // THEN: Verify navigation to the DrinkFragment screen
        verify {
            mockNavController.navigate(
                SearchFragmentDirections.actionSearchFragmentToDrinkFragment(
                    DrinkRef(
                        id = "11403",
                        drinkName = "Gin And Tonic",
                        imageUrl = "https://www.thecocktaildb.com/images/media/drink/z0omyp1582480573.jpg",
                    )
                )
            )
        }
    }
}
