package com.ivolnov.ytranslator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentHostCallback;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ivolnov.ytranslator.activities.MainActivity;
import com.ivolnov.ytranslator.db.EventLog;
import com.ivolnov.ytranslator.dictionary.Dictionary;
import com.ivolnov.ytranslator.fragments.TranslatorFragment;
import com.ivolnov.ytranslator.languages.LanguageSpinner;
import com.ivolnov.ytranslator.languages.LanguagesUIState;
import com.ivolnov.ytranslator.languages.Languages;
import com.ivolnov.ytranslator.languages.VolleyLanguages;
import com.ivolnov.ytranslator.translator.Translator;
import com.ivolnov.ytranslator.util.LanguageSpinnerTestData;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * {@link TranslatorFragment}'s instrumented unit tests.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 03.04.17
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TranslatorFragmentTest {

    private final int UI_DELAY = 500; //milliseconds

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void whenFragmentIsStopped_pendingTranslationsAreCancelled() {
        final TranslatorFragment fragment = getFragment();
        final Translator spy = spy(fragment.getTranslator());

        fragment.withTranslator(spy);

        fragment.onStop();

        verify(spy, times(1)).stopPending();
    }

    @Test
    public void whenFragmentIsStopped_pendingLookupsAreCancelled() {
        final TranslatorFragment fragment = getFragment();
        final Dictionary spy = spy(fragment.getDictionary());

        fragment.withDictionary(spy);

        fragment.onStop();

        verify(spy, times(1)).stopPending();
    }

    @Test
    public void whenFragmentIsStopped_queryTextWatcherIsRemoved() {
        final TranslatorFragment fragment = getFragment();
        final EditText spy = spy(fragment.getQueryField());

        fragment.withQueryField(spy);

        fragment.onStop();

        verify(spy, times(1)).removeTextChangedListener(fragment.getTextWatcher());
    }

    @Test
    public void whenFragmentIsStopped_sourceLanguageSpinnerListenerIsRemoved() {
        final TranslatorFragment fragment = getFragment();
        final LanguageSpinner spy = spy(fragment.getSourceSpinner());

        fragment.withSourceSpinner(spy);

        fragment.onStop();

        verify(spy, times(1)).setOnItemSelectedListener(null);
    }

    @Test
    public void whenFragmentIsStopped_targetLanguageSpinnerListenerIsRemoved() {
        final TranslatorFragment fragment = getFragment();
        final LanguageSpinner spy = spy(fragment.getTargetSpinner());

        fragment.withTargetSpinner(spy);

        fragment.onStop();

        verify(spy, times(1)).setOnItemSelectedListener(null);
    }

    @Test
    public void whenFragmentIsStopped_swapLanguagesListenerIsRemoved() {
        final TranslatorFragment fragment = getFragment();
        final Button spy = spy(fragment.getSwapButton());

        fragment.withSwapButton(spy);

        fragment.onStop();

        verify(spy, times(1)).setOnClickListener(null);
    }

    @Test
    public void whenTranslationListenerIsNotified_translationTextViewIsUpdated() {
        final String result = "translation result";
        final TranslatorFragment fragment = getFragment();
        final TextView translation = spy(fragment.getTranslation());

        fragment.withTranslation(translation);


        rule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.notifyTranslated("", null, result);
            }
        });

        SystemClock.sleep(UI_DELAY);

        verify(translation, times(1)).setText(result);
    }

    @Test
    public void whenTranslationListenerIsNotified_eventLogIsCalled() throws Exception {
        final String query = "translation query";
        final String result = "translation result";
        final String direction = "translation direction";
        final TranslatorFragment fragment = getFragment();
        final MainActivity activity = rule.getActivity();
        final EventLog log = spy(activity.getEventLog());

        activity.withEventLog(log);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.notifyTranslated(query, direction, result);
            }
        });

        SystemClock.sleep(UI_DELAY);

        verify(log, times(1)).logTranslation(query, result, direction);
    }

    @Test
    public void onViewCreated_attachStateToLanguages() throws Exception {
        final TranslatorFragment fragment = getFragment();
        final Languages spy = spy(fragment.getLanguages());

        fragment.withLanguages(spy);

        rule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.onViewCreated(mock(View.class), new Bundle());
            }
        });

        SystemClock.sleep(UI_DELAY * 2);

        verify(spy, times(1)).attachState(any(LanguagesUIState.class));
    }

    @Test
    public void onViewCreated_callLoadAvailableLanguagesList() throws Exception {
        final TranslatorFragment fragment = getFragment();
        final Languages spy = spy(fragment.getLanguages());

        fragment.withLanguages(spy);

        rule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.onViewCreated(mock(View.class), new Bundle());
            }
        });

        SystemClock.sleep(UI_DELAY);

        verify(spy, times(1)).loadAvailableLanguages((VolleyLanguages) spy);
    }

    @Test
    public void onStop_saveTranslationDirection() throws Exception {
        final TranslatorFragment fragment = getFragment();
        final String source = fragment.getSourceSpinner().getSelectedItem();
        final String target = fragment.getTargetSpinner().getSelectedItem();

        fragment.onStop();

        final SharedPreferences prefs = rule.getActivity().getPreferences(Context.MODE_PRIVATE);
        final String savedSource = prefs.getString(Languages.SOURCE_LANGUAGE_PREFERENCE_KEY, null);
        final String savedTarget = prefs.getString(Languages.TARGET_LANGUAGE_PREFERENCE_KEY, null);

        Assert.assertThat(savedSource, is(equalTo(source)));
        Assert.assertThat(savedTarget, is(equalTo(target)));
    }

    @Test
    public void onViewCreates_restoreTranslationDirection() throws Exception {
        final TranslatorFragment fragment = getFragment();
        final String source = LanguageSpinnerTestData.AVAILABLE_LANGUAGES[2];
        final String target = LanguageSpinnerTestData.AVAILABLE_LANGUAGES[3];
        final VolleyLanguages languages = (VolleyLanguages) fragment.getLanguages();

        rule.getActivity()
                .getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putString(Languages.SOURCE_LANGUAGE_PREFERENCE_KEY, source)
                .putString(Languages.TARGET_LANGUAGE_PREFERENCE_KEY, target)
                .apply();

        rule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.getSourceSpinner().getAdapter().clear();
                fragment.getTargetSpinner().getAdapter().clear();
                fragment.onViewCreated(mock(View.class), new Bundle());
            }
        });

        SystemClock.sleep(UI_DELAY * 2);

        final String newSource = languages.getState().getSourceLanguage();
        final String newTarget = languages.getState().getTargetLanguage();

        Assert.assertThat(newSource, is(equalTo(source)));
        Assert.assertThat(newTarget, is(equalTo(target)));
    }

    private TranslatorFragment getFragment() {
        return (TranslatorFragment) rule
                .getActivity()
                .getSectionsPagerAdapter()
                .getFragmentOnPosition(0);
    }

    /**
     * Reflection based way to inject an activity into fragment.
     * Used to swap an activity in the fragment with its spy.
     */
    private TranslatorFragment injectActivity(TranslatorFragment fragment, MainActivity activity)
            throws Exception{

        final Field hostField = TranslatorFragment.class.getSuperclass().getDeclaredField("mHost");
        final boolean hostAccessible = hostField.isAccessible();

        hostField.setAccessible(true);

        final FragmentHostCallback host = (FragmentHostCallback) hostField.get(fragment);
        final Field activityField = FragmentHostCallback.class.getDeclaredField("mActivity");
        final boolean activityAccessible = activityField.isAccessible();

        activityField.setAccessible(true);

        activityField.set(host, activity);

        activityField.setAccessible(activityAccessible);
        hostField.setAccessible(hostAccessible);

        return fragment;
    }
}
