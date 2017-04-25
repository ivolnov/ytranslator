package com.ivolnov.ytranslator;

import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ivolnov.ytranslator.activities.MainActivity;
import com.ivolnov.ytranslator.adapters.DictionaryAdapter;
import com.ivolnov.ytranslator.db.EventLog;
import com.ivolnov.ytranslator.dictionary.Dictionary;
import com.ivolnov.ytranslator.fragments.TranslatorFragment;
import com.ivolnov.ytranslator.languages.LanguageSpinnerListener;
import com.ivolnov.ytranslator.languages.Languages;
import com.ivolnov.ytranslator.languages.SwapLanguageButtonListener;
import com.ivolnov.ytranslator.translator.Translator;
import com.ivolnov.ytranslator.translator.TranslatorQueryWatcher;
import com.ivolnov.ytranslator.util.ForceLocaleRule;

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
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.ivolnov.ytranslator.util.TestingUtils.clickBookmarkIconInBookmarksWith;
import static com.ivolnov.ytranslator.util.TestingUtils.clickBookmarkIconInHistoryWith;
import static com.ivolnov.ytranslator.util.TestingUtils.clickBookmarkIconOnTheFirstHistoryListItem;
import static com.ivolnov.ytranslator.util.TestingUtils.clickTranslatorTab;
import static com.ivolnov.ytranslator.util.TestingUtils.closeSpinner;
import static com.ivolnov.ytranslator.util.TestingUtils.fetchFirstBookmarksItemTag;
import static com.ivolnov.ytranslator.util.TestingUtils.fetchFirstHistoryItemTag;
import static com.ivolnov.ytranslator.util.TestingUtils.fillLists;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Integration UI tests.
 *
 * As opposed to functional UI tests which check end to end functionality, only test
 * how user ui events and views are integrated with application modules.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 03.04.17
 */

@RunWith(AndroidJUnit4.class)
@MediumTest
public class UIIntegrationTest {

    private static final int UI_DELAY = 500; // abstract units

    private static final String RUSSIAN = "Русский";
    private static final String ENGLISH = "Английский";;
    private static final String SHORT_QUERY = "tic tac toe";
    private static final String LONG_QUERY = "Make love not war.";

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
    public void whenTranslationQueryIsTyped_TranslatorIsCalled() throws Exception {
        final TranslatorFragment fragment = getTranslatorFragment();
        final TranslatorQueryWatcher watcher = (TranslatorQueryWatcher) fragment.getTextWatcher();
        final Translator spy = spy(watcher.getTranslator());

        watcher.withTranslator(spy);

        onView(withId(R.id.query))
                .perform(typeText(SHORT_QUERY));

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        verify(spy, times(1)).translate(eq(SHORT_QUERY), any(String.class), eq(fragment));
    }

    @Test
    public void whenLessThatFourWordQueryIsTyped_DictionaryIsCalled() throws Exception {
        final TranslatorFragment fragment = getTranslatorFragment();
        final DictionaryAdapter listener = fragment.getDictionaryAdapter();
        final TranslatorQueryWatcher watcher = (TranslatorQueryWatcher) fragment.getTextWatcher();
        final Dictionary spy = spy(watcher.getDictionary());

        watcher.withDictionary(spy);

        onView(withId(R.id.query))
                .perform(typeText(SHORT_QUERY));

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        verify(spy, times(1))
                .lookup(eq(SHORT_QUERY), any(String.class), any(String.class), eq(listener));
    }

    @Test
    public void whenMoreThatThreeWordQueryIsTyped_DictionaryIsNotCalled() throws Exception {
        final TranslatorFragment fragment = getTranslatorFragment();
        final TranslatorQueryWatcher watcher = (TranslatorQueryWatcher) fragment.getTextWatcher();
        final Dictionary spy = spy(watcher.getDictionary());

        watcher.withDictionary(spy);

        onView(withId(R.id.query))
                .perform(typeText(LONG_QUERY));

        verify(spy, never())
                .lookup(eq(LONG_QUERY),
                        any(String.class),
                        any(String.class),
                        any(DictionaryAdapter.class));
    }

    @Test
    public void whenSourceLanguageChanged_languagesIsCalled() throws Exception {
        final TranslatorFragment fragment = getTranslatorFragment();
        final LanguageSpinnerListener listener = fragment.getSourceLanguageSpinnerListener();
        final Languages spy = spy(listener.getLanguages());

        listener.withLanguages(spy);

        onView(withId(R.id.sourceSpinner))
                .perform(click());

        onData(allOf(is(instanceOf(String.class)), equalTo(RUSSIAN)))
                .perform(click());

        TimeUnit.SECONDS.sleep(1);

        verify(spy, times(1)).onSourceLanguageChanged(RUSSIAN);
    }

    @Test
    public void whenTargetLanguageChanged_languagesIsCalled() throws Exception {
        final TranslatorFragment fragment = getTranslatorFragment();
        final LanguageSpinnerListener listener = fragment.getTargetLanguageSpinnerListener();
        final Languages spy = spy(listener.getLanguages());

        listener.withLanguages(spy);

        onView(withId(R.id.targetSpinner))
                .perform(click());

        onData(allOf(is(instanceOf(String.class)), equalTo(ENGLISH)))
                .perform(click());

        verify(spy, times(1)).onTargetLanguageChanged(ENGLISH);
    }

    @Test
    public void whenSwapLanguageClicked_languagesIsCalled() throws Exception {
        final TranslatorFragment fragment = getTranslatorFragment();
        final SwapLanguageButtonListener listener = fragment.getSwapLanguageButtonListener();
        final Languages spy = spy(listener.getLanguages());

        listener.withLanguages(spy);

        onView(withId(R.id.swapLanguage))
                .perform(click());

        verify(spy, times(1)).onLanguagesSwapped();
    }

    @Test
    public void whenTranslationQueryIsTyped_eventLogIsCalled() throws Exception {
        final EventLog log = spy(rule.getActivity().getEventLog());
        rule.getActivity().withEventLog(log);

        onView(withId(R.id.query))
                .perform(typeText(SHORT_QUERY));

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        verify(log, times(1))
                .logTranslation(eq(SHORT_QUERY), any(String.class), any(String.class));
    }

    @Test
    public void whenBookmarkIconIsClickedInHistory_eventLogIsCalled() throws Exception {

        fillLists();

        onView(withId(R.id.container)).perform(swipeLeft());

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        final int id = fetchFirstHistoryItemTag();
        final EventLog log = spy(rule.getActivity().getEventLog());

        rule.getActivity().getHistoryAdapter().withLog(log);

        clickBookmarkIconInHistoryWith(id);

        verify(log, times(1)).logBookmarked(id);
    }

    @Test
    public void whenActiveBookmarkIconIsClickedInHistory_eventLogIsCalled() throws Exception {

        fillLists();

        onView(withId(R.id.container)).perform(swipeLeft());
        TimeUnit.MILLISECONDS.sleep(UI_DELAY);
        clickBookmarkIconOnTheFirstHistoryListItem();

        final int id = fetchFirstHistoryItemTag();
        final EventLog log = spy(rule.getActivity().getEventLog());

        rule.getActivity().getHistoryAdapter().withLog(log);

        clickBookmarkIconInHistoryWith(id);

        verify(log, times(1)).logUnBookmarked(id);
    }

    @Test
    public void whenBookmarkIconIsClickedInBookmarks_eventLogIsCalled() throws Exception {

        fillLists();

        onView(withId(R.id.container))
                .perform(swipeLeft())
                .perform(swipeLeft());

        TimeUnit.MILLISECONDS.sleep(UI_DELAY);

        final int id = fetchFirstBookmarksItemTag();
        final EventLog log = spy(rule.getActivity().getEventLog());

        rule.getActivity().getBookmarksAdapter().withLog(log);

        clickBookmarkIconInBookmarksWith(id);

        verify(log, times(1)).logUnBookmarked(id);
    }

    private TranslatorFragment getTranslatorFragment() {
        return (TranslatorFragment) rule
                .getActivity()
                .getSectionsPagerAdapter()
                .getFragmentOnPosition(0);
    }

    /**
     * Picks english on the source spinner and russian on the target.
     * Clears translation query
     */
    private void setInitialTranslationDirectionAndClearQuery() throws Exception {
        onView(withId(R.id.sourceSpinner))
                .perform(click());
        onData(allOf(is(instanceOf(String.class)), equalTo(ENGLISH)))
                .perform(click());

        onView(withId(R.id.targetSpinner))
                .perform(click());
        onData(allOf(is(instanceOf(String.class)), equalTo(RUSSIAN)))
                .perform(click());

        onView(withId(R.id.query))
                .perform(clearText());
    }
}