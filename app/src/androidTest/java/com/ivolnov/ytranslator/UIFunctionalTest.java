package com.ivolnov.ytranslator;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ivolnov.ytranslator.activities.MainActivity;
import com.ivolnov.ytranslator.util.DictionaryRecyclerViewTestData;
import com.ivolnov.ytranslator.util.ForceLocaleRule;
import com.ivolnov.ytranslator.util.LanguageSpinnerTestData;
import com.ivolnov.ytranslator.util.TestingUtils;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.ivolnov.ytranslator.util.TestingUtils.clickBookmarkIconInBookmarksWith;
import static com.ivolnov.ytranslator.util.TestingUtils.clickBookmarkIconOnThFirstListItem;
import static com.ivolnov.ytranslator.util.TestingUtils.clickFirstBookmarkIconWith;
import static com.ivolnov.ytranslator.util.TestingUtils.clickTranslatorTab;
import static com.ivolnov.ytranslator.util.TestingUtils.closeSpinner;
import static com.ivolnov.ytranslator.util.TestingUtils.hasItemCount;
import static com.ivolnov.ytranslator.util.TestingUtils.withRecyclerView;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

/**
 * Functional/acceptance tests for UI.
 * Test user stories from end to end.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 29.03.17
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UIFunctionalTest {

    private static final int UI_DELAY = 500; // abstract units
    private static final String DIRECTION = "EN-RU";
    private static final String UNTRANSLATABLE = "pwnz";
    private static final String LONG_QUERY
            = "Early to bed and early to rise makes a man healthy, wealthy and wise.";

    static class TINY_TRANSLATION {
        static final String ENGLISH = "test";
        static final String RUSSIAN = "тест";
    }

    static class TRANSLATION {
        static final String ENGLISH = "functional test";
        static final String RUSSIAN = "функциональное испытание";
        static final String FRENCH = "test fonctionnel";
    }

    static class LANGUAGE {
        static final String ENGLISH = "Английский";
        static final String RUSSIAN = "Русский";
        static final String FRENCH = "Французский";
    }

    @ClassRule
    public static final ForceLocaleRule localeTestRule = new ForceLocaleRule(new Locale("ru"));

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        clickTranslatorTab();
        closeSpinner();
        setInitialTranslationDirectionAndClearQuery();
    }

    @Test
    public void whenUserTypesQuery_translationAppearsOnScreen() throws Exception {

        onView(withId(R.id.query))
                .perform(typeText(TRANSLATION.ENGLISH));

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        onView(withId(R.id.translation))
                .check(matches(withText(TRANSLATION.RUSSIAN)));
    }

    @Test
    public void whenUserTypesQuery_dictionaryAppearsOnScreen() throws Exception {
        onView(withId(R.id.query))
                .perform(typeText(DictionaryRecyclerViewTestData.QUERY))
                .perform(closeSoftKeyboard());

        TimeUnit.MILLISECONDS.sleep(UI_DELAY * 2);

        final int expectedSize = DictionaryRecyclerViewTestData.DICTIONARY_ITEM_LIST.size();

        onView(withId(R.id.dictionary))
                .check(hasItemCount(expectedSize));

        for (int i = 0; i < expectedSize; i++) {
            String item = DictionaryRecyclerViewTestData.DICTIONARY_ITEM_LIST
                    .get(i);

            onView(withId(R.id.dictionary)).perform(scrollToPosition(i));

            onView(withRecyclerView(R.id.dictionary).atPosition(i))
                    .check(matches(hasDescendant(withText(item))));
        }
    }

    @Test
    public void whenUserClicksLanguageSpinner_availableLanguagesAreInTheList() throws Exception {

        onView(withId(R.id.sourceSpinner))
                .perform(click());

        for (int i = 0; i < LanguageSpinnerTestData.AVAILABLE_LANGUAGES.length; i++) {
            final String language = LanguageSpinnerTestData.AVAILABLE_LANGUAGES[i];
            onData(is(instanceOf(String.class)))
                    .atPosition(i)
                    .check(matches(withText(equalTo(language))));
        }

        onData(is(instanceOf(String.class)))
                .atPosition(0)
                .perform(click());

        onView(withId(R.id.targetSpinner))
                .perform(click());

        for (int i = 0; i < LanguageSpinnerTestData.AVAILABLE_LANGUAGES.length; i++) {
            final String language = LanguageSpinnerTestData.AVAILABLE_LANGUAGES[i];
            onData(is(instanceOf(String.class)))
                    .atPosition(i)
                    .check(matches(withText(equalTo(language))));
        }

        onData(is(instanceOf(String.class)))
                .atPosition(0)
                .perform(click());
    }

    @Test
    public void whenUserChangesTargetLanguage_translationOnScreenUpdates() throws Exception {
        setInitialTranslationQuery();

        onView(withId(R.id.targetSpinner))
                .perform(click());
        onData(allOf(is(instanceOf(String.class)), equalTo(LANGUAGE.FRENCH)))
                .perform(click());

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        onView(withId(R.id.translation))
                .check(matches(withText(TRANSLATION.FRENCH)));
    }

    @Test
    public void whenUserChangesSourceLanguage_translationOnScreenUpdates() throws Exception {
        setInitialTranslationQuery();

        onView(withId(R.id.sourceSpinner))
                .perform(click());
        onData(allOf(is(instanceOf(String.class)), equalTo(LANGUAGE.FRENCH)))
                .perform(click());

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        onView(withId(R.id.translation))
                .check(matches(withText(TRANSLATION.ENGLISH)));
    }

    @Test
    public void whenUserPicksSameLanguageAsOnTheOtherSide_languagesAreSwapped() throws Exception {
        final Spinner source = (Spinner) rule.getActivity().findViewById(R.id.sourceSpinner);
        final Spinner target = (Spinner) rule.getActivity().findViewById(R.id.targetSpinner);
        final String oldSource = (String) source.getSelectedItem();
        final String oldTarget = (String) target.getSelectedItem();

        onView(withId(R.id.sourceSpinner))
                .perform(click());
        onData(allOf(is(instanceOf(String.class)), equalTo(oldTarget)))
                .perform(click());

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);
        
        onView(withId(R.id.targetSpinner))
                .check(matches(withSpinnerText(equalTo(oldSource))));

        onView(withId(R.id.targetSpinner))
                .perform(click());
        onData(allOf(is(instanceOf(String.class)), equalTo(oldTarget)))
                .perform(click());

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        onView(withId(R.id.sourceSpinner))
                .check(matches(withSpinnerText(equalTo(oldSource))));
    }

    @Test
    public void whenUserClicksSwapButton_languagesAreSwapped() throws Exception {
        setInitialTranslationQuery();

        final Spinner source = (Spinner) rule.getActivity().findViewById(R.id.sourceSpinner);
        final Spinner target = (Spinner) rule.getActivity().findViewById(R.id.targetSpinner);
        final String oldSource = (String) source.getSelectedItem();
        final String oldTarget = (String) target.getSelectedItem();
        
        onView(withId(R.id.swapLanguage))
                .perform(click());

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        onView(withId(R.id.sourceSpinner))
                .check(matches(withSpinnerText(equalTo(oldTarget))));
        onView(withId(R.id.targetSpinner))
                .check(matches(withSpinnerText(equalTo(oldSource))));
    }

    @Test
    public void whenUserClicksSwapButton_queryAndTranslationAreSwapped() throws Exception {
        onView(withId(R.id.query))
                .perform(typeText(TRANSLATION.ENGLISH));

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        final EditText query = (EditText) rule.getActivity().findViewById(R.id.query);
        final TextView translation = (TextView) rule.getActivity().findViewById(R.id.translation);
        final String oldQuery = query.getText().toString();
        final String oldTranslation = translation.getText().toString();

        onView(withId(R.id.swapLanguage))
                .perform(click());

        TimeUnit.MILLISECONDS.sleep(UI_DELAY * 2);

        onView(withId(R.id.query)).check(matches(withText(equalTo(oldTranslation))));
        onView(withId(R.id.translation)).check(matches(withText(equalTo(oldQuery))));
    }

    @Test
    public void whenUserTypesQuery_queryAndTranslationAppearOnTopOfHistoryList() throws Exception {
        onView(withId(R.id.query))
                .perform(typeText(TINY_TRANSLATION.ENGLISH))
                .perform(swipeLeft());

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        onView(withRecyclerView(R.id.history).atPosition(0))
                .check(matches(allOf(
                        hasDescendant(allOf(
                                withId(R.id.historyQuery),
                                withText(TINY_TRANSLATION.ENGLISH))),
                        hasDescendant(allOf(
                                withId(R.id.historyTranslation),
                                withText(TINY_TRANSLATION.RUSSIAN))),
                        hasDescendant(allOf(
                                withId(R.id.historyDirection),
                                withText(DIRECTION)))
                )));
    }

    @Test
    public void whenUserTypesLongQuery_historyListStaysTheSame() throws Exception {
        onView(withId(R.id.query))
                .perform(typeText(LONG_QUERY))
                .perform(swipeLeft());

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        onView(withRecyclerView(R.id.history).atPosition(0))
                .check(matches(not(
                        hasDescendant(allOf(
                                withId(R.id.historyQuery),
                                withText(LONG_QUERY)))
                        )
                ));
    }

    @Test
    public void whenUserTypesUntranslatableQuery_historyListStaysTheSame() throws Exception {
        onView(withId(R.id.query))
                .perform(typeText(UNTRANSLATABLE))
                .perform(swipeLeft());

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        onView(withRecyclerView(R.id.history).atPosition(0))
                .check(matches(not(
                        hasDescendant(allOf(
                                withId(R.id.historyQuery),
                                withText(UNTRANSLATABLE)))
                        )
                ));
    }


    @Test
    public void whenUserClicksBookmarkIconInHistory_thisItemAppearsOnTopOfBookmarksList()
            throws Exception {

        final TestingUtils.HistoryItemStringFetcher fetchText
                = TestingUtils.fetchHistoryItemTextStrings();

        setInitialTranslationQuery();

        onView(withId(R.id.container)).perform(swipeLeft());

        onView(withRecyclerView(R.id.history)
                .atPosition(0))
                .perform(fetchText);

        final String query = fetchText.getQuery();
        final String direction = fetchText.getDirection();
        final String translation = fetchText.getTranslation();

        clickFirstBookmarkIconWith(query, translation, direction);

        onView(withId(R.id.container)).perform(swipeLeft());

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        onView(withRecyclerView(R.id.bookmarks).atPosition(0))
                .check(matches(allOf(
                        hasDescendant(allOf(
                                withId(R.id.bookmarkQuery),
                                withText(query))),
                        hasDescendant(allOf(
                                withId(R.id.bookmarkTranslation),
                                withText(translation))),
                        hasDescendant(allOf(
                                withId(R.id.bookmarkDirection),
                                withText(direction)))
                )));
    }

    @Test
    public void whenUserClicksBookmarkIconInBookmarks_theItemDisappears() throws Exception {

        final TestingUtils.FetchBookmarkIconTag fetchTag = TestingUtils.fetchBookmarkIconTag();

        setInitialTranslationQuery();

        onView(withId(R.id.container)).perform(swipeLeft());

        clickBookmarkIconOnThFirstListItem();

        onView(withId(R.id.container)).perform(swipeLeft());

        onView(withRecyclerView(R.id.bookmarks)
                .atPosition(0))
                .perform(fetchTag);

        final int tag = fetchTag.getTag();

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        clickBookmarkIconInBookmarksWith(tag);

        onView(allOf(
                withId(R.id.bookmark_icon),
                withParent(withParent(withId(R.id.bookmarks))),
                withTagValue(is(equalTo((Object) tag)))
        )).check(doesNotExist());
    }

    @Test
    public void whenUserClicksActiveBookmarkIconInHistory_theItemDisappearsInBookmarks()
            throws Exception {

        final TestingUtils.FetchBookmarkIconTag fetchTag = TestingUtils.fetchBookmarkIconTag();

        setInitialTranslationQuery();
        TimeUnit.MILLISECONDS.sleep(UI_DELAY);
        clearQuery();
        setAlternativeTranslationQuery();

        onView(withId(R.id.container)).perform(swipeLeft());

        clickBookmarkIconOnThFirstListItem();

        onView(withId(R.id.container)).perform(swipeLeft());

        onView(withRecyclerView(R.id.bookmarks)
                .atPosition(0))
                .perform(fetchTag);

        final int tag = fetchTag.getTag();

        onView(withId(R.id.container)).perform(swipeRight());

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        clickBookmarkIconOnThFirstListItem();

        onView(withId(R.id.container))
                .perform(swipeLeft());

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        onView(allOf(
                withId(R.id.bookmark_icon),
                withParent(withParent(withId(R.id.bookmarks))),
                withTagValue(is(equalTo((Object) tag)))
        )).check(doesNotExist());
    }

    /**
     * Picks english on the source spinner and russian on the target.
     * Clears translation query
     */
    private void setInitialTranslationDirectionAndClearQuery() throws Exception {
        onView(withId(R.id.sourceSpinner))
                    .perform(click());
        onData(allOf(is(instanceOf(String.class)), equalTo(LANGUAGE.ENGLISH)))
                .perform(click());

        onView(withId(R.id.targetSpinner))
                .perform(click());
        onData(allOf(is(instanceOf(String.class)), equalTo(LANGUAGE.RUSSIAN)))
                .perform(click());

        clearQuery();
    }

    private void setInitialTranslationQuery() {
        onView(withId(R.id.query))
                .perform(typeText(TRANSLATION.ENGLISH));
    }

    private void setAlternativeTranslationQuery() {
        onView(withId(R.id.query))
                .perform(typeText(TINY_TRANSLATION.ENGLISH));
    }

    private void clearQuery() {
        onView(withId(R.id.query))
                .perform(clearText());
    }
}