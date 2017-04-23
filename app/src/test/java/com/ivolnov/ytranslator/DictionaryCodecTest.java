package com.ivolnov.ytranslator;

import com.ivolnov.ytranslator.dictionary.DictionaryCodec;
import com.ivolnov.ytranslator.dictionary.DictionaryItem;
import com.ivolnov.ytranslator.util.DictionaryLookupApiTestData;

import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.collection.IsEmptyCollection;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;


/**
 * {@link DictionaryCodec} class local unit tests in a black box fashion.
 * {@link RobolectricTestRunner} is used because {@link JSONObject} is a part of Android SDK.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 07.04.17
 */

@RunWith(RobolectricTestRunner.class)
public class DictionaryCodecTest {

    @Test
    public void jsonStringToItemsTest() throws Exception {
        List<DictionaryItem> items = DictionaryCodec.jsonToItems(DictionaryLookupApiTestData.JSON_FULL);
        Assert.assertThat(items, is(equalTo(DictionaryLookupApiTestData.DICTIONARY_ITEM_LIST)));
    }

    @Test
    public void jsonStringToItemsTest_withEmptyDictionary() throws Exception {
        List<DictionaryItem> items = DictionaryCodec.jsonToItems(DictionaryLookupApiTestData.JSON_EMPTY);
        Assert.assertThat(items, isEmpty());
    }

    @Test
    public void abbreviationOfTest() throws Exception {
        Assert.assertThat(DictionaryCodec.abbreviationOf("существительное"), is(equalTo("сущ")));
        Assert.assertThat(DictionaryCodec.abbreviationOf("conjunction"), is(equalTo("conj")));
        Assert.assertThat(DictionaryCodec.abbreviationOf("apple"), is(equalTo("appl")));
        Assert.assertThat(DictionaryCodec.abbreviationOf("cat"), is(equalTo("cat")));
    }

    private TypeSafeMatcher<Collection<? extends DictionaryItem>> isEmpty() {
        return new IsEmptyCollection<>();
    }
}