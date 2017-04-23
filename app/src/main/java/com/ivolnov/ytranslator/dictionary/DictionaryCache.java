package com.ivolnov.ytranslator.dictionary;

import android.support.v4.util.LruCache;

import java.util.List;

/**
 * LRU cache for dictionary lookups.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 23.04.17
 */

public class DictionaryCache extends LruCache<DictionaryCache.Key, List<DictionaryItem>> {

    public DictionaryCache(int maxSize) {
        super(maxSize);
    }

    /**
     * @see <a href="http://stackoverflow.com/a/258150/4003403">stackoverflow</a>
     */
    @Override
    protected int sizeOf(Key key, List<DictionaryItem> value) {
        int valuesSize = 16 + 4;
        for (DictionaryItem item: value) {
            valuesSize += 4 + item.getSize();
        }
        return key.getSize() + valuesSize;
    }


    public static class Key {
        public final String query;
        public final String direction;
        public final String ui;

        private Key(String query, String direction, String ui) {
            this.query = query;
            this.direction = direction;
            this.ui = ui;
        }

        public static Key from(String query, String direction, String ui) {
            return new Key(query, direction, ui);
        }

        /**
         * @see <a href="http://stackoverflow.com/a/31207050/4003403">stackoverflow</a>
         */
        public int getSize() {
            final int querySize = query != null ? 8 * ((((query.length()) * 2) + 45) / 8) : 0;
            final int directionSize
                    = direction != null ? 8 * ((((direction.length()) * 2) + 45) / 8) : 0;
            final int uiSize = ui != null ? 8 * ((((ui.length()) * 2) + 45) / 8) : 0;

            return 4 * 3 + querySize + directionSize + uiSize;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (!query.equals(key.query)) return false;
            if (!direction.equals(key.direction)) return false;
            return ui.equals(key.ui);

        }

        @Override
        public int hashCode() {
            int result = query.hashCode();
            result = 31 * result + direction.hashCode();
            result = 31 * result + ui.hashCode();
            return result;
        }
    }
}
