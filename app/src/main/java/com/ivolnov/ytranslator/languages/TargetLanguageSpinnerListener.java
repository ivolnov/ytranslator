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

public class TargetLanguageSpinnerListener extends LanguageSpinnerListener {

    public TargetLanguageSpinnerListener(Languages languages, LanguageSpinnerAdapter adapter) {
        super(languages, adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final String target = mAdapter.getItem(position);
        mLanguages.onTargetLanguageChanged(target);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}