package com.ivolnov.ytranslator;

import android.text.Editable;
import android.widget.EditText;
import android.widget.TextView;

import com.ivolnov.ytranslator.adapters.LanguageSpinnerAdapter;
import com.ivolnov.ytranslator.languages.LanguageSpinner;
import com.ivolnov.ytranslator.languages.LanguagesUIState;
import com.ivolnov.ytranslator.util.AvailableLanguagesApiTestData;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link LanguagesUIState} local unit tests.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 12.04.17
 */

public class LanguagesUIStateTest {

    private static final int ENGLISH_INDEX = 0;
    private static final int RUSSIAN_INDEX = 1;
    private static final String ENGLISH = "Английский";
    private static final String RUSSIAN = "Русский";
    private static final String QUERY = "time";
    private static final String TRANSLATION = "время";

    @Test
    public void notifyLanguagesChangedTest() throws Exception {
        final LanguageSpinner source = mock(LanguageSpinner.class);
        final LanguageSpinner target = mock(LanguageSpinner.class);
        final LanguageSpinnerAdapter sourceAdapter = mock(LanguageSpinnerAdapter.class);
        final LanguageSpinnerAdapter targetAdapter = mock(LanguageSpinnerAdapter.class);
        when(source.getAdapter()).thenReturn(sourceAdapter);
        when(target.getAdapter()).thenReturn(targetAdapter);

        final LanguagesUIState state = new LanguagesUIState(
                source,
                target,
                mock(EditText.class),
                mock(TextView.class)
        );

        final String[] languages = {ENGLISH, RUSSIAN};

        state.notifyLanguagesChanged(languages);

        verify(sourceAdapter, times(1)).addAll(languages);
        verify(targetAdapter, times(1)).addAll(languages);
    }

    @Test
    public void getSourceLanguageTest() throws Exception {
        final LanguageSpinner sourceSpinner = mock(LanguageSpinner.class);
        when(sourceSpinner.getSelectedItem()).thenReturn(ENGLISH);
        final LanguagesUIState state = new LanguagesUIState(
                sourceSpinner,
                mock(LanguageSpinner.class),
                mock(EditText.class),
                mock(TextView.class)
        );

        final String result = state.getSourceLanguage();

        verify(sourceSpinner, times(1)).getSelectedItem();
        Assert.assertThat(result, is(equalTo(ENGLISH)));
    }

    @Test
    public void getTargetLanguageTest() throws Exception {
        final LanguageSpinner targetSpinner = mock(LanguageSpinner.class);
        when(targetSpinner.getSelectedItem()).thenReturn(ENGLISH);
        final LanguagesUIState state = new LanguagesUIState(
                mock(LanguageSpinner.class),
                targetSpinner,
                mock(EditText.class),
                mock(TextView.class)
        );

        final String result = state.getTargetLanguage();

        verify(targetSpinner, times(1)).getSelectedItem();
        Assert.assertThat(result, is(equalTo(ENGLISH)));
    }

    @Test
    public void notifyDirectionChangedTest() {
        final EditText query = mock(EditText.class);
        final Editable translation = mock(Editable.class);
        final LanguageSpinner source = mock(LanguageSpinner.class);
        final LanguageSpinner target = mock(LanguageSpinner.class);
        when(translation.toString()).thenReturn(QUERY);
        when(query.getText()).thenReturn(translation);
        when(source.getSelectedItem()).thenReturn(ENGLISH);
        when(target.getSelectedItem()).thenReturn(RUSSIAN);
        final LanguagesUIState state
                = spy(new LanguagesUIState(source, target, query, mock(TextView.class)));

        state.notifyDirectionChanged();
        verify(state, times(1)).setQuery(QUERY);
    }

    @Test
    public void notifySwapTest_setQueryCleanTranslation() {
        final LanguageSpinner spinner = mock(LanguageSpinner.class);
        final EditText query = mock(EditText.class);
        final TextView translation = mock(TextView.class);

        when(translation.getText()).thenReturn(TRANSLATION);
        when(spinner.getAdapter()).thenReturn(mock(LanguageSpinnerAdapter.class));

        final LanguagesUIState state = spy(new LanguagesUIState(
                spinner,
                spinner,
                query,
                translation
        ));

        state.notifySwap();

        verify(translation, times(1)).getText();
        verify(translation, times(1)).setText("");
        verify(state, times(1)).setQuery(TRANSLATION);
    }

    @Test
    public void notifySwapTest_swapSpinnerValues() {
        final LanguageSpinner source = mock(LanguageSpinner.class);
        final LanguageSpinner target = mock(LanguageSpinner.class);
        final LanguageSpinnerAdapter sourceAdapter = mock(LanguageSpinnerAdapter.class);
        final LanguageSpinnerAdapter targetAdapter = mock(LanguageSpinnerAdapter.class);

        final TextView translation = mock(TextView.class);
        when(translation.getText()).thenReturn(TRANSLATION);

        when(sourceAdapter.getPosition(ENGLISH)).thenReturn(ENGLISH_INDEX);
        when(source.getAdapter()).thenReturn(sourceAdapter);
        when(source.getSelectedItem()).thenReturn(ENGLISH);

        when(targetAdapter.getPosition(RUSSIAN)).thenReturn(RUSSIAN_INDEX);
        when(target.getAdapter()).thenReturn(targetAdapter);
        when(target.getSelectedItem()).thenReturn(RUSSIAN);

        final LanguagesUIState state = new LanguagesUIState(
                source, target, mock(EditText.class), translation);

        state.notifySwap();

        verify(source, times(1)).setSelection(RUSSIAN_INDEX);
        verify(target, times(1)).setSelection(ENGLISH_INDEX);
    }

    @Test
    public void saveSnapshotTest() throws Exception {
        final LanguageSpinner source = mock(LanguageSpinner.class);
        final LanguageSpinner target = mock(LanguageSpinner.class);
        final TextView translation = mock(TextView.class);

        when(source.getAdapter()).thenReturn(mock(LanguageSpinnerAdapter.class));
        when(target.getAdapter()).thenReturn(mock(LanguageSpinnerAdapter.class));

        final LanguagesUIState state = new LanguagesUIState(
                source, target, mock(EditText.class), translation);

        state.startWith(ENGLISH, RUSSIAN);
        state.notifyLanguagesChanged(AvailableLanguagesApiTestData.AVAILABLE_LANGUAGES_ARRAY);

        verify(source, times(1)).setSelection(ENGLISH);
        verify(target, times(1)).setSelection(RUSSIAN);
    }

    @Test
    public void setQueryTest() throws Exception {
        final EditText query = mock(EditText.class);

        final LanguagesUIState state = new LanguagesUIState(
                mock(LanguageSpinner.class),
                mock(LanguageSpinner.class),
                query,
                mock(TextView.class)
        );

        state.setQuery(QUERY);

        verify(query, times(1)).setText(QUERY);
    }
}
