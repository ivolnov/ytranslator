package com.ivolnov.ytranslator.dictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.ivolnov.ytranslator.YandexApi.ABBREVIATIONS;
import static com.ivolnov.ytranslator.YandexApi.GEN_KEY;
import static com.ivolnov.ytranslator.YandexApi.HEAD_KEY;
import static com.ivolnov.ytranslator.YandexApi.MEAN_KEY;
import static com.ivolnov.ytranslator.YandexApi.POS_KEY;
import static com.ivolnov.ytranslator.YandexApi.REGULAR_KEY;
import static com.ivolnov.ytranslator.YandexApi.SYN_KEY;
import static com.ivolnov.ytranslator.YandexApi.TEXT_KEY;
import static com.ivolnov.ytranslator.YandexApi.TR_KEY;
import static com.ivolnov.ytranslator.YandexApi.TS_KEY;

/**
 * Converter from json api response content to {@link DictionaryItem} instances.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 06.04.17
 */

public class DictionaryCodec {

    /**
     * Converts a string with json response from yandex dictionary multiple lookup api
     * to a list of {@link DictionaryItem} objects.
     *
     * @param article a json string from response.
     * @return a list of {@link DictionaryItem} objects.
     * @throws JSONException in case json is badly formed or expected fields are absent.
     */
    public static List<DictionaryItem> jsonToItems(String article) throws JSONException {

        JSONObject json = new JSONObject(article);
        String key = findDefinitionsKey(json.keys());

        JSONArray definitions = json
                .getJSONObject(key)
                .getJSONArray(REGULAR_KEY);

        return definitions.length() == 0
                ? Collections.<DictionaryItem>emptyList()
                : definitionsToItems(definitions);
    }

    /**
     * Converts a {@link JSONArray} of dictionary definitions to a list of
     * {@link DictionaryItem} objects.
     *
     * @param definitions json array with dictionary definitions.
     * @return a list of {@link DictionaryItem} objects.
     * @throws JSONException in case json is badly formed or expected fields are absent.
     */
    private static List<DictionaryItem> definitionsToItems(JSONArray definitions)
            throws JSONException {

        int size = definitions.length();
        List<DictionaryItem> items = new ArrayList<>(size);

        for (int i=0; i < size; i++) {
            JSONObject definition = definitions.getJSONObject(i);
            DictionaryItem item = definitionToItem(definition);
            items.add(item);
        }

        return items;
    }

    /**
     * Converts a {@link JSONObject} with dictionary definition to a {@link DictionaryItem} object.
     *
     * @param definition json object with a dictionary definition.
     * @return a {@link DictionaryItem} instance.
     * @throws JSONException in case json is badly formed or expected fields are absent.
     */
    private static DictionaryItem definitionToItem(JSONObject definition) throws JSONException {
        DictionaryItem item = new DictionaryItem();
        DictionaryItem.Title title = definitionToTitle(definition);

        item.withTitle(title);

        JSONArray trs = definition.getJSONArray(TR_KEY);

        for (int i=0; i < trs.length(); i++) {
            JSONObject tr = trs.getJSONObject(i);
            DictionaryItem.Meaning meaning = trToMeaning(tr);
            item.withMeaning(meaning);
        }

        return item;
    }

    /**
     * Fills {@link DictionaryItem.Title} object with data from {@link JSONObject}
     * dictionary definition.
     *
     * @param definition json object with a dictionary definition.
     * @return a {@link DictionaryItem.Title} instance.
     * @throws JSONException in case json is badly formed or expected fields are absent.
     */
    private static DictionaryItem.Title definitionToTitle(JSONObject definition)
            throws JSONException {

        final DictionaryItem.Title title = new DictionaryItem.Title()
                .withText(definition
                        .getString(TEXT_KEY))
                .withTranscription("")
                .withPos(abbreviationOf(definition
                        .getJSONObject(POS_KEY)
                        .getString(TEXT_KEY)));

        if (definition.has(TS_KEY)) {
            title.withTranscription(definition.getString(TS_KEY));
        }

        return title;
    }

    /**
     * Fills {@link DictionaryItem.Meaning} object with the given {@link JSONObject}
     * from array in 'tr' property of dictionary definition.
     *
     * @param tr json object from 'tr' array in dictionary definition.
     * @return a {@link DictionaryItem.Meaning} instance.
     * @throws JSONException in case json is badly formed or expected fields are absent.
     */
    private static DictionaryItem.Meaning trToMeaning(JSONObject tr) throws JSONException {

        DictionaryItem.Meaning meaning = new DictionaryItem.Meaning();

        if (tr.has(MEAN_KEY)) {
            JSONArray means = tr.getJSONArray(MEAN_KEY);
            for (int i=0; i < means.length(); i++) {
                JSONObject mean = means.getJSONObject(i);
                DictionaryItem.Translation translation = meanToTranslation(mean);
                meaning.withTranslation(translation);
            }
        }

        meaning.withValue(synToValue(tr));

        if (tr.has(SYN_KEY)) {
            JSONArray syns = tr.getJSONArray(SYN_KEY);
            for (int i=0; i < syns.length(); i++) {
                JSONObject syn = syns.getJSONObject(i);
                DictionaryItem.Value value = synToValue(syn);
                meaning.withValue(value);
            }
        }

        return meaning;
    }

    /**
     * Fills {@link DictionaryItem.Translation} object with the given {@link JSONObject}
     * from array in 'mean' property of 'tr' property of dictionary definition.
     *
     * @param mean json object from 'mean' array in 'tr' array.
     * @return a {@link DictionaryItem.Translation} instance.
     * @throws JSONException in case json is badly formed or expected fields are absent.
     */
    private static DictionaryItem.Translation meanToTranslation(JSONObject mean)
            throws JSONException {
        return new DictionaryItem.Translation()
                .withValue(mean.getString(TEXT_KEY));
    }

    /**
     * Fills {@link DictionaryItem.Value} object with the given {@link JSONObject}
     * from array in 'syn' property of 'tr' property of dictionary definition.
     *
     * @param syn json object from 'syn' array in 'tr' array.
     * @return a {@link DictionaryItem.Value} instance.
     * @throws JSONException in case json is badly formed or expected fields are absent.
     */
    private static DictionaryItem.Value synToValue(JSONObject syn) throws JSONException {
        final JSONObject markContainer = syn.optJSONObject(GEN_KEY);
        final String mark = markContainer != null
                ? markContainer.getString(TEXT_KEY)
                : "";

        return new DictionaryItem.Value()
                .withMeaning(syn.getString(TEXT_KEY))
                .withMark(mark);
    }

    /**
     * Figures out what key is used for an array of dictionary definition.
     * It is de facto a name of the requested dictionary like 'en-ru' but the converter
     * can not know explicitly.
     */
    private static String findDefinitionsKey(Iterator<String> keys) throws JSONException {
        while (keys.hasNext()) {
            String key = keys.next();
            if (!key.equals(HEAD_KEY)) {
                return key;
            }
        }
        throw new JSONException("No dictionary key like 'en-ru' was found.");
    }

    /**
     * Figures out what key is used for an object with a mark token for the dictionary
     * meaning. It might be for example a gender for nouns or it might be event absent
     * but the converter can not know explicitly.
     *
     * Currently unused because the only mark used is gender.
     *
     */
    private static String findMarkKey(Iterator<String> keys) throws JSONException {
        while (keys.hasNext()) {
            String key = keys.next();
            if (!key.equals(TEXT_KEY) && !key.equals(POS_KEY) && !key.equals(MEAN_KEY)) {
                return key;
            }
        }
        return null;
    }

    /**
     * Gets UI abbreviation for the given dictionary api response string.
     * Like "noun" => "n"
     *
     * @param posText a text field from pos member in dictionary api json response.
     * @return an abbreviation of this string.
     */
    public static String abbreviationOf(String posText) {

        return ABBREVIATIONS.containsKey(posText)
                ? ABBREVIATIONS.get(posText)
                : defaultAbbreviation(posText);

    }

    /**
     * Abbreviates given string with the default technique.
     * Strings larger than 3 characters a trimmed on the first vowel after the second character.
     *
     * @param posText a string to abbreviate.
     * @return abbreviated string.
     */
    private static String defaultAbbreviation(String posText) {
        final String englishVowels = "aeiouAEIOU";
        int indexToTrim = -1;

        for (int i=0; i < posText.length(); i++) {
            if (i > 1 && englishVowels.contains(posText.substring(i, i + 1))) {
                indexToTrim = i;
                break;
            }
        }

        return indexToTrim != -1
                ? posText.substring(0, indexToTrim)
                : posText.length() > 3
                ? posText.substring(0, 3)
                : posText;
    }
}