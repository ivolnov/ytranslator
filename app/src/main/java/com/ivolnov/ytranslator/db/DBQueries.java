package com.ivolnov.ytranslator.db;

import android.content.ContentValues;

/**
 * A container for classes that store query data.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 21.04.17
 */

public class DBQueries {
    public static class AllHistoryQuery {
        public static final String table = DBContract.HistoryEntry.TABLE_NAME;
        public static final String order = DBContract.HistoryEntry._ID + " DESC";
        public static final String[] projection = {
                DBContract.HistoryEntry._ID,
                DBContract.HistoryEntry.COLUMN_NAME_QUERY,
                DBContract.HistoryEntry.COLUMN_NAME_TRANSLATION,
                DBContract.HistoryEntry.COLUMN_NAME_DIRECTION,
                DBContract.HistoryEntry.COLUMN_NAME_BOOKMARKED
        };
    }

    public static class AllBookmarksQuery {
        public static final String table = DBContract.HistoryEntry.TABLE_NAME;
        public static final String order = DBContract.HistoryEntry._ID + " DESC";
        public static final String selection
                = DBContract.HistoryEntry.COLUMN_NAME_BOOKMARKED + " LIKE ?";
        public static final String[] args = {"1"};
        public static final String[] projection = {
                DBContract.HistoryEntry._ID,
                DBContract.HistoryEntry.COLUMN_NAME_QUERY,
                DBContract.HistoryEntry.COLUMN_NAME_TRANSLATION,
                DBContract.HistoryEntry.COLUMN_NAME_DIRECTION
        };
    }

    public static class UpdateBookmarkedFieldQuery {
        public static final String table = DBContract.HistoryEntry.TABLE_NAME;
        public static final String selection = DBContract.HistoryEntry._ID + " LIKE ?";

        public static final ContentValues values(boolean bookmarkedOn) {
            final ContentValues values = new ContentValues();
            values.put(DBContract.HistoryEntry.COLUMN_NAME_BOOKMARKED, bookmarkedOn ? 1 : 0);
            return values;
        }

        public static String[] args(int id) {
            return new String[] { Integer.toString(id) };
        }
    }

    public static class InsertTranslationQuery {
        public static final String table = DBContract.HistoryEntry.TABLE_NAME;

        public static ContentValues values(String query, String translation, String direction) {
            final ContentValues values = new ContentValues();

            values.put(DBContract.HistoryEntry.COLUMN_NAME_QUERY, query);
            values.put(DBContract.HistoryEntry.COLUMN_NAME_DIRECTION, direction);
            values.put(DBContract.HistoryEntry.COLUMN_NAME_TRANSLATION, translation);

            return values;
        }
    }

    public static class RemoveOldRecordsFromHistory {
        public static final String table = DBContract.HistoryEntry.TABLE_NAME;
        public static final String id = DBContract.HistoryEntry._ID;

        public static String toSize(int size) {
            return "DELETE FROM " + table + " WHERE " + id + " NOT IN " +
                    "(SELECT " + id + " FROM " + table + " ORDER BY " + id +
                    " DESC LIMIT " + size + ")";
        }
    }
}