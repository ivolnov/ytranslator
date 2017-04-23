package com.ivolnov.ytranslator.db;

/**
 * Something that accounts events.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 18.04.17
 */

public interface EventLog {

    /**
     * Accounts a translation event.
     *
     * @param query translation's query string.
     * @param translation translation's result string.
     * @param direction translation's direction string.
     */
    void logTranslation(String query, String translation, String direction);

    /**
     * Accounts that a particular translation has been bookmarked.
     *
     * @param index a unique identifier of a translation record.
     */
    void logBookmarked(int index);

    /**
     * Accounts that a particular translation has been removed from bookmarks.
     *
     * @param index a unique identifier of a translation record.
     */
    void logUnBookmarked(int index);

    /**
     * A predicate that tells weather the translation should be stored in history.
     * Designed to avoid unnecessary records in history such as:
     * - single word translations
     * - long articles
     * - untranslated queries
     *  etc.
     */
    interface Constraint {

        /**
         * Validates the given translation parameters.
         *
         * @param query translation's query string.
         * @param translation translation's result string.
         * @param direction translation's direction string.
         * @return true in case this translation should be recorded.
         */
        boolean ok(String query, String translation, String direction);
    }
}