package com.example.medicare_assistant.settings

import androidx.annotation.StringRes
import com.example.medicare_assistant.R
import java.util.Locale

enum class ThemeMode(@StringRes val labelRes: Int) {
    SYSTEM(R.string.theme_system),
    LIGHT(R.string.theme_light),
    DARK(R.string.theme_dark)
}

enum class TextSize(val scale: Float, @StringRes val labelRes: Int) {
    NORMAL(1.0f, R.string.text_size_normal),
    LARGE(1.15f, R.string.text_size_large),
    EXTRA_LARGE(1.3f, R.string.text_size_extra_large)
}

enum class AppLanguage(val tag: String, val nativeName: String) {
    ENGLISH("en", "English"),
    SINHALA("si", "සිංහල"),
    TAMIL("ta", "தமிழ்");

    companion object {
        fun fromTag(tag: String?): AppLanguage =
            entries.firstOrNull { it.tag == tag }
                ?: entries.firstOrNull { it.tag == Locale.getDefault().language }
                ?: ENGLISH
    }
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val textSize: TextSize = TextSize.NORMAL
)