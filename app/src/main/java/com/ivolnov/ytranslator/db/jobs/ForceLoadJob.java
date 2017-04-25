package com.ivolnov.ytranslator.db.jobs;

import android.os.Handler;
import android.support.v4.content.Loader;

/**
 * {@link Runnable} that forces a loader to start load on UI thread.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 24.04.17
 */

public class ForceLoadJob implements Runnable {
    private Handler uiThread;
    private Loader loader;

    public ForceLoadJob(Handler uiThread, Loader loader) {
        this.uiThread = uiThread;
        this.loader = loader;
    }

    @Override
    public void run() {
        uiThread.post(new Runnable() {
            @Override
            public void run() {
                loader.forceLoad();
            }
        });
    }
}
