package com.ivolnov.ytranslator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.util.Pair;
import android.util.Log;

/**
 * SQLite powered based on {@link AsyncTaskLoader} implementation of {@link EventLog}.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 19.04.17
 */

public class SQLiteEventLogLoader extends AsyncTaskLoader<Pair<Cursor, Cursor>> implements EventLog {

    public static final String TAG = "SQLiteEventLogLoader";
    /* Day's history of a user who sleeps 8 hours and types a query each second. */
    public static final int HISTORY_SIZE = 16 * 60 * 60;

    private SQLiteDatabase mDb;
    private DBHelper mHelper;
    private Constraint[] mConstraints;

    public SQLiteEventLogLoader(Context context) {
        super(context);
        mHelper = new DBHelper(context);
        mConstraints = new Constraint[] {
                new SingleCharacterConstraint(),
                new LongSentenceOrWordConstraint(),
                new UntranslatedQueryConstraint(),
                new DuplicatedTranslationConstraint()
        };
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Pair<Cursor, Cursor> loadInBackground() {
        if (mDb == null) {
            mDb = mHelper.getWritableDatabase();
        }
        deleteOldRecords();
        return Pair.create(historyCursor(), bookmarksCursor());
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        mDb.close();
        super.onReset();
    }
    
    @Override
    public void logTranslation(String query, String translation, String direction) {

        if (mDb == null) {
            Log.d(TAG, "Database is not ready to store translation records.");
            return;
        }

        if (allConstraintsSatisfied(query, translation, direction)) {
            final String table = DBQueries.InsertTranslationQuery.table;
            final ContentValues values
                    = DBQueries.InsertTranslationQuery.values(query, translation, direction);

            mDb.insert(table, null,  values);

            forceLoad();
        }
    }

    @Override
    public void logBookmarked(int index) {
        if (mDb == null) {
            Log.d(TAG, "Database is not ready to store bookmark records.");
            return;
        }

        final String table = DBQueries.UpdateBookmarkedFieldQuery.table;
        final String selection = DBQueries.UpdateBookmarkedFieldQuery.selection;
        final String[] args = DBQueries.UpdateBookmarkedFieldQuery.args(index);
        final ContentValues values = DBQueries.UpdateBookmarkedFieldQuery.values(true);

        mDb.update(table, values, selection, args);

        forceLoad();
    }

    @Override
    public void logUnBookmarked(int index) {
        if (mDb == null) {
            Log.d(TAG, "Database is not ready to cancel bookmark records.");
            return;
        }

        final String table = DBQueries.UpdateBookmarkedFieldQuery.table;
        final String selection = DBQueries.UpdateBookmarkedFieldQuery.selection;
        final String[] args = DBQueries.UpdateBookmarkedFieldQuery.args(index);
        final ContentValues values = DBQueries.UpdateBookmarkedFieldQuery.values(false);

        mDb.update(table, values, selection, args);

        forceLoad();
    }

    public SQLiteEventLogLoader withDatabase(SQLiteDatabase db) {
        this.mDb = db;
        return this;
    }

    public SQLiteEventLogLoader withHelper(DBHelper mHelper) {
        this.mHelper = mHelper;
        return this;
    }

    public SQLiteEventLogLoader withConstraints(Constraint[] constraints) {
        this.mConstraints = constraints;
        return this;
    }


    private DuplicatedTranslationConstraint getDuplicatedTranslationConstraint() {
        for (Constraint constraint: mConstraints) {
            if (constraint instanceof DuplicatedTranslationConstraint) {
                return (DuplicatedTranslationConstraint) constraint;
            }
        }
        return new DuplicatedTranslationConstraint();
    }

    private void deleteOldRecords() {
        mDb.execSQL(DBQueries.RemoveOldRecordsFromHistory.toSize(HISTORY_SIZE));
    }

    private Cursor historyCursor() {
        final String table = DBQueries.AllHistoryQuery.table;
        final String order = DBQueries.AllHistoryQuery.order;
        final String[] projection = DBQueries.AllHistoryQuery.projection;

        final Cursor cursor = mDb.query(table, projection, null, null, null, null, order);

        getDuplicatedTranslationConstraint().setLastTranslationFrom(cursor);

        return cursor;
    }

    private Cursor bookmarksCursor() {
        final String table = DBQueries.AllBookmarksQuery.table;
        final String order = DBQueries.AllBookmarksQuery.order;
        final String selection = DBQueries.AllBookmarksQuery.selection;
        final String[] projection = DBQueries.AllBookmarksQuery.projection;
        final String[] args = DBQueries.AllBookmarksQuery.args;

        return mDb.query(table, projection, selection, args, null, null, order);
    }

    private boolean allConstraintsSatisfied(String query, String translation, String direction) {

        for (Constraint constraint: mConstraints) {
            if (!constraint.ok(query, translation, direction)) {
                return false;
            }
        }

        return true;
    }

    /**
     * {@link Constraint} implementation that checks weather the translation consists
     * of single characters only which makes it non informative to store.
     */
    public static class SingleCharacterConstraint implements Constraint {
        @Override
        public boolean ok(String query, String translation, String direction) {
            return query.length() > 1 && translation.length() > 1;
        }
    }

    /**
     * {@link Constraint} implementation that checks weather the translation consists
     * of long sentence of word which is currently impossible to display in the history layout
     * item and thus should not be stored.
     */
    public static class LongSentenceOrWordConstraint implements Constraint {

        public static final int MAX_LENGTH = 30;

        @Override
        public boolean ok(String query, String translation, String direction) {
            return query.length() <= MAX_LENGTH && translation.length() <= MAX_LENGTH;
        }
    }

    /**
     * {@link Constraint} implementation that checks weather the translation failed which
     * basically means that the translation is equal to the query.
     */
    public static class UntranslatedQueryConstraint implements Constraint {
        @Override
        public boolean ok(String query, String translation, String direction) {
            return !translation.equals(query);
        }
    }

    /**
     * {@link Constraint} implementation that checks weather the translation has nt been just
     * logged. Helps to avoid duplicated records  history. Like in the case when the user typed
     * a query than appended one symbol, changed his mind and deleted it.
     */
    public static class DuplicatedTranslationConstraint implements Constraint {

        private String lastQuery;
        private String lastDirection;

        @Override
        public boolean ok(String query, String translation, String direction) {
            return !(lastQuery == null || lastDirection == null)
                    && (!query.equals(lastQuery) || !direction.equals(lastDirection));
        }

        /**
         * Sets values of the last accounted translation to compare new ones against.
         * @param cursor a {@link Cursor} with history records.
         */
        public void setLastTranslationFrom(Cursor cursor) {
            final int queryId = cursor
                    .getColumnIndex(DBContract.HistoryEntry.COLUMN_NAME_QUERY);
            final int directionId = cursor
                    .getColumnIndex(DBContract.HistoryEntry.COLUMN_NAME_DIRECTION);

            cursor.moveToPosition(0);

            lastQuery = cursor.getCount() > 0 ? cursor.getString(queryId) : "";
            lastDirection = cursor.getCount() > 0 ? cursor.getString(directionId) : "";
        }

        public String getLastQuery() {
            return lastQuery;
        }

        public String getLastDirection() {
            return lastDirection;
        }
    }
}