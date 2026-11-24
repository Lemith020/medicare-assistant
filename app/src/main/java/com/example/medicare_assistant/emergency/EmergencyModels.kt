package com.example.medicare_assistant.emergency

import androidx.annotation.StringRes

data class EmergencyContact(
    val id: String,
    val name: String = "",
    @StringRes val nameRes: Int? = null,
    val subtitle: String? = null,
    @StringRes val subtitleRes: Int? = null,
    val phoneNumber: String,
    val isDefault: Boolean = false
)
