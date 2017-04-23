package com.ivolnov.ytranslator.languages;

/**
 * A semaphore variation.
 *
 * Created to solve problems when several changes in UI (like updated spinner and cleared text
 * field) need to be executed atomically but spawn multiple UI events. In case other modules are
 * subscribed on them it may lead to duplicated functionality like several network requests.
 *
 * This particular one is used to amortize spinners' on selected events that happen on language swap
 * as each of them leads to a new translation query by default. Thus when we swap translation query
 * and translation text during language swap(which in turn executes a translation request) we must
 * raise this semaphore to prevent two language spinners to cause the same call twice.
 *
 * Not thread safe!
 * Operations are non blocking: no waiting is implemented or etc.
 * Usage:
 *    if (mSemaphore.p()) //don't do what you've planned.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 13.04.17
 */

public class LanguagesSwapSemaphore {

    private int flag;

    public LanguagesSwapSemaphore() {
        flag = 0;
    }

    /**
     * Raises semaphore by two.
     */
    public void v() {
        flag += 2;
    }

    /**
     * Lowers semaphore by one.
     *
     * @return true in case of success false if was already down.
     */
    public boolean p() {
        if (flag > 0) {
            flag--;
            return true;
        } else {
            return false;
        }
    }
}
