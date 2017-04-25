package com.ivolnov.ytranslator.db.jobs;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.ivolnov.ytranslator.db.DBQueries;

/**
 * {@link Runnable} that undoes bookmarking of a particular history record.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 24.04.17
 */

public class UnBookmarkRecordJob implements Runnable {

    private int index;
    private SQLiteDatabase db;
    private Runnable onComplete;

    public UnBookmarkRecordJob(SQLiteDatabase db, Runnable onComplete) {
        this.db = db;
        this.onComplete = onComplete;
    }

    @Override
    public void run() {
        if (db.isOpen()) {
            final String table = DBQueries.UpdateBookmarkedFieldQuery.table;
            final String selection = DBQueries.UpdateBookmarkedFieldQuery.selection;
            final String[] args = DBQueries.UpdateBookmarkedFieldQuery.args(index);
            final ContentValues values = DBQueries.UpdateBookmarkedFieldQuery.values(false);

            db.update(table, values, selection, args);

            onComplete.run();
        }
    }

    public UnBookmarkRecordJob withIndex(int index) {
        this.index = index;
        return this;
    }
}
