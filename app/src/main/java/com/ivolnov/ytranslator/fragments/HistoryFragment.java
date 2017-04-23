package com.ivolnov.ytranslator.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivolnov.ytranslator.R;
import com.ivolnov.ytranslator.adapters.HistoryAdapter;

/**
 * A {@link Fragment} representing page or tab with the translations' history.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 31.03.17
 */

public class HistoryFragment extends Fragment {

    private HistoryAdapter mHistoryAdapter;
    private RecyclerView mHistoryList;


    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setAdaptersProperties();
        setViewProperties();
        attachAdapters();
    }

    private void setViewProperties() {
        mHistoryList = (RecyclerView) getActivity().findViewById(R.id.history);
    }

    private void setAdaptersProperties() {
        final FragmentsPropertiesProvider provider = (FragmentsPropertiesProvider) getActivity();
        mHistoryAdapter = provider.getHistoryAdapter();
    }

    private void attachAdapters() {
        mHistoryList.setAdapter(mHistoryAdapter);
    }
}

