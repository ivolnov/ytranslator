package com.ivolnov.ytranslator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.Pair;


import com.ivolnov.ytranslator.db.DBHelper;
import com.ivolnov.ytranslator.db.EventLog;
import com.ivolnov.ytranslator.db.SQLiteEventLogLoader;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.ivolnov.ytranslator.db.DBContract.*;

/**
 * {@link SQLiteEventLogLoader} local unit tests.
 *
 * {@link RobolectricTestRunner} is used because {@link ContentValues} is a part of android SDK.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 18.04.17
 */


@RunWith(RobolectricTestRunner.class)
public class SQLiteEventLogLoaderTest {

    public static final int ID = 7;
    public static final int MAX = SQLiteEventLogLoader.LongSentenceOrWordConstraint.MAX_LENGTH;
    public static final String QUERY = "test";
    public static final String DIRECTION = "EN_RU";
    public static final String TRANSLATION = "тест";
    public static final String SINGLE_CHARACTER = wordOfLength(1);
    public static final String LONG_WORD = wordOfLength(MAX + 1);
    public static final String DATABASE = HistoryEntry.TABLE_NAME;
    public static final String SELECTION = null;
    public static final String BOOKMARK_ID_SELECTION = HistoryEntry._ID + " LIKE ?";
    public static final String BOOKMARKS_SELECTION = HistoryEntry.COLUMN_NAME_BOOKMARKED + " LIKE ?";
    public static final String ORDER = HistoryEntry._ID + " DESC";
    public static final String[] SELECTION_ARGS = null;
    public static final String[] BOOKMARKS_SELECTION_ARGS = {"1"};
    public static final String[] BOOKMARK_ID_SELECTION_ARGS = {Integer.toString(ID)};
    public static final String[] PROJECTION = {
            HistoryEntry._ID,
            HistoryEntry.COLUMN_NAME_QUERY,
            HistoryEntry.COLUMN_NAME_TRANSLATION,
            HistoryEntry.COLUMN_NAME_DIRECTION,
            HistoryEntry.COLUMN_NAME_BOOKMARKED
    };
    public static final String[] BOOKMARKS_PROJECTION = {
            HistoryEntry._ID,
            HistoryEntry.COLUMN_NAME_QUERY,
            HistoryEntry.COLUMN_NAME_TRANSLATION,
            HistoryEntry.COLUMN_NAME_DIRECTION
    };
    
    

    @Test
    public void logTranslationTest() throws Exception {
        final SQLiteDatabase database = mock(SQLiteDatabase.class);
        final ContentValues values = new ContentValues();

        values.put(HistoryEntry.COLUMN_NAME_QUERY, QUERY);
        values.put(HistoryEntry.COLUMN_NAME_DIRECTION, DIRECTION);
        values.put(HistoryEntry.COLUMN_NAME_TRANSLATION, TRANSLATION);

        SQLiteEventLogLoader log = spy(new SQLiteEventLogLoader(mock(Context.class)));
        log.withDatabase(database);
        log.withConstraints(new EventLog.Constraint[]{});

        log.logTranslation(QUERY, TRANSLATION, DIRECTION);

        verify(database, times(1)).insert(eq(DATABASE), nullable(String.class), eq(values));

        verify(log, times(1)).forceLoad();
    }

    @Test
    public void logBookmarkedTest() throws Exception {
        final SQLiteDatabase database = mock(SQLiteDatabase.class);
        final ContentValues values = new ContentValues();

        values.put(HistoryEntry.COLUMN_NAME_BOOKMARKED, 1);

        SQLiteEventLogLoader log = spy(new SQLiteEventLogLoader(mock(Context.class)));
        log.withDatabase(database);
        log.withConstraints(new EventLog.Constraint[]{});

        log.logBookmarked(ID);

        verify(database, times(1)).update(
                DATABASE,
                values,
                BOOKMARK_ID_SELECTION,
                BOOKMARK_ID_SELECTION_ARGS);

        verify(log, times(1)).forceLoad();
    }

    @Test
    public void logUnBookmarkedTest() throws Exception {
        final SQLiteDatabase database = mock(SQLiteDatabase.class);
        final ContentValues values = new ContentValues();

        values.put(HistoryEntry.COLUMN_NAME_BOOKMARKED, 0);

        SQLiteEventLogLoader log = spy(new SQLiteEventLogLoader(mock(Context.class)));
        log.withDatabase(database);
        log.withConstraints(new EventLog.Constraint[]{});

        log.logUnBookmarked(ID);

        verify(database, times(1)).update(
                DATABASE,
                values,
                BOOKMARK_ID_SELECTION,
                BOOKMARK_ID_SELECTION_ARGS);

        verify(log, times(1)).forceLoad();
    }

    @Test
    public void loadInBackgroundTest() throws Exception {
        final Cursor historyCursor = mock(Cursor.class);
        final Cursor bookmarksCursor = mock(Cursor.class);
        final SQLiteDatabase database = mock(SQLiteDatabase.class);
        final DBHelper helper = mock(DBHelper.class);
        final SQLiteEventLogLoader log = new SQLiteEventLogLoader(mock(Context.class));
        final SQLiteEventLogLoader.DuplicatedTranslationConstraint constraint
                = spy(new SQLiteEventLogLoader.DuplicatedTranslationConstraint());

        log.withHelper(helper);
        log.withConstraints(new EventLog.Constraint[]{constraint});

        when(helper.getWritableDatabase()).thenReturn(database);
        when(database.query(
                eq(DATABASE),
                eq(PROJECTION),
                eq(SELECTION),
                eq(SELECTION_ARGS),
                nullable(String.class),
                nullable(String.class),
                eq(ORDER)
        )).thenReturn(historyCursor);
        when(database.query(
                eq(DATABASE),
                eq(BOOKMARKS_PROJECTION),
                eq(BOOKMARKS_SELECTION),
                eq(BOOKMARKS_SELECTION_ARGS),
                nullable(String.class),
                nullable(String.class),
                eq(ORDER)
        )).thenReturn(bookmarksCursor);

        when(historyCursor.getColumnIndex(HistoryEntry.COLUMN_NAME_QUERY)).thenReturn(1);
        when(historyCursor.getColumnIndex(HistoryEntry.COLUMN_NAME_DIRECTION)).thenReturn(3);
        when(historyCursor.getString(1)).thenReturn(QUERY);
        when(historyCursor.getString(3)).thenReturn(DIRECTION);
        when(historyCursor.getCount()).thenReturn(1);
        
        final Pair<Cursor, Cursor> result = log.loadInBackground();
        
        Assert.assertThat(result.first, is(equalTo(historyCursor)));
        Assert.assertThat(result.second, is(equalTo(bookmarksCursor)));

        verify(helper,times(1)).getWritableDatabase();
        
        verify(database, times(1))
                .query(
                        DATABASE, 
                        PROJECTION,
                        SELECTION,
                        SELECTION_ARGS,
                        null, null, ORDER);
        verify(database, times(1))
                .query(
                        DATABASE,
                        BOOKMARKS_PROJECTION,
                        BOOKMARKS_SELECTION,
                        BOOKMARKS_SELECTION_ARGS,
                        null, null, ORDER);

        verify(constraint, times(1)).setLastTranslationFrom(historyCursor);
    }

    @Test
    public void singleCharacterConstraintTest() throws Exception {
        final SQLiteDatabase database = mock(SQLiteDatabase.class);
        final SQLiteEventLogLoader log = new SQLiteEventLogLoader(mock(Context.class));
        final EventLog.Constraint[] constraints
                = {new SQLiteEventLogLoader.SingleCharacterConstraint()};

        log.withDatabase(database);
        log.withConstraints(constraints);

        log.logTranslation(SINGLE_CHARACTER, SINGLE_CHARACTER + ' ', null);

        verify(database, never())
                .insert(any(String.class), nullable(String.class), any(ContentValues.class));

        log.logTranslation(SINGLE_CHARACTER + ' ', SINGLE_CHARACTER, null);

        verify(database, never())
                .insert(any(String.class), nullable(String.class), any(ContentValues.class));
    }

    @Test
    public void longSentenceOrWordConstraintTest() throws Exception {
        final SQLiteDatabase database = mock(SQLiteDatabase.class);
        final SQLiteEventLogLoader log = new SQLiteEventLogLoader(mock(Context.class));
        final EventLog.Constraint[] constraints
                = {new SQLiteEventLogLoader.LongSentenceOrWordConstraint()};

        log.withDatabase(database);
        log.withConstraints(constraints);

        log.logTranslation(LONG_WORD, SINGLE_CHARACTER, null);

        verify(database, never())
                .insert(any(String.class), nullable(String.class), any(ContentValues.class));

        log.logTranslation(SINGLE_CHARACTER, LONG_WORD, null);

        verify(database, never())
                .insert(any(String.class), nullable(String.class), any(ContentValues.class));
    }

    @Test
    public void untranslatedQueryConstraintTest() throws Exception {
        final SQLiteDatabase database = mock(SQLiteDatabase.class);
        final SQLiteEventLogLoader log = new SQLiteEventLogLoader(mock(Context.class));
        final EventLog.Constraint[] constraints
                = {new SQLiteEventLogLoader.UntranslatedQueryConstraint()};

        log.withDatabase(database);
        log.withConstraints(constraints);

        log.logTranslation(QUERY, QUERY, null);

        verify(database, never())
                .insert(any(String.class), nullable(String.class), any(ContentValues.class));
    }

    @Test
    public void duplicatedTranslationConstraintTest() throws Exception {
        final Cursor cursor = mock(Cursor.class);
        final SQLiteEventLogLoader.DuplicatedTranslationConstraint constraint
                = new SQLiteEventLogLoader.DuplicatedTranslationConstraint();

        when(cursor.getColumnIndex(HistoryEntry.COLUMN_NAME_QUERY)).thenReturn(1);
        when(cursor.getColumnIndex(HistoryEntry.COLUMN_NAME_DIRECTION)).thenReturn(3);
        when(cursor.getString(1)).thenReturn(QUERY);
        when(cursor.getString(3)).thenReturn(DIRECTION);
        when(cursor.getCount()).thenReturn(1);

        constraint.setLastTranslationFrom(cursor);

        Assert.assertThat(constraint.getLastQuery(), is(equalTo(QUERY)));
        Assert.assertThat(constraint.getLastDirection(), is(equalTo(DIRECTION)));

    }

    private static String wordOfLength(int length) {
        final char FILLER = 'z';
        final char[] array = new char[length];
        Arrays.fill(array, FILLER);
        return new String(array);
    }
}