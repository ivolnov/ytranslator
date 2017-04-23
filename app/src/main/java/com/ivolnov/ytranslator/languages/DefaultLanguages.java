package com.ivolnov.ytranslator.languages;

import java.util.HashMap;
import java.util.Map;

/**
 * Default values to be used for language spinners when the network lookup is not yet made
 * and no data from previous launches is available.
 * We do not want a user to see empty language spinners even if there is no network connection.
 *
 * At the moment defaults are only provided for russian ui and english based defaults for other
 * l8ns. Although translator api provides others like ukrainian and so forth. To support all
 * available defaults some smarter way than hardcode below must be implemented.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 12.04.17
 */

public class DefaultLanguages {

    public static final Languages.Data DEFAULT_DATA_RU = new Languages.Data() {
        @Override
        public String[] getSortedLanguages() {
            return new String[] {
                    "Азербайджанский",
                    "Албанский",
                    "Амхарский",
                    "Английский",
                    "Арабский",
                    "Армянский",
                    "Африкаанс",
                    "Баскский",
                    "Башкирский",
                    "Белорусский",
                    "Бенгальский",
                    "Бирманский",
                    "Болгарский",
                    "Боснийский",
                    "Валлийский",
                    "Венгерский",
                    "Вьетнамский",
                    "Гаитянский",
                    "Галисийский",
                    "Голландский",
                    "Горномарийский",
                    "Греческий",
                    "Грузинский",
                    "Гуджарати",
                    "Датский",
                    "Иврит",
                    "Идиш",
                    "Индонезийский",
                    "Ирландский",
                    "Исландский",
                    "Испанский",
                    "Итальянский",
                    "Казахский",
                    "Каннада",
                    "Каталанский",
                    "Киргизский",
                    "Китайский",
                    "Корейский",
                    "Коса",
                    "Кхмерский",
                    "Лаосский",
                    "Латынь",
                    "Латышский",
                    "Литовский",
                    "Люксембургский",
                    "Македонский",
                    "Малагасийский",
                    "Малайский",
                    "Малаялам",
                    "Мальтийский",
                    "Маори",
                    "Маратхи",
                    "Марийский",
                    "Монгольский",
                    "Немецкий",
                    "Непальский",
                    "Норвежский",
                    "Панджаби",
                    "Папьяменто",
                    "Персидский",
                    "Польский",
                    "Португальский",
                    "Румынский",
                    "Русский",
                    "Себуанский",
                    "Сербский",
                    "Сингальский",
                    "Словацкий",
                    "Словенский",
                    "Суахили",
                    "Сунданский",
                    "Тагальский",
                    "Таджикский",
                    "Тайский",
                    "Тамильский",
                    "Татарский",
                    "Телугу",
                    "Турецкий",
                    "Удмуртский",
                    "Узбекский",
                    "Украинский",
                    "Урду",
                    "Финский",
                    "Французский",
                    "Хинди",
                    "Хорватский",
                    "Чешский",
                    "Шведский",
                    "Шотландский (гэльский)",
                    "Эсперанто",
                    "Эстонский",
                    "Яванский",
                    "Японский"
            };
        }

        @Override
        public Map<String, String> getTagIndex() {
            return new HashMap<String, String>(){{
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
        }
    };

    public static final Languages.Data DEFAULT_DATA_EN = new Languages.Data() {
        @Override
        public String[] getSortedLanguages() {
            return new String[] {
                    "Afrikaans",
                    "Albanian",
                    "Amharic",
                    "Arabic",
                    "Armenian",
                    "Azerbaijani",
                    "Bashkir",
                    "Basque",
                    "Belarusian",
                    "Bengali",
                    "Bosnian",
                    "Bulgarian",
                    "Burmese",
                    "Catalan",
                    "Cebuano",
                    "Chinese",
                    "Croatian",
                    "Czech",
                    "Danish",
                    "Dutch",
                    "English",
                    "Esperanto",
                    "Estonian",
                    "Finnish",
                    "French",
                    "Galician",
                    "Georgian",
                    "German",
                    "Greek",
                    "Gujarati",
                    "Haitian",
                    "Hebrew",
                    "Hill Mari",
                    "Hindi",
                    "Hungarian",
                    "Icelandic",
                    "Indonesian",
                    "Irish",
                    "Italian",
                    "Japanese",
                    "Javanese",
                    "Kannada",
                    "Kazakh",
                    "Khmer",
                    "Korean",
                    "Kyrgyz",
                    "Lao",
                    "Latin",
                    "Latvian",
                    "Lithuanian",
                    "Luxembourgish",
                    "Macedonian",
                    "Malagasy",
                    "Malay",
                    "Malayalam",
                    "Maltese",
                    "Maori",
                    "Marathi",
                    "Mari",
                    "Mongolian",
                    "Nepali",
                    "Norwegian",
                    "Papiamento",
                    "Persian",
                    "Polish",
                    "Portuguese",
                    "Punjabi",
                    "Romanian",
                    "Russian",
                    "Scottish Gaelic",
                    "Serbian",
                    "Sinhalese",
                    "Slovak",
                    "Slovenian",
                    "Spanish",
                    "Sundanese",
                    "Swahili",
                    "Swedish",
                    "Tagalog",
                    "Tajik",
                    "Tamil",
                    "Tatar",
                    "Telugu",
                    "Thai",
                    "Turkish",
                    "Udmurt",
                    "Ukrainian",
                    "Urdu",
                    "Uzbek",
                    "Vietnamese",
                    "Welsh",
                    "Xhosa",
                    "Yiddish"
            };
        }

        @Override
        public Map<String, String> getTagIndex() {
            return new HashMap<String, String>(){{
                put("Slovak", "sk");
                put("Russian", "ru");
                put("Bosnian", "bs");
                put("Hebrew", "he");
                put("Bashkir", "ba");
                put("Belarusian", "be");
                put("Serbian", "sr");
                put("Marathi", "mr");
                put("Mongolian", "mn");
                put("Swedish", "sv");
                put("Turkish", "tr");
                put("Ukrainian", "uk");
                put("Tajik", "tg");
                put("Arabic", "ar");
                put("Kannada", "kn");
                put("Portuguese", "pt");
                put("Haitian", "ht");
                put("Catalan", "ca");
                put("Hindi", "hi");
                put("Malay", "ms");
                put("Spanish", "es");
                put("Thai", "th");
                put("Norwegian", "no");
                put("Malagasy", "mg");
                put("Armenian", "hy");
                put("Hill Mari", "mrj");
                put("Urdu", "ur");
                put("Swahili", "sw");
                put("Romanian", "ro");
                put("Yiddish", "yi");
                put("Macedonian", "mk");
                put("Japanese", "ja");
                put("Estonian", "et");
                put("Irish", "ga");
                put("Bengali", "bn");
                put("Kazakh", "kk");
                put("Burmese", "my");
                put("Javanese", "jv");
                put("Icelandic", "is");
                put("Gujarati", "gu");
                put("Luxembourgish", "lb");
                put("Tatar", "tt");
                put("Latin", "la");
                put("Italian", "it");
                put("Sundanese", "su");
                put("Uzbek", "uz");
                put("Sinhalese", "si");
                put("Afrikaans", "af");
                put("Persian", "fa");
                put("German", "de");
                put("Slovenian", "sl");
                put("Mari", "mhr");
                put("Azerbaijani", "az");
                put("Telugu", "te");
                put("Papiamento", "pap");
                put("French", "fr");
                put("Maltese", "mt");
                put("Indonesian", "id");
                put("Esperanto", "eo");
                put("Tagalog", "tl");
                put("Maori", "mi");
                put("Nepali", "ne");
                put("Udmurt", "udm");
                put("Finnish", "fi");
                put("Georgian", "ka");
                put("Cebuano", "ceb");
                put("Albanian", "sq");
                put("Basque", "eu");
                put("Malayalam", "ml");
                put("Polish", "pl");
                put("Lithuanian", "lt");
                put("Xhosa", "xh");
                put("Latvian", "lv");
                put("Bulgarian", "bg");
                put("Korean", "ko");
                put("Welsh", "cy");
                put("Khmer", "km");
                put("Tamil", "ta");
                put("Croatian", "hr");
                put("English", "en");
                put("Chinese", "zh");
                put("Vietnamese", "vi");
                put("Scottish Gaelic", "gd");
                put("Danish", "da");
                put("Hungarian", "hu");
                put("Czech", "cs");
                put("Lao", "lo");
                put("Amharic", "am");
                put("Galician", "gl");
                put("Greek", "el");
                put("Dutch", "nl");
                put("Kyrgyz", "ky");
                put("Punjabi", "pa");
            }};
        }
    };
}
