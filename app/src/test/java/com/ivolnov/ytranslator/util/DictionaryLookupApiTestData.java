package com.ivolnov.ytranslator.util;


import com.ivolnov.ytranslator.dictionary.DictionaryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Test utility class that stores data for dictionary lookup api related tests:
 * - request text for api query.
 * - json response payload stored locally in 'lookupMultiple.json' text file.
 * - json response with empty payload.
 * - expected list of {@link DictionaryItem} instances derived from this json file.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 07.04.17
 */

public class DictionaryLookupApiTestData {
    public static final String QUERY = "time";

    public static final String JSON_FULL = loadJsonFromFile();

    public static final String JSON_EMPTY = "{\"head\":{},\"en-ru\":{\"regular\":[]}}";

    public static final List<DictionaryItem> DICTIONARY_ITEM_LIST = new ArrayList<>();

    public static final DictionaryItem FIRST_ITEM = new DictionaryItem()
            .withTitle(
                    new DictionaryItem.Title()
                            .withText("time")
                            .withTranscription("taɪm")
                            .withPos("сущ"))
            .withMeaning(
                    new DictionaryItem.Meaning()
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("время")
                                            .withMark("ср")
                            )
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("раз")
                                            .withMark("м")
                            )
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("момент")
                                            .withMark("м")
                            )
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("срок")
                                            .withMark("м")
                            )
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("пора")
                                            .withMark("")
                            )
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("период")
                                            .withMark("м")
                            )
                            .withTranslation(
                                    new DictionaryItem.Translation()
                                            .withValue("period")
                            )
                            .withTranslation(
                                    new DictionaryItem.Translation()
                                            .withValue("once")
                            )
                            .withTranslation(
                                    new DictionaryItem.Translation()
                                            .withValue("moment")
                            )
                            .withTranslation(
                                    new DictionaryItem.Translation()
                                            .withValue("pore")
                            )
            )
            .withMeaning(
                    new DictionaryItem.Meaning()
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("час")
                                            .withMark("м")
                            )
                            .withTranslation(
                                    new DictionaryItem.Translation()
                                            .withValue("hour")
                            )
            )
            .withMeaning(
                    new DictionaryItem.Meaning()
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("эпоха")
                                            .withMark("ж")
                            )
                            .withTranslation(
                                    new DictionaryItem.Translation()
                                            .withValue("era")
                            )
            )
            .withMeaning(
                    new DictionaryItem.Meaning()
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("век")
                                            .withMark("м")
                            )
                            .withTranslation(
                                    new DictionaryItem.Translation()
                                            .withValue("age")
                            )
            )
            .withMeaning(
                    new DictionaryItem.Meaning()
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("такт")
                                            .withMark("м")
                            )
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("темп")
                                            .withMark("м")
                            )
                            .withTranslation(
                                    new DictionaryItem.Translation()
                                            .withValue("cycle")
                            )
                            .withTranslation(
                                    new DictionaryItem.Translation()
                                            .withValue("rate")
                            )
            )
            .withMeaning(
                    new DictionaryItem.Meaning()
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("жизнь")
                                            .withMark("ж")
                            )
                            .withTranslation(
                                    new DictionaryItem.Translation()
                                            .withValue("life")
                            )
            );

    public static final DictionaryItem SECOND_ITEM = new DictionaryItem()
            .withTitle(
                    new DictionaryItem.Title()
                            .withText("time")
                            .withTranscription("taɪm")
                            .withPos("гл"))
            .withMeaning(
                    new DictionaryItem.Meaning()
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("приурочивать")
                                            //.withMark("несов")
                                            .withMark("")
                            )
            )
            .withMeaning(
                    new DictionaryItem.Meaning()
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("рассчитывать")
                                            //.withMark("несов")
                                            .withMark("")
                            )
                            .withTranslation(
                                    new DictionaryItem.Translation()
                                            .withValue("count")
                            )
            );

    public static final DictionaryItem THIRD_ITEM = new DictionaryItem()
            .withTitle(
                    new DictionaryItem.Title()
                            .withText("time")
                            .withTranscription("taɪm")
                            .withPos("прил"))
            .withMeaning(
                    new DictionaryItem.Meaning()
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("временный")
                                            .withMark("")
                            )
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("временной")
                                            .withMark("")
                            )
                            .withTranslation(
                                    new DictionaryItem.Translation()
                                            .withValue("temporary")
                            )
            )
            .withMeaning(
                    new DictionaryItem.Meaning()
                            .withValue(
                                    new DictionaryItem.Value()
                                            .withMeaning("повременный")
                                            .withMark("")
                            )
            );

    static {
        DICTIONARY_ITEM_LIST.add(FIRST_ITEM);
        DICTIONARY_ITEM_LIST.add(SECOND_ITEM);
        DICTIONARY_ITEM_LIST.add(THIRD_ITEM);
    }

    private static String loadJsonFromFile() {
        final String jsonFileName = "lookupMultiple.json";
        return JsonFileReader.getStringFromFile(jsonFileName);
    }
}