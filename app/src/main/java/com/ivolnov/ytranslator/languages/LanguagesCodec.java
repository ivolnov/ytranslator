package com.ivolnov.ytranslator.languages;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.ivolnov.ytranslator.YandexApi.LANGS_KEY;

/**
 * Converter from json api response content to {@link Languages.Data} instances.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 12.04.17
 */

public class LanguagesCodec {
    /**
     * Converts a string with json response from yandex translator api
     * on available languages request to a  {@link Languages.Data} objects.
     *
     * @param languages a json string from response.
     * @return a  {@link Languages.Data} instance.
     * @throws JSONException in case json is badly formed or expected fields are absent.
     */
    public static Languages.Data jsonToLanguagesData(String languages) throws JSONException {

        final JSONObject json = new JSONObject(languages);
        final JSONObject langs = json.getJSONObject(LANGS_KEY);
        final Map<String, String> tagIndex = new HashMap<>(langs.length());
        final Iterator<String> keys = langs.keys();

        while(keys.hasNext()) {
            final String tag = keys.next();
            final String language = langs.getString(tag);
            tagIndex.put(language, tag);
        }

        final String[] languagesArray = tagIndex.keySet().toArray(new String[tagIndex.size()]);
        Arrays.sort(languagesArray);

        return new Languages.Data(){
            @Override
            public String[] getSortedLanguages() {
                return languagesArray;
            }

            @Override
            public Map<String, String> getTagIndex() {
                return tagIndex;
            }
        };
    }
}
