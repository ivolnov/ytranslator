package com.ivolnov.ytranslator;

import android.content.Context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Container for all api related constants.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 04.04.17
 */

public class YandexApi {

    public static String UI;

    public static String TRANSLATE_URL;
    public static String LANGUAGES_URL;
    public static String TRANSLATE_KEY;
    public static String DICTIONARY_URL;
    public static String DICTIONARY_KEY;

    public static final String TS_KEY = "ts";
    public static final String TR_KEY = "tr";
    public static final String UI_KEY = "ui";
    public static final String POS_KEY = "pos";
    public static final String KEY_KEY = "key";
    public static final String SYN_KEY = "syn";
    public static final String GEN_KEY = "gen";
    public static final String TEXT_KEY = "text";
    public static final String MEAN_KEY = "mean";
    public static final String HEAD_KEY = "head";
    public static final String LANG_KEY = "lang";
    public static final String LANGS_KEY = "langs";
    public static final String DICT_KEY = "dict";
    public static final String REGULAR_KEY = "regular";
    public static final String OPTIONS_KEY = "options";
    public static final String OPTIONS_VALUE = "1";

    public static final Map<String,String> ABBREVIATIONS = new HashMap<>();

    static {
        ABBREVIATIONS.put("noun", "n");
        ABBREVIATIONS.put("существительное", "сущ");
        ABBREVIATIONS.put("verb", "v");
        ABBREVIATIONS.put("глагол", "гл");
        ABBREVIATIONS.put("adjective", "adj");
        ABBREVIATIONS.put("прилагательное", "прил");
        ABBREVIATIONS.put("adverb", "adv");
        ABBREVIATIONS.put("наречие", "нареч");
        ABBREVIATIONS.put("pronoun", "pron");
        ABBREVIATIONS.put("местоимение", "мест");
        ABBREVIATIONS.put("invariant", "invar");
        ABBREVIATIONS.put("неизменяемое", "неизм");
        ABBREVIATIONS.put("conjunction", "conj");
        ABBREVIATIONS.put("союз", "союз");
        ABBREVIATIONS.put("numeral", "num");
        ABBREVIATIONS.put("числительное", "числ");
        ABBREVIATIONS.put("preposition", "prep");
        ABBREVIATIONS.put("предлог", "предл");
        ABBREVIATIONS.put("adverbial participle", "adv part");
        ABBREVIATIONS.put("деепричастие", "дееприч");
        ABBREVIATIONS.put("participle", "part");
        ABBREVIATIONS.put("причастие", "прич");
        ABBREVIATIONS.put("interjection", "interj");
        ABBREVIATIONS.put("междометие", "межд");
        ABBREVIATIONS.put("predicative", "pred");
        ABBREVIATIONS.put("предикатив", "предик");
    }

    public static void init(Context context) {
        TRANSLATE_URL = context.getResources().getString(R.string.yandex_translate_api_url);
        LANGUAGES_URL = context.getResources().getString(R.string.yandex_get_languages_api_url);
        TRANSLATE_KEY = context.getResources().getString(R.string.yandex_translate_api_key);
        DICTIONARY_URL = context.getResources().getString(R.string.yandex_dictionary_api_url);
        DICTIONARY_KEY = context.getResources().getString(R.string.yandex_dictionary_api_key);
        UI = Locale.getDefault().getLanguage();
    }
}
