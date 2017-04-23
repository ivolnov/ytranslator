package com.ivolnov.ytranslator;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import com.ivolnov.ytranslator.adapters.DictionaryAdapter;
import com.ivolnov.ytranslator.dictionary.DictionaryItem;
import com.ivolnov.ytranslator.dictionary.DictionaryItemCompiler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link DictionaryAdapter} class local unit tests.
 * Is used because {@link android.database.Observable}'s registerObservable method
 * is a part of android SDK.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 10.04.17
 */

@RunWith(RobolectricTestRunner.class)
public class DictionaryAdapterTest {
    @Test
    public void notifyLookedUpTest() throws Exception {
        final int count = 3;
        final DictionaryAdapter adapter
                = new DictionaryAdapter(mock(DictionaryItemCompiler.class));
        final RecyclerView.AdapterDataObserver observer
                = mock(RecyclerView.AdapterDataObserver.class);

        adapter.registerAdapterDataObserver(observer);

        final DictionaryAdapter spy = spy(adapter);
        final List<DictionaryItem> items
                = fillWithMocks(new ArrayList<DictionaryItem>(), count);

        spy.notifyLookedUp(items);

        verify(spy, times(1)).notifyDataSetChanged();

        Assert.assertThat(spy.getItems(), is(equalTo(items)));
    }

    @Test
    public void onBinViewHolderTest() throws Exception {
        final int index = 0;
        final View container = mock(View.class);
        final TextView viewItem = mock(TextView.class);
        final SpannableStringBuilder viewContent = mock(SpannableStringBuilder.class);
        final DictionaryItem dictionaryItem = mock(DictionaryItem.class);
        final DictionaryItemCompiler compiler = mock(DictionaryItemCompiler.class);

        when(compiler.compile(dictionaryItem)).thenReturn(viewContent);
        when(container.findViewById(anyInt())).thenReturn(viewItem);

        final DictionaryAdapter adapter = new DictionaryAdapter(compiler);
        final DictionaryAdapter.DictionaryItemViewHolder viewHolder
                = new DictionaryAdapter.DictionaryItemViewHolder(container);

        adapter.getItems().add(dictionaryItem);
        adapter.onBindViewHolder(viewHolder, index);

        verify(viewItem, times(1)).setText(viewContent);
    }

    @Test
    public void getItemCountTest() throws Exception {
        final int count = 3;
        final DictionaryAdapter adapter = new DictionaryAdapter(mock(DictionaryItemCompiler.class));

        fillWithMocks(adapter.getItems(), count);

        Assert.assertThat(adapter.getItemCount(), is(equalTo(count)));
    }

    private List<DictionaryItem> fillWithMocks(List<DictionaryItem> items, int amount) {
        for (int i = 0; i < amount; i++) {
            items.add(mock(DictionaryItem.class));
        }
        return items;
    }
}
