package com.ivolnov.ytranslator.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivolnov.ytranslator.dictionary.Dictionary;
import com.ivolnov.ytranslator.dictionary.DictionaryItem;
import com.ivolnov.ytranslator.R;
import com.ivolnov.ytranslator.dictionary.DictionaryItemCompiler;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link RecyclerView.Adapter} responsible for the dictionary list content.
 * Implements {@link Dictionary.Listener} to be aware of lookup results.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 31.03.17
 */
public class DictionaryAdapter
        extends RecyclerView.Adapter<DictionaryAdapter.DictionaryItemViewHolder>
        implements Dictionary.Listener {

    private final List<DictionaryItem> mItems;
    private final DictionaryItemCompiler mCompiler;

    public DictionaryAdapter(DictionaryItemCompiler mCompiler) {
        this.mItems = new ArrayList<>(5);
        this.mCompiler = mCompiler;
    }

    @Override
    public DictionaryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dictionary_item, parent, false);
        return new DictionaryItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DictionaryItemViewHolder holder, int position) {
        final DictionaryItem item = mItems.get(position);
        final Editable compiledItem = mCompiler.compile(item);
        holder.item.setText(compiledItem);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void notifyLookedUp(List<DictionaryItem> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public void notifyLookupError(String error) {

    }

    public List<DictionaryItem> getItems() {
        return mItems;
    }

    /**
     * A {@link android.support.v7.widget.RecyclerView.ViewHolder} for dictionary list item.
     */
    public static class DictionaryItemViewHolder extends RecyclerView.ViewHolder {
        public final TextView item;

        public DictionaryItemViewHolder(View view) {
            super(view);
            this.item  = (TextView) view.findViewById(R.id.dictionaryItem);
        }
    }
}