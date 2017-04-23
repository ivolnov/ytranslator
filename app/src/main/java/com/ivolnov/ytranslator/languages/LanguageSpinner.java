package com.ivolnov.ytranslator.languages;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.ivolnov.ytranslator.adapters.LanguageSpinnerAdapter;

/**
 * A wrapper around {@link Spinner} to enforce usage of {@link LanguageSpinnerAdapter} adapter
 * implementation.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 13.04.17
 */

public class LanguageSpinner {

    public static final String TAG = "LanguageSpinner";

    private final Spinner mSpinner;

    private LanguageSpinner(Spinner spinner) {
        this.mSpinner = spinner;
    }

    public static LanguageSpinner from(Spinner spinner) {
        return new LanguageSpinner(spinner);
    }

    public void setAdapter(LanguageSpinnerAdapter adapter) {
        mSpinner.setAdapter(adapter);
    }

    public LanguageSpinnerAdapter getAdapter() {
        return (LanguageSpinnerAdapter) mSpinner.getAdapter();
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        mSpinner.setOnItemSelectedListener(listener);
    }

    public String getSelectedItem() {
        return (String) mSpinner.getSelectedItem();
    }

    public void setSelection(int position) {
        mSpinner.setSelection(position);
    }

    /**
     * Sets the given language as the selection of this spinner.
     * Invalid language strings are ignored.
     *
     * @param language a string with language to set.
     */
    public void setSelection(String language) {
        final LanguageSpinnerAdapter adapter = (LanguageSpinnerAdapter) mSpinner.getAdapter();
        final int id = adapter.getPosition(language);
        if (id == -1) {
            Log.d(TAG, "Language value: '" + language + "' is not valid for this spinner");
            return;
        }
        mSpinner.setSelection(id);
    }
}