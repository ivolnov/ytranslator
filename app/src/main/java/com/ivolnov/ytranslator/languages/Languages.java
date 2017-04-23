package com.ivolnov.ytranslator.languages;

import java.util.Arrays;
import java.util.Map;

/**
 * Interface for language related operations.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 11.04.17
 */

public interface Languages {

    String SOURCE_LANGUAGE_PREFERENCE_KEY = "source_language";
    String TARGET_LANGUAGE_PREFERENCE_KEY = "target_language";

    /**
     * Loads available languages list and notifies a listener.
     *
     */
    void loadAvailableLanguages(Listener listener);

    /**
     * Gets a string that encodes source-target language pair.
     *
     * @return a string with current direction.
     */
    String getDirection();

    /**
     * Callback method to perform logic related to the change in source language.
     *
     * @param language a new string value for source language.
     */
    void onSourceLanguageChanged(String language);

    /**
     * Callback method to perform logic related to the change in target language.
     *
     * @param language a new string value for target language.
     */
    void onTargetLanguageChanged(String language);

    /**
     * Callback method to perform logic related to the swap of a target and a source languages.
     */
    void onLanguagesSwapped();

    /**
     * Attaches {@link State} instance to be used for in the work of {@link Languages}.
     *
     * @param state {@link State} implementation.
     */
    void attachState(State state);

    /**
     * The one who cares about results of available languages request.
     */
    interface Listener {
        /**
         * Callback to be implemented by listeners to handle available languages request data.
         *
         * @param data a {@link Data} instance representing available languages.
         */
        void notifyAvailableLanguages(Data data);

        /**
         * Callback to be implemented by listeners to handle available languages request errors.
         *
         * @param error an error message
         */
        void notifyAvailableLanguagesError(String error);
    }

    /**
     * Storage of languages related data.
     */
    abstract class Data {

        /**
         * Gets an ascending array of available languages.
         *
         * @return a sorted array of strings representing available languages.
         */
        public abstract String[] getSortedLanguages();

        /**
         * Gets a reverse index from language values to their tags.
         *
         * @return a {@link Map} instance than maps languages strings to tags strings.
         */
        public abstract Map<String, String> getTagIndex();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || !(o instanceof Data)) return false;

            Data data = (Data) o;

            final Map<String, String> tagIndex = getTagIndex();
            final String[] languages = getSortedLanguages();

            if (!tagIndex.equals(data.getTagIndex())) return false;

            return Arrays.equals(languages, data.getSortedLanguages());

        }

        @Override
        public int hashCode() {
            final Map<String, String> tagIndex = getTagIndex();
            final String[] languages = getSortedLanguages();

            int result = tagIndex.hashCode();
            result = 31 * result + Arrays.deepHashCode(languages);
            return result;
        }
    }

    /**
     * Something that represents the current languages situation.
     * The main purpose of this interface is to avoid having an explicit state stored in the fields
     * of {@link Languages} implementations and to delegate fetching of state data to this
     * interface. It acts like a bridge between something that holds actual state and something
     * that manages it.
     */
    interface State {

        /**
         * Notifies the state to be updated with new list of currently available languages.
         *
         * @param languages an array of strings with available languages.
         */
        void notifyLanguagesChanged(String[] languages);

        /**
         * Informs than language direction have been changed.
         */
        void notifyDirectionChanged();

        /**
         * Informs that languages are swapped.
         */
        void notifySwap();

        /**
         * Gets current source language value.
         *
         * @return a string with source language value.
         */
        String getSourceLanguage();

        /**
         * Gets current target language value.
         *
         * @return a string with target language value.
         */
        String getTargetLanguage();

        /**
         * Sets initial languages values.
         *
         * @param source a string with source language
         * @param target a string with target language
         */
        void startWith(String source, String target);
    }
}
