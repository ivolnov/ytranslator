package com.ivolnov.ytranslator.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ivolnov.ytranslator.R;
import com.ivolnov.ytranslator.activities.MainActivity;
import com.ivolnov.ytranslator.adapters.DictionaryAdapter;
import com.ivolnov.ytranslator.adapters.LanguageSpinnerAdapter;
import com.ivolnov.ytranslator.dictionary.Dictionary;
import com.ivolnov.ytranslator.dictionary.DictionaryItemCompiler;
import com.ivolnov.ytranslator.languages.LanguageSpinner;
import com.ivolnov.ytranslator.languages.LanguageSpinnerListener;
import com.ivolnov.ytranslator.languages.LanguagesUIState;
import com.ivolnov.ytranslator.languages.Languages;
import com.ivolnov.ytranslator.languages.SourceLanguageSpinnerListener;
import com.ivolnov.ytranslator.languages.SwapLanguageButtonListener;
import com.ivolnov.ytranslator.languages.TargetLanguageSpinnerListener;
import com.ivolnov.ytranslator.translator.Translator;
import com.ivolnov.ytranslator.translator.TranslatorQueryWatcher;

/**
 * A {@link Fragment} representing page or tab with the mTranslator.
 * Implements {@link Translator.Listener} to be aware of translation results.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 31.03.17
 */
public class TranslatorFragment extends Fragment implements Translator.Listener {

    public static final String QUERY_STATE_KEY = "last_query";

    private Translator mTranslator;
    private Dictionary mDictionary;
    private Languages mLanguages;

    private EditText mQueryField;
    private TextView mTranslation;
    private RecyclerView mDictionaryList;
    private LanguageSpinner mSourceSpinner;
    private LanguageSpinner mTargetSpinner;
    private Button mSwapButton;

    private TextWatcher mTextWatcher;
    private LanguageSpinnerListener mSourceLanguageSpinnerListener;
    private LanguageSpinnerListener mTargetLanguageSpinnerListener;
    private SwapLanguageButtonListener mSwapLanguageButtonListener;

    private DictionaryAdapter mDictionaryAdapter;
    private LanguageSpinnerAdapter mSourceLanguageSpinnerAdapter;
    private LanguageSpinnerAdapter mTargetLanguageSpinnerAdapter;

    /**
     * Factory method.
     *
     * @return new instance of {@link TranslatorFragment}
     */
    public static TranslatorFragment newInstance() {
        return new TranslatorFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.translator, container, false);

        setDomainProperties();
        setAdaptersProperties();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewsProperties();
        setListenersProperties();
        attachAdapters();
        attachListeners();
        attachLanguagesState();
        attachQueryState();
    }

    @Override
    public void onStart() {
        super.onStart();
        attachListeners();
    }

    @Override
    public void onStop() {

        removeListeners();
        stopNetworking();
        saveLanguageSpinnersValues();
        saveQueryText();

        super.onStop();
    }

    @Override
    public void notifyTranslated(String query, String direction, String translation) {
        mTranslation.setText(translation);
        final MainActivity activity = (MainActivity) getActivity();
        activity
                .getEventLog()
                .logTranslation(query, translation, direction);
    }

    @Override
    public void notifyTranslationError(String error) {

    }

    public Translator getTranslator() {
        return mTranslator;
    }

    public Dictionary getDictionary() {
        return mDictionary;
    }

    public Languages getLanguages() {
        return mLanguages;
    }

    public DictionaryAdapter getDictionaryAdapter() {
        return mDictionaryAdapter;
    }

    public EditText getQueryField() {
        return mQueryField;
    }

    public TextView getTranslation() {
        return mTranslation;
    }

    public LanguageSpinner getSourceSpinner() {
        return mSourceSpinner;
    }

    public LanguageSpinner getTargetSpinner() {
        return mTargetSpinner;
    }

    public Button getSwapButton() {
        return mSwapButton;
    }

    public TextWatcher getTextWatcher() {
        return mTextWatcher;
    }

    public LanguageSpinnerListener getSourceLanguageSpinnerListener() {
        return mSourceLanguageSpinnerListener;
    }

    public LanguageSpinnerListener getTargetLanguageSpinnerListener() {
        return mTargetLanguageSpinnerListener;
    }

    public SwapLanguageButtonListener getSwapLanguageButtonListener() {
        return mSwapLanguageButtonListener;
    }

    public TranslatorFragment withTranslator(Translator translator) {
        mTranslator = translator;
        return this;
    }

    public TranslatorFragment withDictionary(Dictionary dictionary) {
        this.mDictionary = dictionary;
        return this;
    }

    public TranslatorFragment withLanguages(Languages languages) {
        this.mLanguages = languages;
        return this;
    }

    public TranslatorFragment withTranslation(TextView translation) {
        this.mTranslation = translation;
        return this;
    }

    public TranslatorFragment withSourceSpinner(LanguageSpinner sourceSpinner) {
        this.mSourceSpinner = sourceSpinner;
        return this;
    }

    public TranslatorFragment withTargetSpinner(LanguageSpinner targetSpinner) {
        this.mTargetSpinner = targetSpinner;
        return this;
    }

    public TranslatorFragment withSwapButton(Button swapButton) {
        this.mSwapButton = swapButton;
        return this;
    }

    public TranslatorFragment withQueryField(EditText queryField) {
        mQueryField = queryField;
        return this;
    }

    private void setDomainProperties() {
        final FragmentsPropertiesProvider provider = (FragmentsPropertiesProvider) getActivity();
        mTranslator = provider.getTranslator();
        mDictionary = provider.getDictionary();
        mLanguages = provider.getLanguages();
    }

    private void setAdaptersProperties() {
        mDictionaryAdapter = new DictionaryAdapter(new DictionaryItemCompiler(getContext()));
        mSourceLanguageSpinnerAdapter
                = new LanguageSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item);
        mSourceLanguageSpinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTargetLanguageSpinnerAdapter
                = new LanguageSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item);
        mTargetLanguageSpinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void setListenersProperties() {
        mTextWatcher = newTextWatcher();
        mSourceLanguageSpinnerListener = new SourceLanguageSpinnerListener(
                mLanguages,
                mSourceLanguageSpinnerAdapter);
        mTargetLanguageSpinnerListener = new TargetLanguageSpinnerListener(
                mLanguages,
                mTargetLanguageSpinnerAdapter);
        mSwapLanguageButtonListener = new SwapLanguageButtonListener(mLanguages);
    }

    private void setViewsProperties() {
        mQueryField = (EditText) getActivity().findViewById(R.id.query);
        mTranslation = (TextView) getActivity().findViewById(R.id.translation);
        mDictionaryList = (RecyclerView) getActivity().findViewById(R.id.dictionary);
        mSwapButton = (Button) getActivity().findViewById(R.id.swapLanguage);
        mSourceSpinner
                = LanguageSpinner.from((Spinner) getActivity().findViewById(R.id.sourceSpinner));
        mTargetSpinner
                = LanguageSpinner.from((Spinner) getActivity().findViewById(R.id.targetSpinner));
    }

    private void attachAdapters() {
        mDictionaryList.setAdapter(mDictionaryAdapter);
        mSourceSpinner.setAdapter(mSourceLanguageSpinnerAdapter);
        mTargetSpinner.setAdapter(mTargetLanguageSpinnerAdapter);
    }

    private void attachListeners() {
        mQueryField.addTextChangedListener(mTextWatcher);
        mSourceSpinner.setOnItemSelectedListener(mSourceLanguageSpinnerListener);
        mTargetSpinner.setOnItemSelectedListener(mTargetLanguageSpinnerListener);
        mSwapButton.setOnClickListener(mSwapLanguageButtonListener);
    }

    private void attachLanguagesState() {
        mLanguages.attachState(buildLanguageState());
        mLanguages.loadAvailableLanguages((Languages.Listener) mLanguages);
    }

    private void attachQueryState() {
        final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        final String lastQuery = preferences.getString(QUERY_STATE_KEY, null);
        mQueryField.setText(lastQuery);
    }

    private void removeListeners() {
        if (mQueryField != null) {
            mQueryField.removeTextChangedListener(mTextWatcher);
        }
        if (mSourceSpinner != null) {
            mSourceSpinner.setOnItemSelectedListener(null);
        }
        if (mTargetSpinner != null) {
            mTargetSpinner.setOnItemSelectedListener(null);
        }
        if (mSwapButton != null) {
            mSwapButton.setOnClickListener(null);
        }
    }

    private void stopNetworking() {
        mTranslator.stopPending();
        mDictionary.stopPending();
    }

    private Languages.State buildLanguageState() {
        final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        final String source = preferences.getString(Languages.SOURCE_LANGUAGE_PREFERENCE_KEY, null);
        final String target = preferences.getString(Languages.TARGET_LANGUAGE_PREFERENCE_KEY, null);

        final Languages.State state = new LanguagesUIState(
                mSourceSpinner,
                mTargetSpinner,
                mQueryField,
                mTranslation);

        state.startWith(source, target);

        return state;
    }

    private void saveLanguageSpinnersValues() {
        final String source = mSourceSpinner.getSelectedItem();
        final String target = mTargetSpinner.getSelectedItem();

        getActivity()
                .getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putString(Languages.SOURCE_LANGUAGE_PREFERENCE_KEY, source)
                .putString(Languages.TARGET_LANGUAGE_PREFERENCE_KEY, target)
                .apply();
    }

    private void saveQueryText() {
        final String query = mQueryField.getText().toString();
        getActivity()
                .getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putString(QUERY_STATE_KEY, query)
                .apply();
    }

    private TextWatcher newTextWatcher() {
        return new TranslatorQueryWatcher(
                mTranslation,
                mTranslator,
                mDictionary,
                mLanguages,
                this,
                mDictionaryAdapter);
    }
}
