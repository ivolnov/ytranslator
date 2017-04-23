package com.ivolnov.ytranslator.translator;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.ivolnov.ytranslator.dictionary.Dictionary;
import com.ivolnov.ytranslator.dictionary.DictionaryItem;
import com.ivolnov.ytranslator.languages.Languages;

import java.util.Collections;

/**
 * {@link TextWatcher} implementation for translation query {@link android.widget.EditText}.
 * Basically just notifies listeners with parameters(set in constructor) when user types a query.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 06.04.17
 */

public class TranslatorQueryWatcher implements TextWatcher {

    public static final int TYPING_TIMEOUT = 100; //milliseconds

    private String mUi;

    private TextView mTranslation;
    private Translator mTranslator;
    private Dictionary mDictionary;
    private Languages mLanguages;
    private Translator.Listener mTranslatorListener;
    private Dictionary.Listener mDictionaryListener;

    private Handler mQueryHandler = new Handler();
    private QueryExecutor mExecutor = new QueryExecutor();

    /**
     * Constructor.
     *
     * @param translation a text field to update on translation ready.
     * @param translator a translator to delegate query to.
     * @param dictionary a dictionary to delegate query to.
     * @param languages languages instance to be used for translation direction.
     * @param translatorListener a translator listener to pass to translator.
     * @param dictionaryListener a dictionary listener to pass to dictionary.
     * @param ui a locale language abbreviation.
     */
    public TranslatorQueryWatcher(
            TextView translation,
            Translator translator,
            Dictionary dictionary,
            Languages languages,
            Translator.Listener translatorListener,
            Dictionary.Listener dictionaryListener,
            String ui)
    {
        this.mTranslation = translation;
        this.mTranslator = translator;
        this.mDictionary = dictionary;
        this.mLanguages = languages;
        this.mTranslatorListener = translatorListener;
        this.mDictionaryListener = dictionaryListener;
        this.mUi = ui;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mQueryHandler.removeCallbacks(mExecutor);
        mExecutor.withQuery(s.toString());
        mQueryHandler.postDelayed(mExecutor, TYPING_TIMEOUT);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public Translator getTranslator() {
        return mTranslator;
    }

    public Dictionary getDictionary() {
        return mDictionary;
    }

    public QueryExecutor getQueryExecutor() {
        return mExecutor;
    }

    public TranslatorQueryWatcher withTranslator(Translator translator) {
        this.mTranslator = translator;
        return this;
    }

    public TranslatorQueryWatcher withDictionary(Dictionary dictionary) {
        this.mDictionary = dictionary;
        return this;
    }

    public TranslatorQueryWatcher withQueryHandler(Handler handler) {
        this.mQueryHandler = handler;
        return this;
    }

    public TranslatorQueryWatcher withQueryExecutor(QueryExecutor executor) {
        this.mExecutor = executor;
        return this;
    }

    /**
     * {@link Runnable} that delegates query to all of the interested parties.
     * Used as a callback that can be scheduled concurrently with a  delay or etc.
     */
    public class QueryExecutor implements Runnable {

        private String query;

        @Override
        public void run() {

            String direction = mLanguages.getDirection();

            if (query.length() > 0) {
                mTranslator.translate(query, direction, mTranslatorListener);
                if (lessThanFourWordsIn(query)) {
                    mDictionary.lookup(query, direction, mUi, mDictionaryListener);
                }
            } else {
                mTranslation.setText("");
                mDictionaryListener.notifyLookedUp(Collections.<DictionaryItem>emptyList());
            }
        }

        public QueryExecutor withQuery(String query) {
            this.query = query;
            return this;
        }

        private boolean lessThanFourWordsIn(CharSequence s) {
            int count = 1;
            for (int i = 1; i < s.length(); i++){
                if (s.charAt(i) == ' ' && s.charAt(i - 1) != ' ') {
                    count++;
                }
            }
            return count < 4;
        }
    }
}
