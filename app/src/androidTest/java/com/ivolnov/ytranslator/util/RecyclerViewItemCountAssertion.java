package com.ivolnov.ytranslator.util;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Espresso assertion to check how many items are there in the recycler view.
 * @see <a href="http://stackoverflow.com/a/37339656/4003403">stackoverflow</a>
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 06.04.17
 */

public class RecyclerViewItemCountAssertion implements ViewAssertion {
    private final int expectedCount;

    public RecyclerViewItemCountAssertion(int expectedCount) {
        this.expectedCount = expectedCount;
    }

    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
            throw noViewFoundException;
        }

        RecyclerView recyclerView = (RecyclerView) view;
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        assertThat(
                "Recycler view's item count differs from expected.",
                adapter.getItemCount(),
                is(expectedCount));
    }
}
