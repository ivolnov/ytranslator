package com.ivolnov.ytranslator.languages;

import android.view.View;
import android.widget.AdapterView;

import com.ivolnov.ytranslator.adapters.LanguageSpinnerAdapter;

/**
 * {@link SourceLanguageSpinnerListener} implementation for the source language spinner.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 11.04.17
 */

public class SourceLanguageSpinnerListener extends LanguageSpinnerListener {

    public SourceLanguageSpinnerListener(Languages languages, LanguageSpinnerAdapter adapter) {
        super(languages, adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final String source = mAdapter.getItem(position);
        mLanguages.onSourceLanguageChanged(source);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
