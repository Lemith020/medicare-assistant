package com.example.medicare_assistant.emergency

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.medicare_assistant.R
import com.example.medicare_assistant.core.appDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class EmergencyRepository private constructor(private val context: Context) {

    private val customContactsKey = stringPreferencesKey("custom_emergency_contacts")

    fun getContacts(): Flow<List<EmergencyContact>> {
        return context.appDataStore.data.map { prefs ->
            val defaults = listOf(
                EmergencyContact(
                    id = "default_1990",
                    nameRes = R.string.emergency_suwa_seriya,
                    subtitleRes = R.string.emergency_ambulance_sub,
                    phoneNumber = "1990",
                    isDefault = true
                ),
                EmergencyContact(
                    id = "default_119",
                    nameRes = R.string.emergency_police,
                    phoneNumber = "119",
                    isDefault = true
                )
            )
            val rawJson = prefs[customContactsKey].orEmpty()
            defaults + parseContacts(rawJson)
        }
    }

    suspend fun addContact(name: String, phoneNumber: String) {
        val cleanName = name.trim()
        val cleanPhone = phoneNumber.trim()
        if (cleanName.isEmpty() || cleanPhone.isEmpty()) return

        context.appDataStore.edit { prefs ->
            val existing = parseContacts(prefs[customContactsKey].orEmpty()).toMutableList()
            existing.add(
                EmergencyContact(
                    id = UUID.randomUUID().toString(),
                    name = cleanName,
                    phoneNumber = cleanPhone,
                    isDefault = false
                )
            )
            prefs[customContactsKey] = serializeContacts(existing)
        }
    }

    suspend fun deleteContact(id: String) {
        context.appDataStore.edit { prefs ->
            val updated = parseContacts(prefs[customContactsKey].orEmpty())
                .filterNot { it.id == id }
            prefs[customContactsKey] = serializeContacts(updated)
        }
    }

    private fun parseContacts(json: String): List<EmergencyContact> {
        if (json.isBlank()) return emptyList()
        return try {
            val array = JSONArray(json)
            List(array.length()) { index ->
                val obj = array.getJSONObject(index)
                EmergencyContact(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    phoneNumber = obj.getString("phoneNumber"),
                    isDefault = false
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun serializeContacts(list: List<EmergencyContact>): String {
        val array = JSONArray()
        list.forEach { contact ->
            if (!contact.isDefault) {
                val obj = JSONObject().apply {
                    put("id", contact.id)
                    put("name", contact.name)
                    put("phoneNumber", contact.phoneNumber)
                }
                array.put(obj)
            }
        }
        return array.toString()
    }

    companion object {
        @Volatile
        private var instance: EmergencyRepository? = null

        fun get(context: Context): EmergencyRepository {
            return instance ?: synchronized(this) {
                instance ?: EmergencyRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
