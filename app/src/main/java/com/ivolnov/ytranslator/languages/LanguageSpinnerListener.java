package com.ivolnov.ytranslator.languages;

import android.widget.AdapterView;

import com.ivolnov.ytranslator.adapters.LanguageSpinnerAdapter;

/**
 * Base class for language spinner {@link AdapterView.OnItemSelectedListener} implementations.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 11.04.17
 */

public abstract class LanguageSpinnerListener implements AdapterView.OnItemSelectedListener {

    protected Languages mLanguages;
    protected LanguageSpinnerAdapter mAdapter;

    protected LanguageSpinnerListener(Languages languages, LanguageSpinnerAdapter adapter) {
        mLanguages = languages;
        mAdapter = adapter;
    }

    public Languages getLanguages() {
        return mLanguages;
    }

    public LanguageSpinnerListener withLanguages(Languages mLanguages) {
        this.mLanguages = mLanguages;
        return this;
    }

    public LanguageSpinnerAdapter getAdapter() {
        return mAdapter;
    }

    public LanguageSpinnerListener withAdapter(LanguageSpinnerAdapter mAdapter) {
        this.mAdapter = mAdapter;
        return this;
    }
}