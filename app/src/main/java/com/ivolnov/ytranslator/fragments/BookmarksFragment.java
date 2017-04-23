package com.ivolnov.ytranslator.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivolnov.ytranslator.R;
import com.ivolnov.ytranslator.adapters.BookmarksAdapter;

/**
 * A {@link Fragment} representing page or tab with bookmarked translations.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 31.03.17
 */
public class BookmarksFragment extends Fragment {

    private BookmarksAdapter mBookmarksAdapter;
    private RecyclerView mBookmarksList;


    public static BookmarksFragment newInstance() {
        return new BookmarksFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bookmarks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setAdaptersProperties();
        setViewProperties();
        attachAdapters();
    }

    private void setViewProperties() {
        mBookmarksList = (RecyclerView) getActivity().findViewById(R.id.bookmarks);
    }

    private void setAdaptersProperties() {
        final FragmentsPropertiesProvider provider = (FragmentsPropertiesProvider) getActivity();
        mBookmarksAdapter = provider.getBookmarksAdapter();
    }

    private void attachAdapters() {
        mBookmarksList.setAdapter(mBookmarksAdapter);
    }
}