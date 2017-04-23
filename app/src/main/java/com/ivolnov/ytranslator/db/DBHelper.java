package com.ivolnov.ytranslator.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.ivolnov.ytranslator.db.DBContract.CREATE_HISTORY_TABLE;

/**
 * {@link SQLiteOpenHelper} class version for app's database.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 18.04.17
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG = "DBHelper";

    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "YTranslator.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, errorHandler());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + DBContract.HistoryEntry.TABLE_NAME);
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    private static DatabaseErrorHandler errorHandler() {
        return new DatabaseErrorHandler() {
            @Override
            public void onCorruption(SQLiteDatabase dbObj) {
                Log.e(TAG, DATABASE_NAME + " database corrupted.");
            }
        };
    }
}
