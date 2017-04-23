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
 * An {@link RecyclerView.Adapter} responsible for the history's recycler view content.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 31.03.17
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryItemViewHolder> {

    private String mActiveIconDescription;
    private String mIconDescription;
    private BookmarkIconClickListener mListener;
    private Cursor mCursor;
    private EventLog mLog;

    /**
     * Constructor.
     *
     * @param log an {@link EventLog} instance to send events from view listeners to.
     */
    public HistoryAdapter(EventLog log) {
        this.mLog = log;
        this.mListener = new BookmarkIconClickListener();
    }

    @Override
    public HistoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new HistoryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HistoryItemViewHolder holder, int position) {

        mCursor.moveToPosition(position);

        final int idId
                = mCursor.getColumnIndex(DBContract.HistoryEntry._ID);
        final int queryId
                = mCursor.getColumnIndex(DBContract.HistoryEntry.COLUMN_NAME_QUERY);
        final int directionId
                = mCursor.getColumnIndex(DBContract.HistoryEntry.COLUMN_NAME_DIRECTION);
        final int translationId
                = mCursor.getColumnIndex(DBContract.HistoryEntry.COLUMN_NAME_TRANSLATION);
        final int bookmarkedId
                = mCursor.getColumnIndex(DBContract.HistoryEntry.COLUMN_NAME_BOOKMARKED);

        final int id = mCursor.getInt(idId);
        final int bookmarked = mCursor.getInt(bookmarkedId);
        final String query = mCursor.getString(queryId);
        final String translation = mCursor.getString(translationId);
        final String direction = mCursor.getString(directionId).toUpperCase();

        holder.setTag(id);
        holder.setQuery(query);
        holder.setDirection(direction);
        holder.setBookmarked(bookmarked);
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

    /**
     * Sets active bookmark icon's content description string.
     */
    public HistoryAdapter withActiveIconDescription(String description) {
        this.mActiveIconDescription = description;
        return this;
    }

    /**
     * Sets inactive bookmark icon's content description string.
     */
    public HistoryAdapter withIconDescription(String description) {
        this.mIconDescription = description;
        return this;
    }

    public class HistoryItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView historyQuery;
        private final TextView historyTranslation;
        private final TextView historyDirection;
        private final ImageButton bookmarkIcon;

        public HistoryItemViewHolder(View view) {
            super(view);
            this.historyQuery = (TextView) view.findViewById(R.id.historyQuery);
            this.historyTranslation = (TextView) view.findViewById(R.id.historyTranslation);
            this.historyDirection = (TextView) view.findViewById(R.id.historyDirection);
            this.bookmarkIcon = (ImageButton) view.findViewById(R.id.bookmark_icon);
        }

        public void setQuery(String query) {
            historyQuery.setText(query);
        }

        public void setDirection(String direction) {
            historyDirection.setText(direction);
        }

        public void setTranslation(String translation) {
            historyTranslation.setText(translation);
        }

        public void setBookmarked(int bookmarked) {
            bookmarkIcon.setImageResource(
                    bookmarked == 1
                            ? R.drawable.ic_bookmark_yellow_24dp
                            : R.drawable.ic_bookmark_grey_24dp
            );
            bookmarkIcon.setContentDescription(
                    bookmarked == 1
                            ? mActiveIconDescription
                            : mIconDescription
            );
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
            final String description = v.getContentDescription().toString();
            if (description.equals(mActiveIconDescription)) {
                mLog.logUnBookmarked(id);
            } else {
                mLog.logBookmarked(id);
            }
        }
    }

    public HistoryAdapter withLog(EventLog log) {
        this.mLog = log;
        return this;
    }

    public EventLog getLog() {
        return mLog;
    }
}

