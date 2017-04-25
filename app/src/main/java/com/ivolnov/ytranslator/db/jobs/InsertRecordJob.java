package com.ivolnov.ytranslator.db.jobs;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.ivolnov.ytranslator.db.DBQueries;
import com.ivolnov.ytranslator.db.EventLog;

/**
 * {@link Runnable} that inserts a history record.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 24.04.17
 */

public class InsertRecordJob implements Runnable {

    private String query;
    private String direction;
    private String translation;
    private SQLiteDatabase db;
    private Runnable onComplete;
    private EventLog.Constraint[] constraints;

    public InsertRecordJob(SQLiteDatabase db, Runnable onComplete, EventLog.Constraint[] constraints) {
        this.db = db;
        this.onComplete = onComplete;
        this.constraints = constraints;
    }

    @Override
    public void run() {
        if (db.isOpen() && allConstraintsSatisfied(query, translation, direction)) {

            final String table = DBQueries.InsertTranslationQuery.table;
            final ContentValues values
                    = DBQueries.InsertTranslationQuery.values(query, translation, direction);

            db.insert(table, null,  values);

            onComplete.run();
        }
    }

    public InsertRecordJob withQuery(String query) {
        this.query = query;
        return this;
    }

    public InsertRecordJob withDirection(String direction) {
        this.direction = direction;
        return this;
    }

    public InsertRecordJob withTranslation(String translation) {
        this.translation = translation;
        return this;
    }

    private boolean allConstraintsSatisfied(String query, String translation, String direction)
    {
        for (EventLog.Constraint constraint: constraints) {
            if (!constraint.ok(query, translation, direction)) {
                return false;
            }
        }

        return true;
    }
}
