package com.ivolnov.ytranslator.db;

import android.provider.BaseColumns;

/**
 * A container for constants that define names of columns and tables.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 18.04.17
 */

public final class DBContract {

    private DBContract() {}

    static final String CREATE_HISTORY_TABLE =
            "CREATE TABLE " + HistoryEntry.TABLE_NAME + " (" +
                    HistoryEntry._ID + " INTEGER PRIMARY KEY," +
                    HistoryEntry.COLUMN_NAME_QUERY + " TEXT," +
                    HistoryEntry.COLUMN_NAME_TRANSLATION + " TEXT," +
                    HistoryEntry.COLUMN_NAME_DIRECTION + " TEXT," +
                    HistoryEntry.COLUMN_NAME_BOOKMARKED + " INTEGER)";

    public static class HistoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_NAME_QUERY = "query";
        public static final String COLUMN_NAME_TRANSLATION = "translation";
        public static final String COLUMN_NAME_DIRECTION = "direction";
        public static final String COLUMN_NAME_BOOKMARKED = "bookmarked";
    }
}


