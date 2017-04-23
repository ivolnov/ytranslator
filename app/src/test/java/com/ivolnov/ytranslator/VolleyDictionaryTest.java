package com.ivolnov.ytranslator;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.ivolnov.ytranslator.dictionary.Dictionary;
import com.ivolnov.ytranslator.dictionary.DictionaryCache;
import com.ivolnov.ytranslator.dictionary.DictionaryItem;
import com.ivolnov.ytranslator.dictionary.VolleyDictionary;
import com.ivolnov.ytranslator.util.DictionaryLookupApiTestData;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ivolnov.ytranslator.R.layout.translator;
import static com.ivolnov.ytranslator.util.DictionaryLookupApiTestData.DICTIONARY_ITEM_LIST;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link VolleyDictionary} class local unit tests.
 * {@link RobolectricTestRunner} is used because Volley requests use native logging in callbacks
 * testing.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 06.04.17
 */

@RunWith(RobolectricTestRunner.class)
public class VolleyDictionaryTest {

    private static final String UI = "ru";
    private static final String KEY = "api_key";
    private static final String DICT = "en-ru.regular";
    private static final String QUERY = "tic tack toe";
    private static final String OPTIONS = "1";
    private static final String DIRECTION = "en-ru";
    private static final String URL = "https://example.com";

    @Test
    public void onLookup_addRequestToVolley() throws Exception {
        final RequestQueue queueMock = mock(RequestQueue.class);
        final Dictionary.Listener listenerMock = mock(Dictionary.Listener.class);

        final Dictionary dictionary
                = new VolleyDictionary(queueMock, new VolleyDictionary.RequestBuilder(URL, KEY));

        dictionary.lookup(QUERY, DIRECTION, UI, listenerMock);

        verify(queueMock, times(1)).add(any(VolleyDictionary.LookupRequest.class));
    }

    @Test
    public void onLookup_whenInCache_doNotAddRequestToVolley() throws Exception {
        final DictionaryCache.Key key = DictionaryCache.Key.from(QUERY, DIRECTION, UI);
        final RequestQueue queueMock = mock(RequestQueue.class);
        final Dictionary.Listener listenerMock = mock(Dictionary.Listener.class);
        final VolleyDictionary dictionary
                = new VolleyDictionary(queueMock, new VolleyDictionary.RequestBuilder(URL, KEY));
        final DictionaryCache cacheMock = mock(DictionaryCache.class);

        when(cacheMock.get(key)).thenReturn(Collections.<DictionaryItem>emptyList());
        dictionary.withCache(cacheMock);

        dictionary.lookup(QUERY, DIRECTION, UI, listenerMock);

        verify(queueMock, never()).add(any(VolleyDictionary.LookupRequest.class));
    }

    @Test
    public void onSuccessCallback_notifyListener() throws Exception {
        final Dictionary.Listener listenerSpy = listenerSpy();

        final VolleyDictionary.LookupRequest request =
                new VolleyDictionary.RequestBuilder(URL, KEY)
                        .aPost()
                        .withListener(listenerSpy)
                        .build();

        request.deliverResponse(DictionaryLookupApiTestData.JSON_EMPTY);

        verify(listenerSpy, times(1))
                .notifyLookedUp(ArgumentMatchers.<List<DictionaryItem>>any());
    }

    @Test
    public void onSuccessCallback_saveToCache() throws Exception {
        final DictionaryCache.Key key = DictionaryCache.Key.from(QUERY, DIRECTION, UI);
        final DictionaryCache cache = mock(DictionaryCache.class);

        final VolleyDictionary.LookupRequest request =
                new VolleyDictionary.RequestBuilder(URL, KEY)
                        .aPost()
                        .withDirection(DIRECTION)
                        .withText(QUERY)
                        .withUi(UI)
                        .withCache(cache)
                        .withListener(listenerSpy())
                        .build();

        request.deliverResponse(DictionaryLookupApiTestData.JSON_EMPTY);

        verify(cache, times(1)).put(key, Collections.<DictionaryItem>emptyList());
    }

    @Test
    public void onErrorCallback_notifyListenerWithEmptyResult() throws Exception {
        final Dictionary.Listener listenerSpy = listenerSpy();

        final VolleyDictionary.LookupRequest request =
                new VolleyDictionary.RequestBuilder(URL, KEY)
                        .aPost()
                        .withListener(listenerSpy)
                        .build();

        request.deliverError(new VolleyError());

        verify(listenerSpy, times(1))
                .notifyLookedUp(Collections.<DictionaryItem>emptyList());

    }

    @Test
    public void whenTranslationRequestCreated_allFieldsAreCorrect() throws Exception {

        final VolleyDictionary.LookupRequest request =
                new VolleyDictionary.RequestBuilder(URL, KEY)
                        .aPost()
                        .withListener(listenerSpy())
                        .withDirection(DIRECTION)
                        .withText(QUERY)
                        .withUi(UI)
                        .build();

        Assert.assertThat(request.getMethod(), is(equalTo(Request.Method.POST)));
        Assert.assertThat(request.getUrl(), is(equalTo(URL)));


        Assert.assertThat(request.getParams().get("ui"), is(equalTo(UI)));
        Assert.assertThat(request.getParams().get("key"), is(equalTo(KEY)));
        Assert.assertThat(request.getParams().get("dict"), is(equalTo(DICT)));
        Assert.assertThat(request.getParams().get("text"), is(equalTo(QUERY)));
        Assert.assertThat(request.getParams().get("lang"), is(equalTo(DIRECTION)));
        Assert.assertThat(request.getParams().get("options"), is(equalTo(OPTIONS)));
    }

    private Dictionary.Listener listenerSpy() {
        return spy(new Dictionary.Listener() {
            @Override
            public void notifyLookedUp(List<DictionaryItem> items) {

            }

            @Override
            public void notifyLookupError(String error) {}
        });
    }
}
