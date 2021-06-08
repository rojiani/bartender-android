package com.nrojiani.bartender.test.utils.espresso

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.CoreMatchers

/**
 * [Source: taingmeng/espresso-recyclerview-assertions](https://github.com/taingmeng/espresso-recyclerview-assertions)
 */
fun hasItemCount(count: Int): ViewAssertion {
    return RecyclerViewItemCountAssertion(count)
}

private class RecyclerViewItemCountAssertion(private val count: Int) : ViewAssertion {
    override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) {
            throw noViewFoundException
        }

        if (view !is RecyclerView) {
            throw IllegalStateException("The asserted view is not RecyclerView")
        }

        view.adapter?.let {
            ViewMatchers.assertThat(
                "RecyclerView item count",
                it.itemCount,
                CoreMatchers.equalTo(count)
            )
        } ?: throw IllegalStateException("No adapter is assigned to RecyclerView")
    }
}
