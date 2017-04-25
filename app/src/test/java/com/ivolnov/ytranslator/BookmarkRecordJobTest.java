package com.ivolnov.ytranslator;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.ivolnov.ytranslator.db.jobs.BookmarkRecordJob;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.ivolnov.ytranslator.db.DBContract.HistoryEntry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link BookmarkRecordJob} local unit tests.
 *
 * {@link RobolectricTestRunner} is used because {@link ContentValues} is a part of android SDK.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 24.04.17
 */

@RunWith(RobolectricTestRunner.class)
public class BookmarkRecordJobTest {

    public static final int ID = 7;
    public static final String DATABASE = HistoryEntry.TABLE_NAME;
    public static final String BOOKMARK_ID_SELECTION = HistoryEntry._ID + " LIKE ?";
    public static final String[] BOOKMARK_ID_SELECTION_ARGS = {Integer.toString(ID)};

    @Test
    public void runTest() throws Exception {
        final SQLiteDatabase database = mock(SQLiteDatabase.class);
        final ContentValues values = new ContentValues();
        final Runnable onComplete = mock(Runnable.class);

        when(database.isOpen()).thenReturn(true);

        final BookmarkRecordJob job = new BookmarkRecordJob(database, onComplete)
                .withIndex(ID);

        values.put(HistoryEntry.COLUMN_NAME_BOOKMARKED, 1);

        job.run();

        verify(database, times(1)).update(
                DATABASE,
                values,
                BOOKMARK_ID_SELECTION,
                BOOKMARK_ID_SELECTION_ARGS);

        verify(onComplete, times(1)).run();

    }
}