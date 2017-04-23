package com.ivolnov.ytranslator.languages;

import android.widget.EditText;
import android.widget.TextView;

import com.ivolnov.ytranslator.adapters.LanguageSpinnerAdapter;
import com.ivolnov.ytranslator.translator.TranslationQueryBridge;

/**
 * {@link Languages.State} implementation backed by UI views like:
 * - spinners and their adapters
 * - query and translation text fields
 * - swap button
 * Acts as a bridge between {@link Languages} manager and state of UI components.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 12.04.17
 */

public class LanguagesUIState implements Languages.State, TranslationQueryBridge {

    private String mSourceSnapshot;
    private String mTargetSnapshot;

    private EditText mQuery;
    private TextView mTranslation;

    private LanguageSpinner mSourceSpinner;
    private LanguageSpinner mTargetSpinner;

    private LanguageSpinnerAdapter mSourceLanguageSpinnerAdapter;
    private LanguageSpinnerAdapter mTargetLanguageSpinnerAdapter;

    /**
     * A constructor.
     *
     * @param source source spinner to get source language value.
     * @param target target spinner to get target language value.
     * @param query a query edit text view to be updated when languages change.
     * @param translation a translation text view to be updated on language swap.
     */
    public LanguagesUIState(
            LanguageSpinner source,
            LanguageSpinner target,
            EditText query,
            TextView translation) {
        this.mQuery = query;
        this.mSourceSpinner = source;
        this.mTargetSpinner = target;
        this.mTranslation = translation;
        this.mSourceLanguageSpinnerAdapter = source.getAdapter();
        this.mTargetLanguageSpinnerAdapter = target.getAdapter();
    }

    @Override
    public void notifyLanguagesChanged(String[] languages) {
        saveSnapshot();
        mSourceLanguageSpinnerAdapter.setNotifyOnChange(false);
        mTargetLanguageSpinnerAdapter.setNotifyOnChange(false);
        mSourceLanguageSpinnerAdapter.clear();
        mTargetLanguageSpinnerAdapter.clear();
        mSourceLanguageSpinnerAdapter.addAll(languages);
        mTargetLanguageSpinnerAdapter.addAll(languages);
        mSourceLanguageSpinnerAdapter.notifyDataSetChanged();
        mTargetLanguageSpinnerAdapter.notifyDataSetChanged();
        applySnapshot();
    }

    @Override
    public void notifyDirectionChanged() {
        if (differentLanguagesPickedOnBothSides()) {
            final String query = getQuery().getText().toString();
            setQuery(query);
        }
    }

    @Override
    public void notifySwap() {
        final String translation = mTranslation.getText().toString();
        mTranslation.setText("");

        final String source = mSourceSpinner.getSelectedItem();
        final String target = mTargetSpinner.getSelectedItem();

        final int sourceId = mSourceLanguageSpinnerAdapter.getPosition(source);
        final int targetId = mTargetLanguageSpinnerAdapter.getPosition(target);

        mSourceSpinner.setSelection(targetId);
        mTargetSpinner.setSelection(sourceId);

        setQuery(translation);
    }

    @Override
    public String getSourceLanguage() {
        return mSourceSpinner.getSelectedItem();
    }

    @Override
    public String getTargetLanguage() {
        return mTargetSpinner.getSelectedItem();
    }

    @Override
    public EditText getQuery() {
        return mQuery;
    }

    @Override
    public void setQuery(String query) {
        mQuery.setText(query);
    }

    @Override
    public void startWith(String source, String target) {
        mSourceSnapshot = source;
        mTargetSnapshot = target;
    }

    private void saveSnapshot() {
        final String source = mSourceSpinner.getSelectedItem();
        final String target = mTargetSpinner.getSelectedItem();

        mSourceSnapshot = source != null ? source : mSourceSnapshot;
        mTargetSnapshot = source != null ? target : mTargetSnapshot;
    }

    private void applySnapshot() {
        mSourceSpinner.setSelection(mSourceSnapshot);
        mTargetSpinner.setSelection(mTargetSnapshot);
    }

    private boolean differentLanguagesPickedOnBothSides() {
        final String source = mSourceSpinner.getSelectedItem();
        final String target = mTargetSpinner.getSelectedItem();

        if (!source.equals(target)) {
            mSourceSnapshot = source;
            mTargetSnapshot = target;
            return true;
        }

        if (source.equals(mSourceSnapshot)) {
            mSourceSpinner.setSelection(mTargetSnapshot);
            mTargetSnapshot = target;
        } else {
            mTargetSpinner.setSelection(mSourceSnapshot);
            mTargetSnapshot = target;
        }

        return false;
    }
}
