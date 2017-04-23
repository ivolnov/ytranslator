package com.ivolnov.ytranslator.translator;

import android.support.v4.util.LruCache;
import android.support.v4.util.Pair;

/**
 * LRU cache for translations.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 23.04.17
 */

public class TranslatorCache extends LruCache<Pair<String, String>, String> {

    public TranslatorCache(int maxSize) {
        super(maxSize);
    }

    /**
     * Stores a new cache record. First two parameters are a translation key, the third one is value.
     *
     * @param query first part f key - translation query string
     * @param direction second part of key - translation direction string
     * @param translation a value to store - translation string
     */
    public void put(String query, String direction, String translation) {
        put(Pair.create(query, direction), translation);
    }

    /**
     * Gets a value from cache. Key is a pair of parameter strings.
     *
     * @param query first part f key - translation query string
     * @param direction second part of key - translation direction string
     * @return a value with translation string
     */
    public String get(String query, String direction) {
        return get(Pair.create(query, direction));
    }

    /**
     * @see <a href="http://stackoverflow.com/a/31207050/4003403">stackoverflow</a>
     */
    @Override
    protected int sizeOf(Pair<String, String> key, String value) {

        final int querySize = 8 * ((((key.first.length()) * 2) + 45) / 8);
        final int directionSize = 8 * ((((key.first.length()) * 2) + 45) / 8);
        final int translationSize = 8 * ((((key.first.length()) * 2) + 45) / 8);

        return querySize + directionSize + translationSize;
    }
}
