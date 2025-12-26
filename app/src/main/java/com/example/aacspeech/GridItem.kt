package com.example.aacspeech

data class GridItem(
    val id: String,
    val text: String,
    val backgroundColor: Int,
    val textEn: String? = null,
    val textEs: String? = null,
    val textDe: String? = null
) {
    fun getTextForLanguage(language: String): String {
        return when (language) {
            "en" -> textEn ?: text
            "es" -> textEs ?: text
            "de" -> textDe ?: text
            else -> text
        }
    }
}
