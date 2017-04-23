package com.ivolnov.ytranslator;

import com.ivolnov.ytranslator.languages.Languages;
import com.ivolnov.ytranslator.languages.LanguagesCodec;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.ivolnov.ytranslator.util.AvailableLanguagesApiTestData.DATA;
import static com.ivolnov.ytranslator.util.AvailableLanguagesApiTestData.JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * {@link LanguagesCodec} class local unit tests in a black box fashion.
 * {@link RobolectricTestRunner} is used because {@link JSONObject} is a part of Android SDK.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 12.04.17
 */

@RunWith(RobolectricTestRunner.class)
public class LanguagesCodecTest {

    @Test
    public void jsonToLanguagesDataTest() throws Exception {
        Languages.Data data = LanguagesCodec.jsonToLanguagesData(JSON);
        Assert.assertThat(data, is(equalTo(DATA)));
    }
}
