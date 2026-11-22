package com.example.medicare_assistant.profile.data

data class Allergy(
    val name: String,
    val reaction: String = ""
)

data class UserProfile(
    val name: String = "",
    val age: Int? = null,
    val phone: String = "",
    val address: String = "",
    val locationText: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationUpdatedAt: Long? = null,
    val photoPath: String? = null,
    val allergies: List<Allergy> = emptyList()
) {
    val isEmpty: Boolean
        get() = name.isBlank() && age == null && phone.isBlank() && address.isBlank() &&
                locationText.isBlank() && photoPath == null && allergies.isEmpty()

    val initial: String?
        get() = name.trim().firstOrNull()?.uppercaseChar()?.toString()
}