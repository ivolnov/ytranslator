package com.ivolnov.ytranslator.dictionary;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ivolnov.ytranslator.YandexApi.*;

/**
 * Volley powered implementation of {@link Dictionary}.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 06.04.17
 */

public class VolleyDictionary implements Dictionary {

    public static final String TAG = "VolleyDictionary";

    private DictionaryCache mCache;
    private RequestQueue mQueue;
    private RequestBuilder mBuilder;

    /**
     * Constructor.
     *
     * @param queue Volley's {@link RequestQueue}.
     */
    public VolleyDictionary(RequestQueue queue, RequestBuilder builder) {
        this.mCache = new DictionaryCache(cacheSize());
        this.mQueue = queue;
        this.mBuilder = builder;
    }

    @Override
    public void lookup(String query, String direction, String ui, Listener listener) {
        final DictionaryCache.Key key = DictionaryCache.Key.from(query, direction, ui);

        if (mCache.get(key) != null) {
            final List<DictionaryItem> items = mCache.get(key);
            listener.notifyLookedUp(items);
            return;
        }

        final LookupRequest request = mBuilder
                .aPost()
                .withDirection(direction)
                .withListener(listener)
                .withText(query)
                .withUi(ui)
                .build();

        mQueue.add(request);
    }

    @Override
    public void stopPending() {

    }

    public VolleyDictionary withCache(DictionaryCache mCache) {
        this.mCache = mCache;
        return this;
    }

    /**
     * Lets estimate an average size of a dictionary lookup result is equal to a size of a lookup
     * for a word 'time' which is a good example as it has many meaning. List of
     * {@link DictionaryItem} instances for this lookup is roughly 49308 bytes (
     * according to getSize method) which absolutely dominates query, direction or ui strings size.
     * For every word in english that dictionary will take roughly 8 Gib.
     * Lets say a user has an 8 hour working day where he uses translator every minute with unique
     * one word queries and the app is always active.
     * That leads to 8 * 60 * 49308 = 23,667,840 bytes of data a day.
     * By rounding this number up we have an acceptable size for most devices.
     */
    private int cacheSize() {
        final long heuristic = 30 * 1000 * 1000; // 30 mib
        final long max = Runtime.getRuntime().maxMemory();
        return (int) (heuristic > max ? heuristic : max);
    }

    /**
     * Utility class to build lookup requests.
     * Stores state of the previous build: every parameter that is not set explicitly
     * is inherited from the previous invocation.
     */
    public static class RequestBuilder {

        private int mMethod;
        private String mUi;
        private String mUrl;
        private String mQuery;
        private String mDirection;
        private Listener mListener;
        private Map<String, String> mParams;
        private DictionaryCache mCache;

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
            this.mParams = new HashMap<>(6);
            this.mParams.put(KEY_KEY, key);
            this.mParams.put(OPTIONS_KEY, OPTIONS_VALUE);
        }

        public RequestBuilder withCache(DictionaryCache mCache) {
            this.mCache = mCache;
            return this;
        }

        public RequestBuilder aPost() {
            mMethod = Request.Method.POST;
            return this;
        }

        public RequestBuilder withListener(Listener listener){
            mListener = listener;
            return this;
        }

        public RequestBuilder withText(String text) {
            mQuery = text;
            mParams.put(TEXT_KEY, text);
            return this;
        }

        public RequestBuilder withDirection(String direction) {
            mDirection = direction;
            mParams.put(LANG_KEY, direction);
            this.mParams.put(DICT_KEY, direction + '.' +REGULAR_KEY);
            return this;
        }

        public RequestBuilder withUi(String ui) {
            mUi = ui;
            mParams.put(UI_KEY, ui);
            return this;
        }

        public LookupRequest build() {
            if (mListener == null) {
                throw new RuntimeException("No listener is set for this request.");
            }

            return new LookupRequest(
                    mMethod,
                    mUrl,
                    buildOnSuccess(mListener, mQuery, mDirection, mUi),
                    buildOnError(mListener))
                    .setParams(mParams);
        }

        private Response.Listener<String> buildOnSuccess
                (final Listener listener, final String query, final String direction, final String ui) {
            return new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        final List<DictionaryItem> items
                                = DictionaryCodec.jsonToItems(response);
                        final DictionaryCache.Key key
                                = DictionaryCache.Key.from(query, direction, ui);
                        cache(key, items);
                        listener.notifyLookedUp(items);
                    } catch (JSONException e) {
                        Log.d(TAG, e.getMessage());
                        listener.notifyLookupError(e.getMessage());
                    }
                }
            };
        }

        private Response.ErrorListener buildOnError(final Listener listener) {
            return new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.notifyLookedUp(Collections.<DictionaryItem>emptyList());
                }
            };
        }

        private void cache(DictionaryCache.Key key, List<DictionaryItem> items) {
            if (mCache != null) {
                mCache.put(key, items);
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

    public static class LookupRequest extends StringRequest {

        private Map<String, String> mParams;

        public LookupRequest(int method, String url,
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
        public LookupRequest setParams(Map<String, String> params) {
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
