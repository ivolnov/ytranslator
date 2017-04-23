package com.ivolnov.ytranslator.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

/**
 * Dumb successor of {@link ArrayAdapter<String>} to keep things more domain oriented.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 11.04.17
 */

public class LanguageSpinnerAdapter extends ArrayAdapter<String> {

    public LanguageSpinnerAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }
}