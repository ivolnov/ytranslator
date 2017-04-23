package com.ivolnov.ytranslator.dictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for content of a dictionary item.
 * Basically acts like a medium between json and a text view.
 * As the json data gets explored more and more this approach might be changed to
 * some Jackson or Gson implementation.
 * See <a href="http://www.jsonschema2pojo.org/">jsonschema2pojo</a> generator.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 05.04.17
 */

public class DictionaryItem {

    private Title title;
    private List<Meaning> meanings;

    public static class Title {
        private String text;
        private String transcription;
        private String pos; // noun, adjective etc

        public Title withText(String text) {
            this.text = text;
            return this;
        }

        public Title withTranscription(String transcription) {
            this.transcription = transcription;
            return this;
        }

        public Title withPos(String pos) {
            this.pos = pos;
            return this;
        }

        public String getText() {
            return text;
        }

        public String getTranscription() {
            return transcription;
        }

        public String getPos() {
            return pos;
        }

        /**
         * @see <a href="http://stackoverflow.com/a/31207050/4003403">stackoverflow</a>
         */
        public int getSize() {
            final int textSize = text != null ? 8 * ((((text.length()) * 2) + 45) / 8) : 0;
            final int transcriptionSize
                    = transcription != null ? 8 * ((((transcription.length()) * 2) + 45) / 8) : 0;
            final int posSize = pos != null ? 8 * ((((pos.length()) * 2) + 45) / 8) : 0;

            return 4 * 3 + textSize + transcriptionSize + posSize;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Title title = (Title) o;

            if (!text.equals(title.text)) return false;
            if (!transcription.equals(title.transcription)) return false;
            return pos.equals(title.pos);

        }

        @Override
        public int hashCode() {
            int result = text.hashCode();
            result = 31 * result + transcription.hashCode();
            result = 31 * result + pos.hashCode();
            return result;
        }
    }

    public static class Meaning {
        private List<Value> values;
        private List<Translation> translations;

        public Meaning() {
            this.values = new ArrayList<>();
            this.translations = new ArrayList<>();
        }

        public Meaning withValue(Value value) {
            this.values.add(value);
            return this;
        }

        public Meaning withTranslation(Translation translation) {
            this.translations.add(translation);
            return this;
        }

        public List<Value> getValues() {
            return values;
        }

        public List<Translation> getTranslations() {
            return translations;
        }

        public int getSize() {
            int valuesListSize = 16 + 4; //aligned header + int size
            int translationsListSize = 16 + 4; //aligned header + int size

            for(Value value: values) {
                valuesListSize += 4 + value.getSize();
            }

            for(Translation translation: translations) {
                translationsListSize += 4 + translation.getSize();
            }

            return 16 + 4 * valuesListSize + 4 * translationsListSize;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Meaning meaning = (Meaning) o;

            if (!values.equals(meaning.values)) return false;
            return translations.equals(meaning.translations);

        }

        @Override
        public int hashCode() {
            int result = values.hashCode();
            result = 31 * result + translations.hashCode();
            return result;
        }
    }

    public static class Value {
        private String meaning;
        private String mark; // plural, gender etc

        public Value withMeaning(String meaning) {
            this.meaning = meaning;
            return this;
        }

        public Value withMark(String mark) {
            this.mark = mark;
            return this;
        }

        public String getMeaning() {
            return meaning;
        }

        public String getMark() {
            return mark;
        }

        public int getSize() {
            final int posSize = mark != null ? 8 * ((((mark.length()) * 2) + 45) / 8) : 0;
            final int meaningSize
                    = meaning != null ? 8 * ((((meaning.length()) * 2) + 45) / 8) : 0;
            return 4 * 2 + posSize + meaningSize;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Value value = (Value) o;

            if (!meaning.equals(value.meaning)) return false;
            return mark.equals(value.mark);

        }

        @Override
        public int hashCode() {
            int result = meaning.hashCode();
            result = 31 * result + mark.hashCode();
            return result;
        }
    }

    public static class Translation {
        private String value;

        public Translation withValue(String value) {
            this.value = value;
            return this;
        }

        public String getValue() {
            return value;
        }

        public int getSize() {
            final int valueSize = value != null ? 8 * ((((value.length()) * 2) + 45) / 8) : 0;
            return 4  + valueSize;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Translation that = (Translation) o;

            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    public DictionaryItem() {
        this.meanings = new ArrayList<>();
    }

    public DictionaryItem withTitle(Title title) {
        this.title = title;
        return this;
    }

    public DictionaryItem withMeaning(Meaning meaning) {
        this.meanings.add(meaning);
        return this;
    }

    public Title getTitle() {
        return title;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }

    public int getSize() {
        int listSize = 16 + 4; //aligned header + int size

        for(Meaning meaning: meanings) {
            listSize += 4 + meaning.getSize();
        }

        return 16 + 4 * listSize + 4 * title.getSize();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DictionaryItem that = (DictionaryItem) o;

        if (!title.equals(that.title)) return false;
        return meanings.equals(that.meanings);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + meanings.hashCode();
        return result;
    }
}