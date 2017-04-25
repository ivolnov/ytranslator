package com.ivolnov.ytranslator.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.util.Pair;
import android.util.Log;

import com.ivolnov.ytranslator.db.jobs.BookmarkRecordJob;
import com.ivolnov.ytranslator.db.jobs.ForceLoadJob;
import com.ivolnov.ytranslator.db.jobs.InsertRecordJob;
import com.ivolnov.ytranslator.db.jobs.UnBookmarkRecordJob;

/**
 * SQLite powered based on {@link AsyncTaskLoader} implementation of {@link EventLog}.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 19.04.17
 */

public class SQLiteEventLogLoader extends AsyncTaskLoader<Pair<Cursor, Cursor>>
        implements EventLog {

    public static final String TAG = "SQLiteEventLogLoader";
    public static final int HISTORY_SIZE = 16 * 60 * 60; // 16 hours and types a query each second.
    public static final int USER_TYPING_TIMEOUT = 1000; // milliseconds

    private DBHelper mHelper;
    private SQLiteDatabase mDb;
    private Constraint[] mConstraints;
    private Handler mUiThread;
    private Handler mWorkerThread;
    private InsertRecordJob mInsertJob;
    private BookmarkRecordJob mBookmarkJob;
    private UnBookmarkRecordJob mUnBookmarkJob;
    private ForceLoadJob mForceLoadJob;

    public SQLiteEventLogLoader(Context context) {
        super(context);
        mHelper = new DBHelper(context);
        mConstraints = new Constraint[] {
                new SingleCharacterConstraint(),
                new LongSentenceOrWordConstraint(),
                new UntranslatedQueryConstraint(),
                new DuplicatedTranslationConstraint()
        };
        mUiThread = new Handler(context.getMainLooper());
        mWorkerThread = new Handler();
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
        initProperties();
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

        mWorkerThread.removeCallbacks(mInsertJob);
        mWorkerThread.postDelayed(
                mInsertJob.withQuery(query)
                        .withDirection(direction)
                        .withTranslation(translation)
                ,
                USER_TYPING_TIMEOUT);
    }

    @Override
    public void logBookmarked(int index) {
        if (mDb == null) {
            Log.d(TAG, "Database is not ready to store bookmark records.");
            return;
        }

        mBookmarkJob.withIndex(index);
        mWorkerThread.post(mBookmarkJob);
    }

    @Override
    public void logUnBookmarked(int index) {
        if (mDb == null) {
            Log.d(TAG, "Database is not ready to cancel bookmark records.");
            return;
        }

        mUnBookmarkJob.withIndex(index);
        mWorkerThread.post(mUnBookmarkJob);
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

    public SQLiteEventLogLoader withWorkerThread(Handler workerThread) {
        this.mWorkerThread = workerThread;
        return this;
    }

    public SQLiteEventLogLoader withInsertJob(InsertRecordJob insertJob) {
        this.mInsertJob = insertJob;
        return this;
    }

    public SQLiteEventLogLoader withBookmarkJob(BookmarkRecordJob bookmarkJob) {
        this.mBookmarkJob = bookmarkJob;
        return this;
    }

    private void initProperties() {
        mForceLoadJob = new ForceLoadJob(mUiThread, this);
        mBookmarkJob = new BookmarkRecordJob(mDb, mForceLoadJob);
        mUnBookmarkJob = new UnBookmarkRecordJob(mDb, mForceLoadJob);
        mInsertJob = new InsertRecordJob(mDb, mForceLoadJob, mConstraints);
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

        getDuplicatedTranslationConstraint().saveLastTranslationFrom(cursor);

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
        public void saveLastTranslationFrom(Cursor cursor) {
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