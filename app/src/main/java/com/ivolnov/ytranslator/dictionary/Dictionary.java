package com.ivolnov.ytranslator.dictionary;

import java.util.List;

/**
 * Interface to dictionary functionality.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 06.04.17
 */

public interface Dictionary {
    /**
     * Asynchronously looks up the given query string and notifies the listener.
     *
     * @param query a string to lookup
     * @param direction a string that specifies language direction
     * @param ui a string that specifies current ui localization.
     * @param listener a {@link Listener} instance to be notified on lookup ready
     */
    void lookup(String query, String direction, String ui, Listener listener);

    /**
     * Cancels all pending requests if exist.
     * Must be called when an activity or fragment is stopped.
     */
    void stopPending();

    /**
     * The one who cares about directory look up results.
     */
    interface Listener {
        /**
         * Callback to be implemented by listeners to handle lookup result.
         *
         * @param items an {@link List<DictionaryItem>} representing a dictionary article items.
         */
        void notifyLookedUp(List<DictionaryItem> items);

        /**
         * Callback to be implemented by listeners to handle lookup errors.
         *
         * @param error an error message
         */
        void notifyLookupError(String error);
    }
}
