package com.ivolnov.ytranslator.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ivolnov.ytranslator.R;
import com.ivolnov.ytranslator.db.DBContract;
import com.ivolnov.ytranslator.db.EventLog;

/**
 * An {@link RecyclerView.Adapter} responsible for bookmarks list content.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 31.03.17
 */

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.BookmarksItemViewHolder>
{

    private BookmarkIconClickListener mListener;
    private Cursor mCursor;
    private EventLog mLog;

    /**
     * Constructor.
     *
     * @param log an {@link EventLog} instance to send events from view listeners to.
     */
    public BookmarksAdapter(EventLog log) {
        this.mLog = log;
        this.mListener = new BookmarkIconClickListener();
    }

    @Override
    public BookmarksItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookmarks_item, parent, false);
        return new BookmarksItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookmarksItemViewHolder holder, int position) {

        mCursor.moveToPosition(position);

        final int idId
                = mCursor.getColumnIndex(DBContract.HistoryEntry._ID);
        final int queryId
                = mCursor.getColumnIndex(DBContract.HistoryEntry.COLUMN_NAME_QUERY);
        final int directionId
                = mCursor.getColumnIndex(DBContract.HistoryEntry.COLUMN_NAME_DIRECTION);
        final int translationId
                = mCursor.getColumnIndex(DBContract.HistoryEntry.COLUMN_NAME_TRANSLATION);

        final int id = mCursor.getInt(idId);
        final String query = mCursor.getString(queryId);
        final String translation = mCursor.getString(translationId);
        final String direction = mCursor.getString(directionId).toUpperCase();

        holder.setTag(id);
        holder.setQuery(query);
        holder.setDirection(direction);
        holder.setTranslation(translation);
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0: mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public class BookmarksItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView bookmarkQuery;
        private final TextView bookmarksTranslation;
        private final TextView bookmarksDirection;
        private final ImageButton bookmarkIcon;

        public BookmarksItemViewHolder(View view) {
            super(view);
            this.bookmarkQuery = (TextView) view.findViewById(R.id.bookmarkQuery);
            this.bookmarksTranslation = (TextView) view.findViewById(R.id.bookmarkTranslation);
            this.bookmarksDirection = (TextView) view.findViewById(R.id.bookmarkDirection);
            this.bookmarkIcon = (ImageButton) view.findViewById(R.id.bookmark_icon);
        }

        public void setQuery(String query) {
            bookmarkQuery.setText(query);
        }

        public void setDirection(String direction) {
            bookmarksDirection.setText(direction);
        }

        public void setTranslation(String translation) {
            bookmarksTranslation.setText(translation);
        }

        public void setTag(int tag) {
            bookmarkIcon.setTag(tag);
            bookmarkIcon.setOnClickListener(mListener);
        }
    }

    public class BookmarkIconClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final int id = (Integer) v.getTag();
            mLog.logUnBookmarked(id);
        }
    }

    public BookmarksAdapter withLog(EventLog log) {
        this.mLog = log;
        return this;
    }

    public EventLog getLog() {
        return mLog;
    }
}
