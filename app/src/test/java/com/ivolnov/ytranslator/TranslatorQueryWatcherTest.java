package com.ivolnov.ytranslator;

import android.os.Handler;
import android.widget.TextView;

import com.ivolnov.ytranslator.dictionary.Dictionary;
import com.ivolnov.ytranslator.dictionary.DictionaryItem;
import com.ivolnov.ytranslator.languages.Languages;
import com.ivolnov.ytranslator.translator.Translator;
import com.ivolnov.ytranslator.translator.TranslatorQueryWatcher;

import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link TranslatorQueryWatcher} class local unit tests.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 11.04.17
 */

public class TranslatorQueryWatcherTest {

    public static final String UI = "ru";
    public static final String QUERY = "time";
    public static final String EMPTY_QUERY = "";
    public static final String DIRECTION = "en-ru";

    @Test
    public void onTextChangedTest() throws Exception {

        final TranslatorQueryWatcher watcher = new TranslatorQueryWatcher(
                mock(TextView.class),
                mock(Translator.class),
                mock(Dictionary.class),
                mock(Languages.class),
                mock(Translator.Listener.class),
                mock(Dictionary.Listener.class),
                UI
        );
        final TranslatorQueryWatcher.QueryDeliverer executor = spy(watcher.getQueryExecutor());
        final Handler handler = mock(Handler.class);
        watcher.withQueryExecutor(executor);
        watcher.withQueryHandler(handler);

        watcher.onTextChanged(QUERY, 1, 0, 3);

        verify(handler, times(1)).removeCallbacks(executor);
        verify(executor, times(1)).withQuery(QUERY);
        verify(handler, times(1)).postDelayed(executor, TranslatorQueryWatcher.TYPING_TIMEOUT);
    }

    @Test
    public void QueryExecutor_runTest() {
        final Translator translator = mock(Translator.class);
        final Dictionary dictionary = mock(Dictionary.class);
        final Languages languages = mock(Languages.class);
        final Translator.Listener translatorListener = mock(Translator.Listener.class);
        final Dictionary.Listener dictionaryListener =  mock(Dictionary.Listener.class);

        when(languages.getDirection()).thenReturn(DIRECTION);

        TranslatorQueryWatcher watcher = new TranslatorQueryWatcher(
                mock(TextView.class),
                translator,
                dictionary,
                languages,
                translatorListener,
                dictionaryListener,
                UI
        );

        final TranslatorQueryWatcher.QueryDeliverer executor = watcher.getQueryExecutor();
        executor.withQuery(QUERY);

        executor.run();

        verify(languages, times(1)).getDirection();
        verify(translator, times(1)).translate(QUERY, DIRECTION, translatorListener);
        verify(dictionary, times(1)).lookup(QUERY, DIRECTION, UI, dictionaryListener);
    }

    @Test
    public void QueryExecutor_runTest_emptyQuery() {
        final Dictionary.Listener dictionaryListener =  mock(Dictionary.Listener.class);
        final TextView translation = mock(TextView.class);

        TranslatorQueryWatcher watcher = new TranslatorQueryWatcher(
                translation,
                mock(Translator.class),
                mock(Dictionary.class),
                mock(Languages.class),
                mock(Translator.Listener.class),
                dictionaryListener,
                UI
        );

        final TranslatorQueryWatcher.QueryDeliverer executor = watcher.getQueryExecutor();
        executor.withQuery(EMPTY_QUERY);

        executor.run();

        verify(translation, times(1)).setText("");
        verify(dictionaryListener, times(1)).notifyLookedUp(Collections.<DictionaryItem>emptyList());
    }
}