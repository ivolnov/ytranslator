package com.ivolnov.ytranslator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Unit tests.
 *
 * @version %I%, %G%
 * @author ivolnov
 * @since 29.03.17
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        VolleyTranslatorTest.class,
        TranslatorQueryWatcherTest.class,
        VolleyDictionaryTest.class,
        DictionaryCodecTest.class,
        DictionaryAdapterTest.class,
        VolleyLanguagesTest.class,
        LanguagesCodecTest.class,
        LanguagesUIStateTest.class,
        HistoryAdapterTest.class,
        BookmarksAdapterTest.class,
        SQLiteEventLogLoaderTest.class
})
public class LocalUnitTestSuite {

}

