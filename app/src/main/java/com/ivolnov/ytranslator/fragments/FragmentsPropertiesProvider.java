package com.ivolnov.ytranslator.fragments;

import com.ivolnov.ytranslator.adapters.BookmarksAdapter;
import com.ivolnov.ytranslator.adapters.HistoryAdapter;
import com.ivolnov.ytranslator.dictionary.Dictionary;
import com.ivolnov.ytranslator.languages.Languages;
import com.ivolnov.ytranslator.translator.Translator;

/**
 * A source of properties' values for fragments.
 *
 * Because of its lifecycle a fragment will be recreated simply via a constructor even
 * when we initially instantiated it from some fancy code. We need a way to set its non
 * layout properties during onCreateView() (onCreate() is too early the parent activity might not
 * be fully initialised ) as we can't do it elsewhere. As long as the fragment is not responsible
 * for creation of these properties some third party must implement this interface.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 15.04.17
 */
public interface FragmentsPropertiesProvider {

    /**
     * Provides an instance of {@link Translator} class.
     *
     * @return a fully initialised translator.
     */
    Translator getTranslator();

    /**
     * Provides an instance of {@link Dictionary} class.
     *
     * @return a fully initialised dictionary.
     */
    Dictionary getDictionary();

    /**
     * Provides an instance of {@link Languages} class.
     *
     * @return a fully initialised languages.
     */
    Languages getLanguages();

    /**
     * Provides an instance of {@link HistoryAdapter} class.
     *
     * @return a fully initialised history adapter.
     */
    HistoryAdapter getHistoryAdapter();

    /**
     * Provides an instance of {@link BookmarksAdapter} class.
     *
     * @return a fully initialised bookmarks adapter.
     */
    BookmarksAdapter getBookmarksAdapter();
}
