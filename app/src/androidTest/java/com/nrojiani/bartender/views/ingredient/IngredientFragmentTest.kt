package com.nrojiani.bartender.views.ingredient

import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nrojiani.bartender.R
import com.nrojiani.bartender.test.utils.espresso.BaseRobot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import it.czerwinski.android.hilt.fragment.testing.launchFragmentInContainer
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class IngredientFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var robot = BaseRobot()

    @Test
    fun test_IngredientFragment_ui_with_full_ingredient_details() {
        val fragmentArgs = bundleOf("ingredientName" to "Vodka")

        launchFragmentInContainer<IngredientFragment>(
            fragmentArgs = fragmentArgs,
            themeResId = R.style.Theme_Bartender
        )

        // Ingredient title isn't fetched, should be immediately visible
        onView(withId(R.id.ingredient_title))
            .check(matches(isDisplayed()))

        // Wait for network fetch
        robot.assertOnView(
            viewMatcher = withId(R.id.ingredient_description_text),
            initialViewAssertion = matches(withEffectiveVisibility(Visibility.VISIBLE)),
            matches(
                allOf(
                    isDisplayed(),
                    withText(containsString("Vodka is a distilled beverage")),
                    withText(containsString("Everclear, an American brand, is also sold at 95% ABV."))
                )
            )
        )

        onView(withId(R.id.ingredient_progress_indicator))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun test_IngredientFragment_ui_without_description() {
        val fragmentArgs = bundleOf("ingredientName" to "cinnamon")

        launchFragmentInContainer<IngredientFragment>(
            fragmentArgs = fragmentArgs,
            themeResId = R.style.Theme_Bartender
        )

        // Ingredient title isn't fetched, should be immediately visible
        onView(withId(R.id.ingredient_title))
            .check(matches(isDisplayed()))

        // Wait for network fetch
        robot.assertOnView(
            viewMatcher = withId(R.id.ingredient_description_text),
            initialViewAssertion = matches(withEffectiveVisibility(Visibility.VISIBLE)),
            matches(
                allOf(
                    isDisplayed(),
                    withText(containsString("No description provided.")),
                )
            )
        )
    }
}
