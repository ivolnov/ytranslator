package com.ivolnov.ytranslator.languages;

import android.view.View;

/**
 * Listener for a button that is responsible for the swap of a target and a source language.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 11.04.17
 */

public class SwapLanguageButtonListener implements View.OnClickListener {

    private Languages mLanguages;

    public SwapLanguageButtonListener(Languages mLanguages) {
        this.mLanguages = mLanguages;
    }

    @Override
    public void onClick(View v) {
        mLanguages.onLanguagesSwapped();
    }

    public Languages getLanguages() {
        return mLanguages;
    }

    public SwapLanguageButtonListener withLanguages(Languages mLanguages) {
        this.mLanguages = mLanguages;
        return this;
    }
}
