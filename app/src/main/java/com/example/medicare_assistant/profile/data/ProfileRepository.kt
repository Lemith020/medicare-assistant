package com.example.medicare_assistant.profile.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.medicare_assistant.core.appDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class ProfileRepository private constructor(private val context: Context) {

    private val key = stringPreferencesKey("profile_json")

    val profile: Flow<UserProfile> = context.appDataStore.data.map { decode(it[key]) }

    suspend fun update(transform: (UserProfile) -> UserProfile) {
        context.appDataStore.edit { prefs ->
            prefs[key] = encode(transform(decode(prefs[key])))
        }
    }

    private fun encode(profile: UserProfile): String {
        val allergies = JSONArray()
        profile.allergies.forEach {
            allergies.put(JSONObject().put("name", it.name).put("reaction", it.reaction))
        }
        return JSONObject().apply {
            put("name", profile.name)
            put("age", profile.age ?: -1)
            put("phone", profile.phone)
            put("address", profile.address)
            put("locationText", profile.locationText)
            profile.latitude?.let { put("lat", it) }
            profile.longitude?.let { put("lng", it) }
            profile.locationUpdatedAt?.let { put("locationAt", it) }
            profile.photoPath?.let { put("photo", it) }
            put("allergies", allergies)
        }.toString()
    }

    private fun decode(json: String?): UserProfile {
        if (json.isNullOrBlank()) return UserProfile()
        return runCatching {
            val obj = JSONObject(json)
            val allergyArray = obj.optJSONArray("allergies")
            UserProfile(
                name = obj.optString("name"),
                age = obj.optInt("age", -1).takeIf { it >= 0 },
                phone = obj.optString("phone"),
                address = obj.optString("address"),
                locationText = obj.optString("locationText"),
                latitude = if (obj.has("lat")) obj.getDouble("lat") else null,
                longitude = if (obj.has("lng")) obj.getDouble("lng") else null,
                locationUpdatedAt = if (obj.has("locationAt")) obj.getLong("locationAt") else null,
                photoPath = obj.optString("photo", "").takeIf { it.isNotEmpty() },
                allergies = allergyArray?.let { array ->
                    List(array.length()) { index ->
                        val item = array.getJSONObject(index)
                        Allergy(item.optString("name"), item.optString("reaction"))
                    }
                } ?: emptyList()
            )
        }.getOrDefault(UserProfile())
    }

    companion object {
        @Volatile
        private var instance: ProfileRepository? = null

        fun get(context: Context): ProfileRepository =
            instance ?: synchronized(this) {
                instance ?: ProfileRepository(context.applicationContext).also { instance = it }
            }
    }
}