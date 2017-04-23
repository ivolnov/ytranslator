package com.ivolnov.ytranslator;

import android.database.Cursor;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.util.Pair;

import com.ivolnov.ytranslator.activities.MainActivity;
import com.ivolnov.ytranslator.adapters.BookmarksAdapter;
import com.ivolnov.ytranslator.adapters.HistoryAdapter;
import com.ivolnov.ytranslator.db.SQLiteEventLogLoader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * {@link MainActivity}'s instrumented unit tests.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 18.04.17
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void onLoadFinished_swapHistoryAdapterCursor() throws Exception {
        final MainActivity activity = rule.getActivity();
        final Cursor cursor = mock(Cursor.class);
        final SQLiteEventLogLoader loader = mock(SQLiteEventLogLoader.class);
        final Pair<Cursor, Cursor> cursors = Pair.create(cursor, mock(Cursor.class));
        final HistoryAdapter adapter = mock(HistoryAdapter.class);
        activity
                .withBookmarksAdapter(mock(BookmarksAdapter.class))
                .withHistoryAdapter(adapter);

        activity.onLoadFinished(loader, cursors);

        verify(adapter, times(1)).swapCursor(cursor);
    }

    @Test
    public void onLoadFinished_swapBookmarksAdapterCursor() throws Exception {
        final MainActivity activity = rule.getActivity();
        final Cursor cursor = mock(Cursor.class);
        final SQLiteEventLogLoader loader = mock(SQLiteEventLogLoader.class);
        final Pair<Cursor, Cursor> cursors = Pair.create(mock(Cursor.class), cursor);
        final BookmarksAdapter adapter = mock(BookmarksAdapter.class);
        activity
                .withHistoryAdapter(mock(HistoryAdapter.class))
                .withBookmarksAdapter(adapter);

        activity.onLoadFinished(loader, cursors);

        verify(adapter, times(1)).swapCursor(cursor);
    }
}
