package com.ivolnov.ytranslator.translator;

import android.widget.EditText;

/**
 * Something that gives bidirectional access to the translation query view.
 * This interface allows different modules to communicate with each other:
 * changing query string will spawn UI events that will trigger appropriate modules.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 13.04.17
 */

public interface TranslationQueryBridge {
    /**
     * Gets translation query view.
     *
     * @return an {@link EditText} instance representing translation query.
     */
    EditText getQuery();

    /**
     * Sets translation query view text attribute.
     *
     * @param query new query's string value.
     */
    void setQuery(String query);
}
