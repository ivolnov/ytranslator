package com.ivolnov.ytranslator.languages;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import static com.ivolnov.ytranslator.YandexApi.KEY_KEY;
import static com.ivolnov.ytranslator.YandexApi.UI_KEY;
import static com.ivolnov.ytranslator.languages.DefaultLanguages.DEFAULT_DATA_EN;
import static com.ivolnov.ytranslator.languages.DefaultLanguages.DEFAULT_DATA_RU;

/**
 * Volley powered implementation of {@link Languages}.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 11.04.17
 */

public class VolleyLanguages implements Languages, Languages.Listener {

    public static final String TAG = "VolleyLanguages";

    private LanguagesSwapSemaphore mSemaphore;
    private RequestQueue mQueue;
    private RequestBuilder mBuilder;
    private State mState;
    private Data mData;
    private String mUi;

    public VolleyLanguages(RequestQueue queue, RequestBuilder builder, String ui) {
        mSemaphore = new LanguagesSwapSemaphore();
        mQueue = queue;
        mBuilder = builder;
        mData = defaultDataForUi(ui);
        mUi = ui;
    }

    @Override
    public String getDirection() {
        final String source = mState.getSourceLanguage();
        final String target = mState.getTargetLanguage();
        final String sourceTag = mData.getTagIndex().get(source);
        final String targetTag = mData.getTagIndex().get(target);

        return sourceTag + '-' + targetTag;
    }

    @Override
    public void onSourceLanguageChanged(String language) {
        if (!mSemaphore.p()) {
            mState.notifyDirectionChanged();
        }
    }

    @Override
    public void onTargetLanguageChanged(String language) {
        if (!mSemaphore.p()) {
            mState.notifyDirectionChanged();
        }
    }

    @Override
    public void onLanguagesSwapped() {
        mState.notifySwap();
        mSemaphore.v();
    }

    @Override
    public void attachState(State state) {
        this.mState = state;
        mState.notifyLanguagesChanged(mData.getSortedLanguages());
    }

    @Override
    public void notifyAvailableLanguages(Data data) {
        mData = data;
        mState.notifyLanguagesChanged(mData.getSortedLanguages());
    }

    @Override
    public void notifyAvailableLanguagesError(String error) {

    }

    @Override
    public void loadAvailableLanguages(Listener listener) {

        final AvailableLanguagesRequest request = mBuilder
                .aPost()
                .withUi(mUi)
                .withListener(this)
                .build();

        mQueue.add(request);
    }

    public Data getData() {
        return mData;
    }

    public VolleyLanguages withState(State state) {
        this.mState = state;
        return this;
    }

    public VolleyLanguages withData(Data mData) {
        this.mData = mData;
        return this;
    }

    public VolleyLanguages withSemaphore(LanguagesSwapSemaphore semaphore) {
        this.mSemaphore = semaphore;
        return this;
    }

    public State getState() {
        return mState;
    }

    private Data defaultDataForUi(String ui) {
        return ui.equals("ru") ? DEFAULT_DATA_RU: DEFAULT_DATA_EN;
    }

    /**
     * Utility class to build available languages requests.
     * Stores state of the previous build: every parameter that is not set explicitly
     * is inherited from the previous invocation.
     */
    public static class RequestBuilder {
        private int mMethod;
        private String mUrl;
        private Languages.Listener mListener;
        private Map<String, String> mParams;

        /**
         * Constructor.
         *
         * @param url link to the resource to be requested.
         * @param key an api key.
         */
        public RequestBuilder(String url, String key) {
            this(url, key, Request.Method.POST);
        }

        /**
         * Constructor.
         *
         * @param url link to the resource to be requested.
         * @param key an api key.
         * @param method {@link com.android.volley.Request.Method} to be used.
         */
        public RequestBuilder(String url, String key, int method) {
            this.mUrl = url;
            this.mMethod = method;
            this.mParams = new HashMap<>(2);
            this.mParams.put(KEY_KEY, key);
        }

        public RequestBuilder aPost() {
            mMethod = Request.Method.POST;
            return this;
        }

        public VolleyLanguages.RequestBuilder withUi(String ui) {
            mParams.put(UI_KEY, ui);
            return this;
        }

        public RequestBuilder withListener(Listener listener){
            mListener = listener;
            return this;
        }

        public AvailableLanguagesRequest build() {
            if (mListener == null) {
                throw new RuntimeException("No listener is set for this request.");
            }

            return new AvailableLanguagesRequest(
                    mMethod,
                    mUrl,
                    buildOnSuccess(mListener),
                    buildOnError(mListener))
                    .setParams(mParams);
        }

        private Response.Listener<String> buildOnSuccess(final Listener listener) {
            return new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Data data = LanguagesCodec.jsonToLanguagesData(response);
                        listener.notifyAvailableLanguages(data);
                    } catch (JSONException e) {
                        Log.d(TAG, e.getMessage());
                        listener.notifyAvailableLanguagesError(e.getMessage());
                    }
                }
            };
        }

        private Response.ErrorListener buildOnError(final Listener listener) {
            return new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //nothing to log here
                }
            };
        }
    }

    /**
     * Wrapper for {@link StringRequest}.
     * The only purpose of this class is to provide access to the protected method deliverResponse()
     * for the unit testing reasons.
     *
     * @author ivolnov
     * @version %I%, %G%
     * @since 04.04.17
     */

    public static class AvailableLanguagesRequest extends StringRequest {

        private Map<String, String> mParams;

        public AvailableLanguagesRequest(int method, String url,
                             Response.Listener<String> listener,
                             Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        /**
         * Simple delegates a call to the parent.
         *
         * @param response a {@link String} payload from Http response.
         */
        public void deliverResponse(String response) {
            super.deliverResponse(response);
        }

        /**
         * Sets http parameters for this request.
         *
         * @param params a {@link Map} with parameters' key values.
         */
        public VolleyLanguages.AvailableLanguagesRequest setParams(Map<String, String> params) {
            mParams = params;
            return this;
        }

        @Override
        public Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = super.getParams() != null ? super.getParams(): mParams;
            if (super.getParams() != null) {
                params.putAll(mParams);
            }
            return params;
        }
    }
}