package com.ivolnov.ytranslator.dictionary;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineHeightSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.ivolnov.ytranslator.R;

import java.util.List;

/**
 * Utility class to compile {@link DictionaryItem} model class into {@link SpannableStringBuilder}
 * according to the desirable layout and styling.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 06.04.17
 */

public class DictionaryItemCompiler {

    private final int LIGHT;
    private final int ACCENT;
    private final int DARK;
    private final int COMPLEMENT;
    /* Used to enforce top margin for a line by making it higher*/
    private final int LINE_HEIGHT;

    /**
     * Constructor.
     *
     * @param context a {@link Context} instance to fetch resources from.
     */
    public DictionaryItemCompiler(Context context) {
        LIGHT = ContextCompat.getColor(context, R.color.textMedium);
        ACCENT = ContextCompat.getColor(context, R.color.textAccent);
        DARK = ContextCompat.getColor(context, R.color.textDark);
        COMPLEMENT = ContextCompat.getColor(context, R.color.textComplementDark);
        LINE_HEIGHT
                = (int) context.getResources().getDimension(R.dimen.activity_vertical_margin) / 2;
    }

    /**
     * Compiles {@link DictionaryItem} model class into {@link SpannableStringBuilder}.
     *
     * @param item a dictionary item to compile.
     * @return {@link SpannableStringBuilder} filled with layout and content.
     */
    public SpannableStringBuilder compile(DictionaryItem item) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        compileTitle(item.getTitle(), builder);
        compileMeanings(item.getMeanings(), builder);

        return builder;
    }

    /**
     * Example:
     *
     * time [taɪm] сущ
     */
    private SpannableStringBuilder compileTitle(DictionaryItem.Title title, SpannableStringBuilder builder) {
        final int start = builder.length();
        final int transcriptionStart = start + title.getText().length() + 1;
        final int transcriptionEnd = transcriptionStart + title.getTranscription().length() + 2;

        int posStart = transcriptionEnd + (title.getTranscription().length() == 0 ? -2 : 1);
        int posEnd = posStart + title.getPos().length() + 1;

        builder.append(title.getText());

        if(!title.getTranscription().equals("")) {
            builder.append(" [").append(title.getTranscription()).append("] ");
            builder.setSpan(
                    new ForegroundColorSpan(LIGHT),
                    transcriptionStart,
                    transcriptionEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        } else {
            builder.append(' ');
        }

        builder
                .append(title.getPos())
                .append(System.getProperty("line.separator"));

        builder.setSpan(
                new ForegroundColorSpan(ACCENT),
                posStart,
                posEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        builder.setSpan(
                new HeightSpan(LINE_HEIGHT + LINE_HEIGHT / 2),
                start,
                posEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        return builder;

    }

    /**
     * Example:
     *
     * 1 время ср, раз м, момент м, срок м, пора, период м
     *   (period, once, moment, pore)
     */
    private SpannableStringBuilder compileMeanings(List<DictionaryItem.Meaning> meanings,
                                                   SpannableStringBuilder builder) {
        for (int i=0; i < meanings.size(); i++) {
            DictionaryItem.Meaning meaning = meanings.get(i);

            if (!meaning.getValues().isEmpty()) {
                compileMeaningValues(i + 1, meaning.getValues(), builder);
            }
            if (!meaning.getTranslations().isEmpty()) {
                compileMeaningTranslations(meaning.getTranslations(), builder);
            }
        }
        return builder;
    }

    /**
     * Example:
     *
     * 1 время ср, раз м, момент м, срок м, пора, период м
     */
    private SpannableStringBuilder compileMeaningValues(int index,
                                                        List<DictionaryItem.Value> values,
                                                        SpannableStringBuilder builder) {
        final int start = builder.length();

        final int indexStart = builder.length();
        final int indexEnd = start + 1;

        builder.append(Integer.toString(index));
        builder.append(' ');
        builder.setSpan(
                new ForegroundColorSpan(LIGHT),
                indexStart,
                indexEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        for (int i=0; i < values.size(); i++) {
            DictionaryItem.Value value = values.get(i);

            final int valueStart = builder.length();
            final int valueEnd = valueStart + value.getMeaning().length();
            final int markStart = valueEnd;
            final int markEnd = markStart
                    + value.getMark().length()
                    + (value.getMark().length() == 0 ? 0 : 1);

            builder
                    .append(value.getMeaning());

            if (!value.getMark().equals("")) {
                builder .append(' ')
                        .append(value.getMark());
            }

            if (i != values.size() - 1) {
                builder.append(", ");
            }

            builder.setSpan(
                    new ForegroundColorSpan(DARK),
                    valueStart,
                    valueEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            if (markStart != markEnd) {
                builder.setSpan(
                        new ForegroundColorSpan(LIGHT),
                        markStart,
                        markEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                builder.setSpan(
                        new StyleSpan(Typeface.ITALIC),
                        markStart,
                        markEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                builder.setSpan(
                        new RelativeSizeSpan(0.8f),
                        markStart,
                        markEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }

        final int end = builder.length();

        builder.append(System.getProperty("line.separator"));

        builder.setSpan(
                new HeightSpan(LINE_HEIGHT + LINE_HEIGHT / 2),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        return builder;
    }

    /**
     * Example:
     *
     *   (period, once, moment, pore)
     */
    private  SpannableStringBuilder compileMeaningTranslations(List<DictionaryItem.Translation> translations,
                                                              SpannableStringBuilder builder) {
        final int start = builder.length();

        builder.append("  (");

        for (int i=0; i < translations.size(); i++) {
            DictionaryItem.Translation translation = translations.get(i);

            builder.append(translation.getValue());

            if (i != translations.size() - 1) {
                builder.append(", ");
            }
        }

        builder.append(')');

        final int end = builder.length();

        builder.append(System.getProperty("line.separator"));

        builder.setSpan(
                new ForegroundColorSpan(COMPLEMENT),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        builder.setSpan(
                new HeightSpan(LINE_HEIGHT * 3 / 2),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        builder.setSpan(
                new LeadingMarginSpan.Standard(1),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        return builder;
    }

    /**
     * Solution for spanning text with particular line height.
     * @see <a href="http://stackoverflow.com/questions/12917813/android-spannable-line-height">stackoverflow</a>.
     */
    private static class HeightSpan implements LineHeightSpan.WithDensity {
        private int mSize;
        private static float sProportion = 0;

        public HeightSpan(int size) {
            mSize = size;
        }

        public void chooseHeight(CharSequence text, int start, int end,
                                 int spanstartv, int v,
                                 Paint.FontMetricsInt fm) {
            // Should not get called, at least not by StaticLayout.
            chooseHeight(text, start, end, spanstartv, v, fm, null);
        }

        public void chooseHeight(CharSequence text, int start, int end,
                                 int spanstartv, int v,
                                 Paint.FontMetricsInt fm, TextPaint paint) {
            int size = mSize;
            if (paint != null) {
                size *= paint.density;
            }

            if (fm.bottom - fm.top < size) {
                fm.top = fm.bottom - size;
                fm.ascent = fm.ascent - size;
            } else {
                if (sProportion == 0) {
                /*
                 * Calculate what fraction of the nominal ascent
                 * the height of a capital letter actually is,
                 * so that we won't reduce the ascent to less than
                 * that unless we absolutely have to.
                 */

                    Paint p = new Paint();
                    p.setTextSize(100);
                    Rect r = new Rect();
                    p.getTextBounds("ABCDEFG", 0, 7, r);

                    sProportion = (r.top) / p.ascent();
                }

                int need = (int) Math.ceil(-fm.top * sProportion);

                if (size - fm.descent >= need) {
                /*
                 * It is safe to shrink the ascent this much.
                 */

                    fm.top = fm.bottom - size;
                    fm.ascent = fm.descent - size;
                } else if (size >= need) {
                /*
                 * We can't show all the descent, but we can at least
                 * show all the ascent.
                 */

                    fm.top = fm.ascent = -need;
                    fm.bottom = fm.descent = fm.top + size;
                } else {
                /*
                 * Show as much of the ascent as we can, and no descent.
                 */

                    fm.top = fm.ascent = -size;
                    fm.bottom = fm.descent = 0;
                }
            }
        }
    }
}
