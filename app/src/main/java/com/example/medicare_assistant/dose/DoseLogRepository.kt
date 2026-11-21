package com.example.medicare_assistant.dose

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.medicare_assistant.core.appDataStore
import com.example.medicare_assistant.medicine.DoseSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.time.LocalDate

class DoseLogRepository private constructor(private val context: Context) {

    private val storeKey = stringPreferencesKey("dose_log_json")

    val log: Flow<Map<String, DoseStatus>> = context.appDataStore.data.map { decode(it[storeKey]) }

    suspend fun statusOf(key: String): DoseStatus? = log.first()[key]

    suspend fun record(key: String, status: DoseStatus) {
        context.appDataStore.edit { prefs ->
            val updated = decode(prefs[storeKey]).toMutableMap()
            updated[key] = status
            prefs[storeKey] = encode(prune(updated))
        }
    }

    private fun prune(map: Map<String, DoseStatus>): Map<String, DoseStatus> {
        val cutoff = LocalDate.now().minusDays(60).toString()
        return map.filterKeys { it.substringBefore('|') >= cutoff }
    }

    private fun encode(map: Map<String, DoseStatus>): String {
        val obj = JSONObject()
        map.forEach { (key, status) -> obj.put(key, status.name) }
        return obj.toString()
    }

    private fun decode(json: String?): Map<String, DoseStatus> {
        if (json.isNullOrBlank()) return emptyMap()
        return runCatching {
            val obj = JSONObject(json)
            obj.keys().asSequence().mapNotNull { key ->
                DoseStatus.entries.firstOrNull { it.name == obj.getString(key) }?.let { key to it }
            }.toMap()
        }.getOrDefault(emptyMap())
    }

    companion object {
        @Volatile
        private var instance: DoseLogRepository? = null

        fun get(context: Context): DoseLogRepository =
            instance ?: synchronized(this) {
                instance ?: DoseLogRepository(context.applicationContext).also { instance = it }
            }

        fun key(date: LocalDate, medicineId: Long, slot: DoseSlot): String =
            "$date|$medicineId|${slot.name}"
    }
}