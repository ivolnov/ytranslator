package com.ivolnov.ytranslator.util;

import com.ivolnov.ytranslator.languages.Languages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test utility class that stores data for available languages api related tests:
 * - list of available languages
 * - reverse index from languages to their tags
 * - sorted array of available languages
 * - {@link Languages.Data} instance with the above listed data.
 * - small version of language array
 * - {@link Languages.Data} with small array.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 12.04.17
 */

public class AvailableLanguagesApiTestData {

    public static final String UI = "ru";
    public static final String KEY = "api_key";
    public static final String DIRECTION = "en-ru";
    public static final String URL = "https://example.com";
    public static final String JSON = loadJsonFromFile();
    public static final String TARGET_LANGUAGE = "Русский";
    public static final String SOURCE_LANGUAGE = "Английский";

    public static final Languages.Data DATA = new Languages.Data() {
        @Override
        public String[] getSortedLanguages() {
            return AVAILABLE_LANGUAGES_ARRAY;
        }

        @Override
        public Map<String, String> getTagIndex() {
            return LANGUAGES_TAG_INDEX;
        }
    };

    public static final String[] SMALL_ARRAY = {SOURCE_LANGUAGE, TARGET_LANGUAGE};

    public static final Languages.Data SMALL_DATA = new Languages.Data(){
        @Override
        public String[] getSortedLanguages() {
            return SMALL_ARRAY;
        }

        @Override
        public Map<String, String> getTagIndex() {
            return null;
        }
    };

    public static final List<String> AVAILABLE_LANGUAGES_LIST = new ArrayList<String>(){{
        add("Африкаанс");
        add("Амхарский");
        add("Арабский");
        add("Азербайджанский");
        add("Башкирский");
        add("Белорусский");
        add("Болгарский");
        add("Бенгальский");
        add("Боснийский");
        add("Каталанский");
        add("Себуанский");
        add("Чешский");
        add("Валлийский");
        add("Датский");
        add("Немецкий");
        add("Греческий");
        add("Английский");
        add("Эсперанто");
        add("Испанский");
        add("Эстонский");
        add("Баскский");
        add("Персидский");
        add("Финский");
        add("Французский");
        add("Ирландский");
        add("Шотландский (гэльский)");
        add("Галисийский");
        add("Гуджарати");
        add("Иврит");
        add("Хинди");
        add("Хорватский");
        add("Гаитянский");
        add("Венгерский");
        add("Армянский");
        add("Индонезийский");
        add("Исландский");
        add("Итальянский");
        add("Японский");
        add("Яванский");
        add("Грузинский");
        add("Казахский");
        add("Кхмерский");
        add("Каннада");
        add("Корейский");
        add("Киргизский");
        add("Латынь");
        add("Люксембургский");
        add("Лаосский");
        add("Литовский");
        add("Латышский");
        add("Малагасийский");
        add("Марийский");
        add("Маори");
        add("Македонский");
        add("Малаялам");
        add("Монгольский");
        add("Маратхи");
        add("Горномарийский");
        add("Малайский");
        add("Мальтийский");
        add("Бирманский");
        add("Непальский");
        add("Голландский");
        add("Норвежский");
        add("Панджаби");
        add("Папьяменто");
        add("Польский");
        add("Португальский");
        add("Румынский");
        add("Русский");
        add("Сингальский");
        add("Словацкий");
        add("Словенский");
        add("Албанский");
        add("Сербский");
        add("Сунданский");
        add("Шведский");
        add("Суахили");
        add("Тамильский");
        add("Телугу");
        add("Таджикский");
        add("Тайский");
        add("Тагальский");
        add("Турецкий");
        add("Татарский");
        add("Удмуртский");
        add("Украинский");
        add("Урду");
        add("Узбекский");
        add("Вьетнамский");
        add("Коса");
        add("Идиш");
        add("Китайский");
    }};
    public static final Map<String, String> LANGUAGES_TAG_INDEX = new HashMap<String, String>(){{
        put("Татарский", "tt");
        put("Немецкий", "de");
        put("Хинди", "hi");
        put("Лаосский", "lo");
        put("Португальский", "pt");
        put("Литовский", "lt");
        put("Хорватский", "hr");
        put("Латышский", "lv");
        put("Гаитянский", "ht");
        put("Венгерский", "hu");
        put("Идиш", "yi");
        put("Армянский", "hy");
        put("Украинский", "uk");
        put("Малагасийский", "mg");
        put("Индонезийский", "id");
        put("Маори", "mi");
        put("Урду", "ur");
        put("Македонский", "mk");
        put("Папьяменто", "pap");
        put("Малаялам", "ml");
        put("Монгольский", "mn");
        put("Африкаанс", "af");
        put("Маратхи", "mr");
        put("Узбекский", "uz");
        put("Малайский", "ms");
        put("Греческий", "el");
        put("Мальтийский", "mt");
        put("Английский", "en");
        put("Эсперанто", "eo");
        put("Исландский", "is");
        put("Итальянский", "it");
        put("Амхарский", "am");
        put("Бирманский", "my");
        put("Испанский", "es");
        put("Китайский", "zh");
        put("Эстонский", "et");
        put("Баскский", "eu");
        put("Арабский", "ar");
        put("Вьетнамский", "vi");
        put("Марийский", "mhr");
        put("Японский", "ja");
        put("Непальский", "ne");
        put("Азербайджанский", "az");
        put("Персидский", "fa");
        put("Румынский", "ro");
        put("Голландский", "nl");
        put("Башкирский", "ba");
        put("Удмуртский", "udm");
        put("Себуанский", "ceb");
        put("Норвежский", "no");
        put("Белорусский", "be");
        put("Финский", "fi");
        put("Русский", "ru");
        put("Болгарский", "bg");
        put("Бенгальский", "bn");
        put("Французский", "fr");
        put("Яванский", "jv");
        put("Боснийский", "bs");
        put("Грузинский", "ka");
        put("Сингальский", "si");
        put("Словацкий", "sk");
        put("Словенский", "sl");
        put("Ирландский", "ga");
        put("Шотландский (гэльский)", "gd");
        put("Каталанский", "ca");
        put("Албанский", "sq");
        put("Сербский", "sr");
        put("Казахский", "kk");
        put("Кхмерский", "km");
        put("Сунданский", "su");
        put("Каннада", "kn");
        put("Шведский", "sv");
        put("Корейский", "ko");
        put("Горномарийский", "mrj");
        put("Суахили", "sw");
        put("Галисийский", "gl");
        put("Тамильский", "ta");
        put("Гуджарати", "gu");
        put("Киргизский", "ky");
        put("Чешский", "cs");
        put("Коса", "xh");
        put("Панджаби", "pa");
        put("Телугу", "te");
        put("Таджикский", "tg");
        put("Тайский", "th");
        put("Латынь", "la");
        put("Валлийский", "cy");
        put("Люксембургский", "lb");
        put("Тагальский", "tl");
        put("Польский", "pl");
        put("Датский", "da");
        put("Иврит", "he");
        put("Турецкий", "tr");
    }};

    public static final String[] AVAILABLE_LANGUAGES_ARRAY 
            = AVAILABLE_LANGUAGES_LIST.toArray(new String[AVAILABLE_LANGUAGES_LIST.size()]);
    
    static {
        Arrays.sort(AVAILABLE_LANGUAGES_ARRAY);
    }

    private static String loadJsonFromFile() {
        final String jsonFileName = "availableLanguages.json";
        return JsonFileReader.getStringFromFile(jsonFileName);
    }
}