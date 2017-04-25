package com.ivolnov.ytranslator;

import android.os.Handler;
import android.support.v4.content.Loader;

import com.ivolnov.ytranslator.db.jobs.ForceLoadJob;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * {@link ForceLoadJob} local unit tests.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 24.04.17
 */

public class ForceLoadJobTest {

    @Test
    public void runTest() throws Exception {
        final Handler uiThread = mock(Handler.class);
        final Loader loader = mock(Loader.class);
        final ForceLoadJob job = new ForceLoadJob(uiThread, loader);

        final ArgumentCaptor<Runnable> runnableCallingUiThread
                = ArgumentCaptor.forClass(Runnable.class);

        job.run();

        verify(uiThread, times(1)).post(runnableCallingUiThread.capture());

        runnableCallingUiThread.getValue().run();

        verify(loader, times(1)).forceLoad();
    }
}
