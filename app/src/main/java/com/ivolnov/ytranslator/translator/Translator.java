package com.ivolnov.ytranslator.translator;

/**
 * Interface to translation functionality.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 03.04.17
 */

public interface Translator {

    /**
     * Asynchronously translates the given query string and notifies the listener.
     *
     * @param query a string to translate.
     * @param direction a string that specifies language direction
     * @param listener a {@link Listener} instance to be notified on translation ready.
     */
    void translate(String query, String direction, Listener listener);

    /**
     * Cancels all pending requests if exist.
     * Must be called when an activity or fragment is stopped.
     */
    void stopPending();

    /**
     * The one who cares about translation results.
     */
    interface Listener {
        /**
         * Callback to be implemented by listeners to handle translation result.
         *
         * @param query a string representing a translation query.
         * @param direction a string representing translation direction.
         * @param translation a string representing a translation result.
         */
        void notifyTranslated(String query, String direction, String translation);

        /**
         * Callback to be implemented by listeners to handle translation errors.
         *
         * @param error an error message
         */
        void notifyTranslationError(String error);
    }
}
