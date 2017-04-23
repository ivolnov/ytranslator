package com.ivolnov.ytranslator;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.ivolnov.ytranslator.languages.Languages;
import com.ivolnov.ytranslator.languages.LanguagesSwapSemaphore;
import com.ivolnov.ytranslator.languages.VolleyLanguages;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import static com.ivolnov.ytranslator.util.AvailableLanguagesApiTestData.DIRECTION;
import static com.ivolnov.ytranslator.util.AvailableLanguagesApiTestData.JSON;
import static com.ivolnov.ytranslator.util.AvailableLanguagesApiTestData.KEY;
import static com.ivolnov.ytranslator.util.AvailableLanguagesApiTestData.SMALL_ARRAY;
import static com.ivolnov.ytranslator.util.AvailableLanguagesApiTestData.SMALL_DATA;
import static com.ivolnov.ytranslator.util.AvailableLanguagesApiTestData.SOURCE_LANGUAGE;
import static com.ivolnov.ytranslator.util.AvailableLanguagesApiTestData.TARGET_LANGUAGE;
import static com.ivolnov.ytranslator.util.AvailableLanguagesApiTestData.UI;
import static com.ivolnov.ytranslator.util.AvailableLanguagesApiTestData.URL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link VolleyLanguages} class local unit tests.
 * {@link RobolectricTestRunner} is used because Volley requests use native logging in callbacks
 * testing.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 11.04.17
 */
@RunWith(RobolectricTestRunner.class)
public class VolleyLanguagesTest {

    @Test
    public void getDirectionTest() throws Exception {
        final VolleyLanguages languages = spy(languages());
        final Languages.State state = mock(Languages.State.class);
        when(state.getSourceLanguage()).thenReturn(SOURCE_LANGUAGE);
        when(state.getTargetLanguage()).thenReturn(TARGET_LANGUAGE);
        languages.attachState(state);

        final String direction = languages.getDirection();

        Assert.assertThat(direction, is(equalTo(DIRECTION)));
    }

    @Test
    public void onAttachState_notifyState() throws Exception {
        final VolleyLanguages languages = spy(languages());
        final Languages.State state = mock(Languages.State.class);
        final ArgumentCaptor<String[]> argument = ArgumentCaptor.forClass(String[].class);

        languages.withData(SMALL_DATA);

        languages.attachState(state);

        verify(state, times(1)).notifyLanguagesChanged(argument.capture());
        Assert.assertThat(argument.getValue(), is(equalTo(SMALL_ARRAY)));
    }

    @Test
    public void onOnSourceLanguageChanged_notifyState() throws Exception {
        final VolleyLanguages languages = spy(languages());
        final Languages.State state = mock(Languages.State.class);
        languages.attachState(state);

        languages.onSourceLanguageChanged(SOURCE_LANGUAGE);

        verify(state, times(1)).notifyDirectionChanged();
    }

    @Test
    public void onOnSourceLanguageChanged_lowerSwapSemaphore() throws Exception {
        final VolleyLanguages languages = spy(languages());
        final LanguagesSwapSemaphore semaphore = mock(LanguagesSwapSemaphore.class);
        final Languages.State state = mock(Languages.State.class);
        languages.withSemaphore(semaphore);
        languages.attachState(state);

        languages.onSourceLanguageChanged(SOURCE_LANGUAGE);

        verify(semaphore, times(1)).p();
    }

    @Test
    public void onOnSourceLanguageChanged_andSemaphoreUp_doNotNotifyState() throws Exception {
        final VolleyLanguages languages = spy(languages());
        final LanguagesSwapSemaphore semaphore = new LanguagesSwapSemaphore();
        final Languages.State state = mock(Languages.State.class);
        languages.withSemaphore(semaphore);
        languages.attachState(state);

        semaphore.v();

        languages.onSourceLanguageChanged(SOURCE_LANGUAGE);

        verify(state, never()).notifyDirectionChanged();
    }

    @Test
    public void onOnTargetLanguageChanged_notifyState() throws Exception {
        final VolleyLanguages languages = spy(languages());
        final Languages.State state = mock(Languages.State.class);
        languages.attachState(state);

        languages.onSourceLanguageChanged(TARGET_LANGUAGE);

        verify(state, times(1)).notifyDirectionChanged();
    }

    @Test
    public void onOnTargetLanguageChanged_lowSwapSemaphore() throws Exception {
        final VolleyLanguages languages = spy(languages());
        final LanguagesSwapSemaphore semaphore = mock(LanguagesSwapSemaphore.class);
        final Languages.State state = mock(Languages.State.class);
        languages.withSemaphore(semaphore);
        languages.attachState(state);

        languages.onTargetLanguageChanged(SOURCE_LANGUAGE);

        verify(semaphore, times(1)).p();
    }

    @Test
    public void onOnTargetLanguageChanged_andSemaphoreUp_doNotNotifyState() throws Exception {
        final VolleyLanguages languages = spy(languages());
        final LanguagesSwapSemaphore semaphore = new LanguagesSwapSemaphore();
        final Languages.State state = mock(Languages.State.class);
        languages.withSemaphore(semaphore);
        languages.attachState(state);

        semaphore.v();

        languages.onTargetLanguageChanged(SOURCE_LANGUAGE);

        verify(state, never()).notifyDirectionChanged();
    }

    @Test
    public void onLanguagesSwapped_notifyState() {
        final VolleyLanguages languages = spy(languages());
        final Languages.State state = mock(Languages.State.class);
        languages.attachState(state);

        languages.onLanguagesSwapped();

        verify(state, times(1)).notifySwap();
    }

    @Test
    public void onLanguagesSwapped_raiseSwapSemaphore() {
        final VolleyLanguages languages = spy(languages());
        final LanguagesSwapSemaphore semaphore = mock(LanguagesSwapSemaphore.class);
        final Languages.State state = mock(Languages.State.class);
        languages.withSemaphore(semaphore);
        languages.attachState(state);

        languages.onLanguagesSwapped();

        verify(semaphore, times(1)).v();
    }

    @Test
    public void onNotifyAvailableLanguages_notifyState(){
        final VolleyLanguages languages = languages();
        final Languages.State state = mock(Languages.State.class);
        final ArgumentCaptor<String[]> argument = ArgumentCaptor.forClass(String[].class);

        languages.withState(state);

        languages.notifyAvailableLanguages(SMALL_DATA);

        verify(state, times(1)).notifyLanguagesChanged(argument.capture());
        Assert.assertThat(argument.getValue(), is(equalTo(SMALL_ARRAY)));
    }

    @Test
    public void onNotifyAvailableLanguages_updateProperties(){
        final VolleyLanguages languages = spy(languages());
        languages.withData(mock(Languages.Data.class));
        languages.attachState(mock(Languages.State.class));

        languages.notifyAvailableLanguages(SMALL_DATA);

        Assert.assertThat(languages.getData(), is(equalTo(SMALL_DATA)));
    }

    @Test
    public void onLoadAvailableLanguagesList_addRequestToVolley() throws Exception {
        final RequestQueue queueMock = mock(RequestQueue.class);
        final VolleyLanguages languages
                = new VolleyLanguages(queueMock, builder(), UI);

        languages.loadAvailableLanguages(languages);

        verify(queueMock, times(1)).add(any(VolleyLanguages.AvailableLanguagesRequest.class));
    }

    @Test
    public void onSuccessCallback_notifyListener() throws Exception {
        final Languages.Listener listener = mock(Languages.Listener.class);

        final VolleyLanguages.AvailableLanguagesRequest request = builder()
                .withListener(listener)
                .build();

        request.deliverResponse(JSON);

        verify(listener, times(1))
                .notifyAvailableLanguages(any(Languages.Data.class));
    }

    @Test
    public void whenAvailableLanguagesRequestCreated_allFieldsAreCorrect() throws Exception {
        final VolleyLanguages.AvailableLanguagesRequest request = builder().build();

        Assert.assertThat(request.getMethod(), Is.is(equalTo(Request.Method.POST)));
        Assert.assertThat(request.getUrl(), Is.is(equalTo(URL)));


        Assert.assertThat(request.getParams().get("ui"), Is.is(equalTo(UI)));
        Assert.assertThat(request.getParams().get("key"), Is.is(equalTo(KEY)));
    }

    private VolleyLanguages languages() {
        return spy(
                new VolleyLanguages(
                        mock(RequestQueue.class), mock(VolleyLanguages.RequestBuilder.class), UI
                )
        );
    }

    private VolleyLanguages.RequestBuilder builder() {
        return new VolleyLanguages.RequestBuilder(URL, KEY)
                .aPost()
                .withListener(mock(Languages.Listener.class))
                .withUi(UI);
    }
}