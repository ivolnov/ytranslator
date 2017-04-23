package com.ivolnov.ytranslator.util;


import android.content.Context;
import android.content.res.AssetManager;
import android.support.test.InstrumentationRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Test utility class that stores data for dictionary functionality acceptance tests:
 * - request for an api query.
 * - list of expected dictionary items strings in dictionary's recycler view.
 *
 * Expected content is loaded from asset files 'item1.txt', 'item2.txt' and 'item3.txt'.
 * Although the content of this files mirrors <a href=https://translate.yandex.com">web service</a>
 * we don't have ordered list functionality in android text views thus line numbering
 * was explicitly added to the text files.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 07.04.17
 */

public class DictionaryRecyclerViewTestData {
    public static final String QUERY = "time";
    public static final List<String> DICTIONARY_ITEM_LIST = new ArrayList<>();

    static {
        DICTIONARY_ITEM_LIST.add(loadItem(1));
        DICTIONARY_ITEM_LIST.add(loadItem(2));
        DICTIONARY_ITEM_LIST.add(loadItem(3));
    }

    private static String loadItem(int index) {
        final String itemFileName = "item" + index + ".txt";

        return AssetFileReader.getStringFromFile(itemFileName);
    }

    /**
     * Utility class for reading instrumented test's assets files.
     * Inspired by:
     * @see <a href="http://stackoverflow.com/a/13357785/4003403">stackoverflow</a>
     */
    private static class AssetFileReader {

        static String convertStreamToString(InputStream is) throws IOException {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            final StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        }

        static String getStringFromFile(String filename) {
            String ret;
            final Context testContext = InstrumentationRegistry.getInstrumentation().getContext();
            final AssetManager assetManager = testContext.getAssets();

            try {
                final InputStream is = assetManager.open(filename);
                ret = convertStreamToString(is);
                is.close();
            } catch (IOException e) {
                ret = e.getMessage();
            }
            return ret;
        }
    }
}