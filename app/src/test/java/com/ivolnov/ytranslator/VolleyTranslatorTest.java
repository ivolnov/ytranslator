package com.ivolnov.ytranslator;

import android.support.v4.util.Pair;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.ivolnov.ytranslator.translator.TranslatorCache;
import com.ivolnov.ytranslator.translator.Translator;
import com.ivolnov.ytranslator.translator.VolleyTranslator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link VolleyTranslator} class local unit tests.
 * {@link RobolectricTestRunner} is used because Volley requests use native logging in callbacks
 * testing.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 03.04.17
 */

@RunWith(RobolectricTestRunner.class)
public class VolleyTranslatorTest {

    private static final String KEY = "api_key";
    private static final String OPTIONS = "1";
    private static final String DIRECTION = "en-ru";
    private static final String URL = "https://example.com";
    private static final String RESPONSE = "{\"code\":200,\"lang\":\"en-ru\",\"text\":[\"время\"]}";
    private static final Pair<String, String> TRANSLATION = Pair.create("time", "время");

    @Test
    public void onTranslate_addRequestToVolley() throws Exception {
        final RequestQueue queueMock = mock(RequestQueue.class);
        final Translator.Listener listenerMock = mock(Translator.Listener.class);

        final Translator translator
                = new VolleyTranslator(queueMock, new VolleyTranslator.RequestBuilder(URL, KEY));

        translator.translate(TRANSLATION.first, null, listenerMock);

        verify(queueMock, times(1)).add(any(VolleyTranslator.TranslateRequest.class));
    }

    @Test
    public void onTranslate_whenInCache_doNotAddRequestToVolley() throws Exception {
        final RequestQueue queueMock = mock(RequestQueue.class);
        final Translator.Listener listenerMock = mock(Translator.Listener.class);
        final VolleyTranslator translator
                = new VolleyTranslator(queueMock, new VolleyTranslator.RequestBuilder(URL, KEY));
        final TranslatorCache cacheMock = mock(TranslatorCache.class);

        when(cacheMock.get(TRANSLATION.first, DIRECTION)).thenReturn(TRANSLATION.second);
        translator.withCache(cacheMock);

        translator.translate(TRANSLATION.first, DIRECTION, listenerMock);

        verify(queueMock, never()).add(any(VolleyTranslator.TranslateRequest.class));
    }

    @Test
    public void onSuccessCallback_notifyListener() throws Exception {
        final Translator.Listener listenerSpy = listenerSpy();

        final VolleyTranslator.TranslateRequest request =
                new VolleyTranslator.RequestBuilder(URL, KEY)
                        .aPost()
                        .withListener(listenerSpy)
                        .withText(TRANSLATION.first)
                        .build();

        request.deliverResponse(RESPONSE);

        verify(listenerSpy, times(1))
                .notifyTranslated(TRANSLATION.first, DIRECTION, TRANSLATION.second);
    }

    @Test
    public void onSuccessCallback_saveToCache() throws Exception {
        final TranslatorCache cache = mock(TranslatorCache.class);

        final VolleyTranslator.TranslateRequest request =
                new VolleyTranslator.RequestBuilder(URL, KEY)
                        .aPost()
                        .withCache(cache)
                        .withListener(listenerSpy())
                        .withText(TRANSLATION.first)
                        .build();

        request.deliverResponse(RESPONSE);

        verify(cache, times(1))
                .put(TRANSLATION.first,DIRECTION, TRANSLATION.second);
    }

    @Test
    public void whenTranslationRequestCreated_allFieldsAreCorrect() throws Exception {

        final VolleyTranslator.TranslateRequest request =
                new VolleyTranslator.RequestBuilder(URL, KEY)
                        .aPost()
                        .withListener(listenerSpy())
                        .withDirection(DIRECTION)
                        .withText(TRANSLATION.first)
                        .build();

        Assert.assertThat(request.getMethod(), is(equalTo(Request.Method.POST)));
        Assert.assertThat(request.getUrl(), is(equalTo(URL)));

        Assert.assertThat(request.getParams().get("key"), is(equalTo(KEY)));
        Assert.assertThat(request.getParams().get("lang"), is(equalTo(DIRECTION)));
        Assert.assertThat(request.getParams().get("options"), is(equalTo(OPTIONS)));
        Assert.assertThat(request.getParams().get("text"), is(equalTo(TRANSLATION.first)));
    }

    private Translator.Listener listenerSpy() {
        return spy(new Translator.Listener() {
            @Override
            public void notifyTranslated(String query, String direction, String translation) {

            }

            @Override
            public void notifyTranslationError(String error) {}
        });
    }
}