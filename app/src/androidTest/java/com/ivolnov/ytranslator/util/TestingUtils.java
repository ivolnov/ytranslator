package com.ivolnov.ytranslator.util;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ivolnov.ytranslator.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

/**
 * TestingUtils, assertion, actions and other useful utils for testing.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 21.04.17
 */

public class TestingUtils {

    /**
     * Make sure no spinners are left open.
     */
    public static void closeSpinner() {
        try {
            onData(is(instanceOf(String.class)))
                    .atPosition(0)
                    .perform(click());
        } catch (Exception e) {
            //was closed
        }
    }

    public static void clickTranslatorTab() {
        onView(first(withParent(withParent(withId(R.id.tabs)))))
                .perform(click());
    }

    /**
     * Finds first history or bookmarks item with given content and clicks its bookmark icon.
     *
     * @param query a displayed translation query string
     * @param translation a displayed translation result string
     * @param direction a displayed translation direction string
     */
    public static void clickFirstBookmarkIconWith(String query, String translation, String direction) {
        onView(withId(R.id.container))
                .perform(closeSoftKeyboard());

        onView(first(allOf(
                withId(R.id.bookmark_icon),
                withParent(allOf(
                        withChild(allOf(
                                withChild(withText(query)),
                                withChild(withText(translation))
                        )),
                        withChild(withText(direction))
                )))))
                .perform(click());
    }

    /**
     * Clicks bookmark icon in history list that has a given tag.
     *
     * @param tag a string with translation record integer id.
     */
    public static  void clickBookmarkIconInHistoryWith(int tag) {
        onView(withId(R.id.container))
                .perform(closeSoftKeyboard());

        onView(allOf(
                withId(R.id.bookmark_icon),
                withParent(withParent(withId(R.id.history))),
                withTagValue(is(equalTo((Object) tag)))
        )).perform(click());
    }

    /**
     * Clicks bookmark icon in bookmarks list that has a given tag.
     *
     * @param tag a string with translation record integer id.
     */
    public static void clickBookmarkIconInBookmarksWith(int tag) {
        onView(withId(R.id.container))
                .perform(closeSoftKeyboard());

        onView(allOf(
                withId(R.id.bookmark_icon),
                withParent(withParent(withId(R.id.bookmarks))),
                withTagValue(is(equalTo((Object) tag)))
        )).perform(click());
    }

    /**
     * Clicks bookmark icon of the first item.
     */
    public static  void clickBookmarkIconOnThFirstListItem() {
        final TestingUtils.FetchBookmarkIconTag fetchTag = TestingUtils.fetchBookmarkIconTag();

        onView(withId(R.id.container))
                .perform(closeSoftKeyboard());

        onView(withRecyclerView(R.id.history)
                .atPosition(0))
                .perform(fetchTag);

        final Integer id = fetchTag.getTag();

        clickBookmarkIconInHistoryWith(id);
    }

    /**
     * @see <a href="http://stackoverflow.com/a/39756832/4003403">stackoverflow</>
     */
    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }

    public static HistoryItemStringFetcher fetchHistoryItemTextStrings() {
        return new HistoryItemStringFetcher();
    }

    public static FetchBookmarkIconTag fetchBookmarkIconTag() {
        return new FetchBookmarkIconTag();
    }

    public static RecyclerViewItemCountAssertion hasItemCount(int count) {
        return new RecyclerViewItemCountAssertion(count);
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    public static void tapRecyclerViewItem(int recyclerViewId, int position) {
        onView(withId(recyclerViewId)).perform(scrollToPosition(position));
        onView(withRecyclerView(recyclerViewId).atPosition(position)).perform(click());
    }

    public static Matcher<View> first(final Matcher<View> matcher) {
        return withIndex(matcher, 0);
    }

    /**
     * Fetches strings from history list item text views which can later be accessed via getters.
     */
    public static class HistoryItemStringFetcher implements ViewAction {
        private String query;
        private String translation;
        private String direction;

        @Override
        public Matcher<View> getConstraints() {
            return isAssignableFrom(LinearLayout.class);
        }

        @Override
        public String getDescription() {
            return "getting text from a TextView";
        }

        @Override
        public void perform(UiController uiController, View view) {
            TextView query = (TextView) view.findViewById(R.id.historyQuery);
            TextView direction = (TextView) view.findViewById(R.id.historyDirection);
            TextView translation = (TextView) view.findViewById(R.id.historyTranslation);

            this.query = query.getText().toString();
            this.direction = direction.getText().toString();
            this.translation = translation.getText().toString();
        }

        public String getQuery() {
            return query;
        }

        public String getTranslation() {
            return translation;
        }

        public String getDirection() {
            return direction;
        }
    }

    /**
     * Fetches tag from view.
     */
    public static class FetchBookmarkIconTag implements ViewAction {

        private int tag;

        @Override
        public Matcher<View> getConstraints() {
            return isAssignableFrom(LinearLayout.class);
        }

        @Override
        public String getDescription() {
            return "getting text from a TextView";
        }

        @Override
        public void perform(UiController uiController, View view) {
            final ImageView icon = (ImageView) view.findViewById(R.id.bookmark_icon);
            this.tag = (Integer) icon.getTag();
        }

        public int getTag() {
            return tag;
        }
    }
}
