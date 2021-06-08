package com.nrojiani.bartender.test.utils.espresso

import android.view.View
import androidx.test.espresso.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import com.nrojiani.bartender.test.utils.espresso.EspressoExtensions.searchFor
import org.hamcrest.Matcher
import java.lang.Thread.sleep

/**
 * [Source](https://stackoverflow.com/a/56499223/3717025)
 */
open class BaseRobot {
    fun doOnView(
        viewMatcher: Matcher<View>,
        initialViewAssertion: ViewAssertion,
        vararg actions: ViewAction
    ) {
        actions.forEach {
            waitForView(viewMatcher, initialViewAssertion).perform(it)
        }
    }

    fun assertOnView(
        viewMatcher: Matcher<View>,
        initialViewAssertion: ViewAssertion,
        vararg assertions: ViewAssertion
    ) {
        assertions.forEach {
            waitForView(viewMatcher, initialViewAssertion).check(it)
        }
    }

    /**
     * Perform action of implicitly waiting for a certain view.
     * This differs from [EspressoExtensions.searchFor] in that,
     * upon failure to locate an element, it will fetch a new root view
     * in which to traverse searching for our @param match
     *
     * @param viewMatcher ViewMatcher used to find our view.
     * @param initialViewAssertion An initial [ViewAssertion] that we will wait for to be true.
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
}
