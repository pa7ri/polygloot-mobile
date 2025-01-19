package com.polygloot.mobile.android.ui.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import java.util.AbstractMap.SimpleEntry

class Consts {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences.settings")

        const val REQUEST_PERMISSIONS = 200
        const val REQUEST_CODE_GOOGLE_PLAY_SERVICES = 1002
        const val REQUEST_CODE_LOCATION_SETTINGS = 1003

        const val EXTRAS_LOGIN_USERNAME = "extras.login.username"
        const val AUDIO_INPUT_FILE_NAME = "audio_input.mp3"
        const val AUDIO_OUTPUT_FILE_NAME = "audio_output.mp3"
        const val AUDIO_RESPONSE_NAME = "audio_output.mp3"

        const val PREFERENCES_SETTINGS = "preferences.settings"
        const val PREFERENCES_SETTINGS_SELECTED_LANGUAGES_KEY = "selected.languages"

        const val PREFERENCES_LOGIN = "preferences.login"
        const val PREFERENCES_LOGIN_REMEMBER_CHECKED = "remember.checked"
        const val PREFERENCES_LOGIN_REMEMBER_USERNAME = "remember.username"
        const val PREFERENCES_LOGIN_REMEMBER_PASSWORD = "remember.password"


        const val SILENCE_DETECTOR_THRESHOLD = 2000
        const val SILENCE_DETECTOR_DURATION = 1000L

        fun countryToSupportedLanguage(countryCode: String): Map.Entry<String, String> {
            val languageCode = COUNTRY_TO_LANGUAGE.getOrDefault(countryCode, "en")
            return SimpleEntry(languageCode, SUPPORTED_LANGUAGES.getOrDefault(languageCode, "English"))
        }

        /**
         * Supported languages for translation in OpenAI API
         * https://platform.openai.com/docs/guides/speech-to-text#supported-languages
         */
        val SUPPORTED_LANGUAGES: Map<String, String> = mapOf(
            "af" to "Afrikaans",
            "ar" to "Arabic",
            "hy" to "Armenian",
            "az" to "Azerbaijani",
            "be" to "Belarusian",
            "bs" to "Bosnian",
            "bg" to "Bulgarian",
            "ca" to "Catalan",
            "zh" to "Chinese",
            "hr" to "Croatian",
            "cs" to "Czech",
            "da" to "Danish",
            "nl" to "Dutch",
            "en" to "English",
            "et" to "Estonian",
            "fi" to "Finnish",
            "fr" to "French",
            "gl" to "Galician",
            "de" to "German",
            "el" to "Greek",
            "he" to "Hebrew",
            "hi" to "Hindi",
            "hu" to "Hungarian",
            "is" to "Icelandic",
            "id" to "Indonesian",
            "it" to "Italian",
            "ja" to "Japanese",
            "kn" to "Kannada",
            "kk" to "Kazakh",
            "ko" to "Korean",
            "lv" to "Latvian",
            "lt" to "Lithuanian",
            "mk" to "Macedonian",
            "ms" to "Malay",
            "mr" to "Marathi",
            "mi" to "Maori",
            "ne" to "Nepali",
            "no" to "Norwegian",
            "fa" to "Persian",
            "pl" to "Polish",
            "pt" to "Portuguese",
            "ro" to "Romanian",
            "ru" to "Russian",
            "sr" to "Serbian",
            "sk" to "Slovak",
            "sl" to "Slovenian",
            "es" to "Spanish",
            "sw" to "Swahili",
            "sv" to "Swedish",
            "tl" to "Tagalog",
            "ta" to "Tamil",
            "th" to "Thai",
            "tr" to "Turkish",
            "uk" to "Ukrainian",
            "ur" to "Urdu",
            "vi" to "Vietnamese",
            "cy" to "Welsh"
        )

        val COUNTRY_TO_LANGUAGE: Map<String, String> = mapOf(
            // Countries with supported languages
            "AF" to "fa",   // Afghanistan -> Persian (Dari)
            "AL" to "sq",   // Albania -> Albanian
            "DZ" to "ar",   // Algeria -> Arabic
            "AO" to "pt",   // Angola -> Portuguese
            "AR" to "es",   // Argentina -> Spanish
            "AM" to "hy",   // Armenia -> Armenian
            "AU" to "en",   // Australia -> English
            "AT" to "de",   // Austria -> German
            "AZ" to "az",   // Azerbaijan -> Azerbaijani
            "BD" to "bn",   // Bangladesh -> Bengali
            "BY" to "be",   // Belarus -> Belarusian
            "BE" to "nl",   // Belgium -> Dutch
            "BJ" to "fr",   // Benin -> French
            "BT" to "dz",   // Bhutan -> Dzongkha
            "BO" to "es",   // Bolivia -> Spanish
            "BA" to "bs",   // Bosnia and Herzegovina -> Bosnian
            "BW" to "en",   // Botswana -> English
            "BR" to "pt",   // Brazil -> Portuguese
            "BG" to "bg",   // Bulgaria -> Bulgarian
            "BF" to "fr",   // Burkina Faso -> French
            "BI" to "fr",   // Burundi -> French
            "KH" to "km",   // Cambodia -> Khmer
            "CM" to "fr",   // Cameroon -> French
            "CA" to "en",   // Canada -> English
            "CV" to "pt",   // Cape Verde -> Portuguese
            "CF" to "fr",   // Central African Republic -> French
            "TD" to "fr",   // Chad -> French
            "CL" to "es",   // Chile -> Spanish
            "CN" to "zh",   // China -> Chinese
            "CO" to "es",   // Colombia -> Spanish
            "KM" to "ar",   // Comoros -> Arabic
            "CG" to "fr",   // Congo -> French
            "CD" to "fr",   // Democratic Republic of the Congo -> French
            "CR" to "es",   // Costa Rica -> Spanish
            "HR" to "hr",   // Croatia -> Croatian
            "CU" to "es",   // Cuba -> Spanish
            "CY" to "el",   // Cyprus -> Greek
            "CZ" to "cs",   // Czech Republic -> Czech
            "DK" to "da",   // Denmark -> Danish
            "DJ" to "fr",   // Djibouti -> French
            "DO" to "es",   // Dominican Republic -> Spanish
            "EC" to "es",   // Ecuador -> Spanish
            "EG" to "ar",   // Egypt -> Arabic
            "SV" to "es",   // El Salvador -> Spanish
            "EE" to "et",   // Estonia -> Estonian
            "ET" to "am",   // Ethiopia -> Amharic
            "FI" to "fi",   // Finland -> Finnish
            "FR" to "fr",   // France -> French
            "GA" to "fr",   // Gabon -> French
            "GM" to "en",   // Gambia -> English
            "GE" to "ka",   // Georgia -> Georgian
            "DE" to "de",   // Germany -> German
            "GH" to "en",   // Ghana -> English
            "GR" to "el",   // Greece -> Greek
            "GT" to "es",   // Guatemala -> Spanish
            "GN" to "fr",   // Guinea -> French
            "GY" to "en",   // Guyana -> English
            "HT" to "fr",   // Haiti -> French
            "HN" to "es",   // Honduras -> Spanish
            "HU" to "hu",   // Hungary -> Hungarian
            "IS" to "is",   // Iceland -> Icelandic
            "IN" to "hi",   // India -> Hindi
            "ID" to "id",   // Indonesia -> Indonesian
            "IR" to "fa",   // Iran -> Persian
            "IQ" to "ar",   // Iraq -> Arabic
            "IE" to "en",   // Ireland -> English
            "IL" to "he",   // Israel -> Hebrew
            "IT" to "it",   // Italy -> Italian
            "JM" to "en",   // Jamaica -> English
            "JP" to "ja",   // Japan -> Japanese
            "JO" to "ar",   // Jordan -> Arabic
            "KZ" to "kk",   // Kazakhstan -> Kazakh
            "KE" to "sw",   // Kenya -> Swahili
            "KR" to "ko",   // South Korea -> Korean
            "KW" to "ar",   // Kuwait -> Arabic
            "KG" to "ky",   // Kyrgyzstan -> Kyrgyz
            "LA" to "lo",   // Laos -> Lao
            "LV" to "lv",   // Latvia -> Latvian
            "LB" to "ar",   // Lebanon -> Arabic
            "LS" to "en",   // Lesotho -> English
            "LR" to "en",   // Liberia -> English
            "LT" to "lt",   // Lithuania -> Lithuanian
            "LU" to "fr",   // Luxembourg -> French
            "MG" to "fr",   // Madagascar -> French
            "MW" to "en",   // Malawi -> English
            "MY" to "ms",   // Malaysia -> Malay
            "MV" to "dv",   // Maldives -> Dhivehi
            "ML" to "fr",   // Mali -> French
            "MT" to "mt",   // Malta -> Maltese
            "MX" to "es",   // Mexico -> Spanish
            "MN" to "mn",   // Mongolia -> Mongolian
            "ME" to "sr",   // Montenegro -> Serbian
            "MA" to "ar",   // Morocco -> Arabic
            "MZ" to "pt",   // Mozambique -> Portuguese
            "MM" to "my",   // Myanmar -> Burmese
            "NA" to "en",   // Namibia -> English
            "NP" to "ne",   // Nepal -> Nepali
            "NL" to "nl",   // Netherlands -> Dutch
            "NZ" to "en",   // New Zealand -> English
            "NI" to "es",   // Nicaragua -> Spanish
            "NG" to "en",   // Nigeria -> English
            "NO" to "no",   // Norway -> Norwegian
            "OM" to "ar",   // Oman -> Arabic
            "PK" to "ur",   // Pakistan -> Urdu
            "PA" to "es",   // Panama -> Spanish
            "PY" to "es",   // Paraguay -> Spanish
            "PE" to "es",   // Peru -> Spanish
            "PH" to "tl",   // Philippines -> Tagalog
            "PL" to "pl",   // Poland -> Polish
            "PT" to "pt",   // Portugal -> Portuguese
            "QA" to "ar",   // Qatar -> Arabic
            "RO" to "ro",   // Romania -> Romanian
            "RU" to "ru",   // Russia -> Russian
            "RW" to "fr",   // Rwanda -> French
            "SA" to "ar",   // Saudi Arabia -> Arabic
            "SN" to "fr",   // Senegal -> French
            "RS" to "sr",   // Serbia -> Serbian
            "SG" to "en",   // Singapore -> English
            "SK" to "sk",   // Slovakia -> Slovak
            "SI" to "sl",   // Slovenia -> Slovenian
            "ZA" to "af",   // South Africa -> Afrikaans
            "ES" to "es",   // Spain -> Spanish
            "LK" to "ta",   // Sri Lanka -> Tamil
            "SD" to "ar",   // Sudan -> Arabic
            "SE" to "sv",   // Sweden -> Swedish
            "CH" to "de",   // Switzerland -> German
            "SY" to "ar",   // Syria -> Arabic
            "TW" to "zh",   // Taiwan -> Chinese
            "TZ" to "sw",   // Tanzania -> Swahili
            "TH" to "th",   // Thailand -> Thai
            "TR" to "tr",   // Turkey -> Turkish
            "UG" to "en",   // Uganda -> English
            "UA" to "uk",   // Ukraine -> Ukrainian
            "AE" to "ar",   // United Arab Emirates -> Arabic
            "GB" to "en",   // United Kingdom -> English
            "US" to "en",   // United States -> English
            "UY" to "es",   // Uruguay -> Spanish
            "UZ" to "uz",   // Uzbekistan -> Uzbek
            "VE" to "es",   // Venezuela -> Spanish
            "VN" to "vi",   // Vietnam -> Vietnamese
            "YE" to "ar",   // Yemen -> Arabic
            "ZM" to "en",   // Zambia -> English
            "ZW" to "en"    // Zimbabwe -> English
        )
    }
}