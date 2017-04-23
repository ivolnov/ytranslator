package com.ivolnov.ytranslator;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.ivolnov.ytranslator.adapters.BookmarksAdapter;
import com.ivolnov.ytranslator.db.DBContract;
import com.ivolnov.ytranslator.db.EventLog;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link BookmarksAdapter} local unit tests.
 * {@link RobolectricTestRunner} is used because this class inherits from
 * {@link RecyclerView.Adapter} which is a part of android SDK.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 19.04.17
 */

@RunWith(RobolectricTestRunner.class)
public class BookmarksAdapterTest {

    public static final int ID = 7;
    public static final int COUNT = 4;
    public static final int POSITION = 16;
    public static final String QUERY = "test";
    public static final String DIRECTION = "EN_RU";
    public static final String TRANSLATION = "тест";

    @Test
    public void testGetItemCount() throws Exception {
        final BookmarksAdapter adapter = new BookmarksAdapter(mock(EventLog.class));
        final Cursor cursor = mock(Cursor.class);
        adapter.swapCursor(cursor);
        when(cursor.getCount()).thenReturn(COUNT);

        final int result = adapter.getItemCount();

        Assert.assertThat(result, is(equalTo(COUNT)));
    }

    @Test
    public void onBindViewHolderTest() throws Exception {
        final BookmarksAdapter adapter = new BookmarksAdapter(mock(EventLog.class));
        final Cursor cursor = mockCursor();
        final BookmarksAdapter.BookmarksItemViewHolder holder
                = mock(BookmarksAdapter.BookmarksItemViewHolder.class);

        adapter.swapCursor(cursor);

        adapter.onBindViewHolder(holder, POSITION);

        verify(cursor, times(1)).moveToPosition(POSITION);
        verify(holder, times(1)).setTag(ID);
        verify(holder, times(1)).setQuery(QUERY);
        verify(holder, times(1)).setDirection(DIRECTION);
        verify(holder, times(1)).setTranslation(TRANSLATION);
    }

    @Test
    public void swapCursorTest() throws Exception {
        final BookmarksAdapter adapter = spy(new BookmarksAdapter(mock(EventLog.class)));
        final Cursor cursor = mock(Cursor.class);

        adapter.swapCursor(cursor);

        final Cursor result = adapter.getCursor();

        Assert.assertThat(result, is(equalTo(cursor)));
        verify(adapter, times(1)).notifyDataSetChanged();
    }

    @Test
    public void bookmarksItemViewHolder_setTagTest() throws Exception {
        final ImageButton icon = mock(ImageButton.class);
        final View view = mock(View.class);
        when(view.findViewById(R.id.bookmark_icon)).thenReturn(icon);
        final BookmarksAdapter.BookmarksItemViewHolder holder
                = new BookmarksAdapter(mock(EventLog.class))
                .new BookmarksItemViewHolder(view);

        holder.setTag(ID);

        verify(icon, times(1)).setTag(ID);
    }

    @Test
    public void bookmarkIconClickListener_onClickTest() throws Exception {
        final View view = mock(View.class);
        final EventLog log = mock(EventLog.class);
        final BookmarksAdapter.BookmarkIconClickListener listener
                = new BookmarksAdapter(log).new BookmarkIconClickListener();
        when(view.getTag()).thenReturn(ID);

        listener.onClick(view);

        verify(log, times(1)).logUnBookmarked(ID);

    }

    private Cursor mockCursor() {
        final Cursor cursor =  mock(Cursor.class);

        when(cursor.getColumnIndex(DBContract.HistoryEntry._ID)).thenReturn(0);
        when(cursor.getColumnIndex(DBContract.HistoryEntry.COLUMN_NAME_QUERY)).thenReturn(1);
        when(cursor.getColumnIndex(DBContract.HistoryEntry.COLUMN_NAME_TRANSLATION)).thenReturn(2);
        when(cursor.getColumnIndex(DBContract.HistoryEntry.COLUMN_NAME_DIRECTION)).thenReturn(3);
        when(cursor.getInt(0)).thenReturn(ID);
        when(cursor.getString(1)).thenReturn(QUERY);
        when(cursor.getString(2)).thenReturn(TRANSLATION);
        when(cursor.getString(3)).thenReturn(DIRECTION);

        return cursor;
    }
}