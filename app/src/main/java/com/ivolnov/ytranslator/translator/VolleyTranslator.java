package com.ivolnov.ytranslator.translator;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.ivolnov.ytranslator.YandexApi.*;

/**
 * Volley powered implementation of {@link Translator}.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 03.04.17
 */

public class VolleyTranslator implements Translator {

    public static final String TAG = "VolleyTranslator";

    private RequestQueue mQueue;
    private RequestBuilder mBuilder;
    private TranslatorCache mCache;

    /**
     * Constructor.
     *
     * @param queue Volley's {@link RequestQueue}.
     * @param builder a {@link RequestBuilder} to use for api calls.
     */
    public VolleyTranslator(RequestQueue queue, RequestBuilder builder) {
        this.mCache = new TranslatorCache(cacheSize());
        this.mQueue = queue;
        this.mBuilder = builder;
    }

    @Override
    public void translate(String query, String direction, Listener listener) {

        if (mCache.get(query, direction) != null) {
            final String translation = mCache.get(query,direction);
            listener.notifyTranslated(query, direction, translation);
            return;
        }

        TranslateRequest request = mBuilder
                .aPost()
                .withDirection(direction)
                .withListener(listener)
                .withText(query)
                .build();

        mQueue.add(request);
    }

    @Override
    public void stopPending() {
        mQueue.cancelAll(TAG);
    }

    public VolleyTranslator withCache(TranslatorCache mCache) {
        this.mCache = mCache;
        return this;
    }

    /**
     * Runtime.getRuntime().maxMemory() is not very useful here.
     * The main reason for this cache is to avoid networking on unfinished requests that
     * appear when a user types words with pauses. If an average word length is 5 letters
     * and 5 ^ 5 = 3125 then we need roughly 3125 * (36 + 5 * 2) = 143,750 bytes of query strings.
     * So let it be one order of magnitude bigger that this to account direction, translation
     * strings.
     */
    private int cacheSize() {
        return 1024 * 1024; // 1MiB
    }

    /**
     * Utility class to build translation requests.
     * Stores state of the previous build: every parameter that is not set explicitly
     * is inherited from the previous invocation.
     */
    public static class RequestBuilder {
        private int mMethod;
        private String mUrl;
        private Translator.Listener mListener;
        private Map<String, String> mParams;
        private TranslatorCache mCache;

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
            this.mParams = new HashMap<>(4);
            this.mParams.put(KEY_KEY, key);
            this.mParams.put(OPTIONS_KEY, OPTIONS_VALUE);
        }

        public RequestBuilder withCache(TranslatorCache mCache) {
            this.mCache = mCache;
            return this;
        }

        public RequestBuilder aPost() {
            mMethod = Request.Method.POST;
            return this;
        }

        public RequestBuilder withListener(Translator.Listener listener){
            mListener = listener;
            return this;
        }

        public RequestBuilder withText(String text) {
            mParams.put(TEXT_KEY, text);
            return this;
        }

        public RequestBuilder withDirection(String direction) {
            mParams.put(LANG_KEY, direction);
            return this;
        }

        public TranslateRequest build() {
            if (mListener == null) {
                throw new RuntimeException("No listener is set for this request.");
            }

            final String query = mParams.get(TEXT_KEY);

            return new TranslateRequest(
                    mMethod,
                    mUrl,
                    buildOnSuccess(mListener, query),
                    buildOnError(mListener))
                    .setParams(mParams);
        }

        private Response.Listener<String> buildOnSuccess(
                final Translator.Listener listener, final String query) {

            return new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        final String translation = new JSONObject(response)
                                .getJSONArray(TEXT_KEY)
                                .getString(0);
                        final String direction = new JSONObject(response)
                                .getString(LANG_KEY);

                        cache(query, direction, translation);
                        listener.notifyTranslated(query, direction, translation);

                    } catch (JSONException e) {
                        Log.d(VolleyTranslator.TAG,
                                "Unexpected json in response.\n " + e.getMessage());
                        listener.notifyTranslationError("Unexpected response.");
                    }
                }
            };
        }

        private Response.ErrorListener buildOnError(final Translator.Listener listener) {
            return new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //nothing to log here
                }
            };
        }

        private void cache(String query, String direction, String translation) {
            if (mCache != null) {
                mCache.put(query, direction, translation);
            }
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

    public static class TranslateRequest extends StringRequest {

        private Map<String, String> mParams;

        public TranslateRequest(int method, String url,
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
        public TranslateRequest setParams(Map<String, String> params) {
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