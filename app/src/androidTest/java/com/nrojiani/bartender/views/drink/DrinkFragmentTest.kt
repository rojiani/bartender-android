package com.nrojiani.bartender.views.drink

import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nrojiani.bartender.R
import com.nrojiani.bartender.data.domain.DrinkRef
import com.nrojiani.bartender.data.domain.IngredientMeasure
import com.nrojiani.bartender.test.utils.espresso.BaseRobot
import com.nrojiani.bartender.test.utils.espresso.hasItemCount
import com.nrojiani.bartender.test.utils.espresso.withRecyclerView
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import it.czerwinski.android.hilt.fragment.testing.launchFragmentInContainer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DrinkFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var robot = BaseRobot()

    @Test
    fun test_DrinkFragment_ui() {
        val blueberryMojitoDrinkRef = DrinkRef(
            id = "178336",
            imageUrl = "https://www.thecocktaildb.com/images/media/drink/07iep51598719977.jpg",
            drinkName = "Blueberry Mojito"
        )
        val fragmentArgs = bundleOf(
            "drinkRef" to blueberryMojitoDrinkRef
        )

        launchFragmentInContainer<DrinkFragment>(
            fragmentArgs = fragmentArgs,
            themeResId = R.style.Theme_Bartender
        )

        // Wait for network fetch
        robot.waitForView(
            withId(R.id.instructions_text),
            matches(withEffectiveVisibility(Visibility.VISIBLE))
        )

        recyclerViewHasIngredientsData(
            listOf(
                IngredientMeasure("Dark Rum", "2 shots"),
                IngredientMeasure("Lime Juice", "1 shot"),
                IngredientMeasure("Sugar", "Dash"),
                IngredientMeasure("Blueberries", "Whole"),
                IngredientMeasure("Lemon-lime soda", "Top"),
            )
        )
    }

    private fun recyclerViewHasIngredientsData(ingredientMeasures: List<IngredientMeasure>) {
        robot.waitForView(
            withId(R.id.ingredients_list),
            matches(withEffectiveVisibility(Visibility.VISIBLE))
        ).check(hasItemCount(ingredientMeasures.size))

        ingredientMeasures.forEachIndexed { index, (ingredientName, measure) ->
            // Check ingredient name text
            onView(
                withRecyclerView(R.id.ingredients_list)
                    .atPositionOnView(index, R.id.ingredient_name_text)
            ).check(matches(withText(ingredientName)))

            // Check ingredient measure text
            onView(
                withRecyclerView(R.id.ingredients_list)
                    .atPositionOnView(index, R.id.ingredient_measure_text)
            ).check(matches(withText(measure)))
        }
    }
}
