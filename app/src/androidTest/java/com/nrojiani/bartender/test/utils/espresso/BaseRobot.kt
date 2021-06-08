package com.nrojiani.bartender.test.utils.espresso

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import com.nrojiani.bartender.test.utils.espresso.EspressoExtensions.searchFor
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import java.lang.Thread.sleep

/**
 * [Source](https://stackoverflow.com/a/56499223/3717025)
 */
open class BaseRobot {
    fun doOnView(matcher: Matcher<View>, initialViewAssertion: ViewAssertion, vararg actions: ViewAction) {
        actions.forEach {
            waitForView(matcher, initialViewAssertion).perform(it)
        }
    }

    fun assertOnView(
        matcher: Matcher<View>,
        viewAssertion: ViewAssertion,
        vararg assertions: ViewAssertion
    ) {
        assertions.forEach {
            waitForView(matcher, viewAssertion).check(it)
        }
    }

    /**
     * Perform action of implicitly waiting for a certain view.
     * This differs from [EspressoExtensions.searchFor] in that,
     * upon failure to locate an element, it will fetch a new root view
     * in which to traverse searching for our @param match
     *
     * @param viewMatcher ViewMatcher used to find our view
     */
    fun waitForView(
        viewMatcher: Matcher<View>,
        initialViewAssertion: ViewAssertion,
        waitMillis: Int = 5000,
        waitMillisPerTry: Long = 100
    ): ViewInteraction {

        // Derive the max tries
        val maxTries = waitMillis / waitMillisPerTry.toInt()

        var tries = 0

        for (i in 0..maxTries)
            try {
                // Track the amount of times we've tried
                tries++

                // Search the root for the view
                onView(isRoot()).perform(searchFor(viewMatcher))

                return onView(viewMatcher).check(initialViewAssertion)

                // If we're here, we found our view. Now return it
                // return onView(viewMatcher)
            } catch (e: Throwable) {

                if (tries == maxTries) {
                    throw e
                }
                sleep(waitMillisPerTry)
            }

        throw Exception("Error finding a view matching $viewMatcher")
    }

    private fun nthChildOf(
        parentMatcher: Matcher<View?>,
        childPosition: Int
    ): Matcher<View> = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("with $childPosition child view of type parentMatcher")
        }

        override fun matchesSafely(item: View): Boolean {
            if (item.parent !is ViewGroup) {
                return parentMatcher.matches(item.parent)
            }
            val group = item.parent as ViewGroup
            var view: View? = null
            if (parentMatcher.matches(item.parent)) {
                view = group.getChildAt(childPosition) as? ViewGroup
            }

            return view != null && view == item
        }
    }
}
