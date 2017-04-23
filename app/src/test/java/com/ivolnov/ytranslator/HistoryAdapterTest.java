package com.ivolnov.ytranslator;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.ivolnov.ytranslator.adapters.HistoryAdapter;
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
 * {@link HistoryAdapter} local unit tests.
 * {@link RobolectricTestRunner} is used because this class inherits from
 * {@link RecyclerView.Adapter} which is a part of android SDK.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 19.04.17
 */

@RunWith(RobolectricTestRunner.class)
public class HistoryAdapterTest {

    public static final int ID = 7;
    public static final int COUNT = 4;
    public static final int POSITION = 16;
    public static final int BOOKMARKED = 1;
    public static final String QUERY = "test";
    public static final String DIRECTION = "EN_RU";
    public static final String TRANSLATION = "тест";
    public static final String ICON_DESCRIPTION = "Some description";

    @Test
    public void testGetItemCount() throws Exception {
        final HistoryAdapter adapter = new HistoryAdapter(mock(EventLog.class));
        final Cursor cursor = mock(Cursor.class);
        adapter.swapCursor(cursor);
        when(cursor.getCount()).thenReturn(COUNT);

        final int result = adapter.getItemCount();

        Assert.assertThat(result, is(equalTo(COUNT)));
    }

    @Test
    public void onBindViewHolderTest() throws Exception {
        final HistoryAdapter adapter = new HistoryAdapter(mock(EventLog.class));
        final Cursor cursor = mockCursor();
        final HistoryAdapter.HistoryItemViewHolder holder
                = mock(HistoryAdapter.HistoryItemViewHolder.class);

        adapter.swapCursor(cursor);

        adapter.onBindViewHolder(holder, POSITION);

        verify(cursor, times(1)).moveToPosition(POSITION);
        verify(holder, times(1)).setTag(ID);
        verify(holder, times(1)).setQuery(QUERY);
        verify(holder, times(1)).setDirection(DIRECTION);
        verify(holder, times(1)).setBookmarked(BOOKMARKED);
        verify(holder, times(1)).setTranslation(TRANSLATION);
    }

    @Test
    public void swapCursorTest() throws Exception {
        final HistoryAdapter adapter = spy(new HistoryAdapter(mock(EventLog.class)));
        final Cursor cursor = mock(Cursor.class);

        adapter.swapCursor(cursor);

        final Cursor result = adapter.getCursor();

        Assert.assertThat(result, is(equalTo(cursor)));
        verify(adapter, times(1)).notifyDataSetChanged();
    }

    @Test
    public void historyItemViewHolder_setBookmarked() throws Exception {
        final ImageButton icon = mock(ImageButton.class);
        final View view = mock(View.class);
        when(view.findViewById(R.id.bookmark_icon)).thenReturn(icon);
        final HistoryAdapter.HistoryItemViewHolder holder
                = new HistoryAdapter(mock(EventLog.class))
                .new HistoryItemViewHolder(view);

        holder.setBookmarked(1);
        verify(icon, times(1)).setImageResource(R.drawable.ic_bookmark_yellow_24dp);

        holder.setBookmarked(0);
        verify(icon, times(1)).setImageResource(R.drawable.ic_bookmark_grey_24dp);
    }

    @Test
    public void historyItemViewHolder_setTagTest() throws Exception {
        final ImageButton icon = mock(ImageButton.class);
        final View view = mock(View.class);
        when(view.findViewById(R.id.bookmark_icon)).thenReturn(icon);
        final HistoryAdapter.HistoryItemViewHolder holder
                = new HistoryAdapter(mock(EventLog.class))
                .new HistoryItemViewHolder(view);

        holder.setTag(ID);

        verify(icon, times(1)).setTag(ID);
    }

    @Test
    public void bookmarkIconClickListener_onClickTest() throws Exception {
        final ImageButton icon = mock(ImageButton.class);
        final EventLog log = mock(EventLog.class);
        final HistoryAdapter.BookmarkIconClickListener listener
                = new HistoryAdapter(log)
                .withIconDescription(ICON_DESCRIPTION)
                .new BookmarkIconClickListener();
        when(icon.getTag()).thenReturn(ID);
        when(icon.getContentDescription()).thenReturn(ICON_DESCRIPTION);

        listener.onClick(icon);

        verify(log, times(1)).logBookmarked(ID);
    }

    @Test
    public void bookmarkIconClickListener_onClickTest_whenIconActive() throws Exception {
        final ImageButton icon = mock(ImageButton.class);
        final EventLog log = mock(EventLog.class);
        final HistoryAdapter.BookmarkIconClickListener listener
                = new HistoryAdapter(log)
                .withActiveIconDescription(ICON_DESCRIPTION)
                .new BookmarkIconClickListener();
        when(icon.getTag()).thenReturn(ID);
        when(icon.getContentDescription()).thenReturn(ICON_DESCRIPTION);

        listener.onClick(icon);

        verify(log, times(1)).logUnBookmarked(ID);
    }

    private Cursor mockCursor() {
        final Cursor cursor =  mock(Cursor.class);

        when(cursor.getColumnIndex(DBContract.HistoryEntry._ID)).thenReturn(0);
        when(cursor.getColumnIndex(DBContract.HistoryEntry.COLUMN_NAME_QUERY)).thenReturn(1);
        when(cursor.getColumnIndex(DBContract.HistoryEntry.COLUMN_NAME_TRANSLATION)).thenReturn(2);
        when(cursor.getColumnIndex(DBContract.HistoryEntry.COLUMN_NAME_DIRECTION)).thenReturn(3);
        when(cursor.getColumnIndex(DBContract.HistoryEntry.COLUMN_NAME_BOOKMARKED)).thenReturn(4);
        when(cursor.getInt(0)).thenReturn(ID);
        when(cursor.getString(1)).thenReturn(QUERY);
        when(cursor.getString(2)).thenReturn(TRANSLATION);
        when(cursor.getString(3)).thenReturn(DIRECTION);
        when(cursor.getInt(4)).thenReturn(BOOKMARKED);

        return cursor;
    }
}