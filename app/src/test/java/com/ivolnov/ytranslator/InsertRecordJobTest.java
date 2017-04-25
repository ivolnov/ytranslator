package com.ivolnov.ytranslator;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.ivolnov.ytranslator.db.DBContract;
import com.ivolnov.ytranslator.db.EventLog;
import com.ivolnov.ytranslator.db.jobs.InsertRecordJob;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link InsertRecordJob} local unit tests.
 *
 * {@link RobolectricTestRunner} is used because {@link ContentValues} is a part of android SDK.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 24.04.17
 */

@RunWith(RobolectricTestRunner.class)
public class InsertRecordJobTest {

    public static final String QUERY = "test";
    public static final String DIRECTION = "EN_RU";
    public static final String TRANSLATION = "тест";
    public static final String DATABASE = DBContract.HistoryEntry.TABLE_NAME;

    @Test
    public void runTest() throws Exception {

        final SQLiteDatabase database = mock(SQLiteDatabase.class);
        final ContentValues values = new ContentValues();
        final Runnable onComplete = mock(Runnable.class);

        when(database.isOpen()).thenReturn(true);

        final InsertRecordJob job = new InsertRecordJob(
                database,
                onComplete,
                new EventLog.Constraint[]{})
                .withQuery(QUERY)
                .withTranslation(TRANSLATION)
                .withDirection(DIRECTION);

        values.put(DBContract.HistoryEntry.COLUMN_NAME_QUERY, QUERY);
        values.put(DBContract.HistoryEntry.COLUMN_NAME_DIRECTION, DIRECTION);
        values.put(DBContract.HistoryEntry.COLUMN_NAME_TRANSLATION, TRANSLATION);

        job.run();

        verify(database, times(1)).insert(eq(DATABASE), nullable(String.class), eq(values));

        verify(onComplete, times(1)).run();
    }
}
