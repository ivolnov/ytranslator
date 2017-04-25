package com.ivolnov.ytranslator;

import com.ivolnov.ytranslator.db.jobs.BookmarkRecordJob;
import com.ivolnov.ytranslator.db.jobs.ForceLoadJob;
import com.ivolnov.ytranslator.db.jobs.InsertRecordJob;

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
        InsertRecordJobTest.class,
        BookmarkRecordJobTest.class,
        UnBookmarkRecordJobTest.class,
        ForceLoadJobTest.class,
        SQLiteEventLogLoaderTest.class
})
public class LocalUnitTestSuite {

}

