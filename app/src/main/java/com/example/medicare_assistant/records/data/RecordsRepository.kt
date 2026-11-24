package com.example.medicare_assistant.records.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.medicare_assistant.core.appDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

class RecordsRepository private constructor(private val context: Context) {

    private val key = stringPreferencesKey("records_json")

    val records: Flow<List<MedicalRecord>> = context.appDataStore.data.map { decode(it[key]) }

    suspend fun find(id: Long): MedicalRecord? = records.first().firstOrNull { it.id == id }

    suspend fun add(record: MedicalRecord) = modify { it + record }

    suspend fun update(record: MedicalRecord) = modify { list ->
        list.map { if (it.id == record.id) record else it }
    }

    suspend fun delete(id: Long) = modify { list -> list.filterNot { it.id == id } }

    private suspend fun modify(transform: (List<MedicalRecord>) -> List<MedicalRecord>) {
        context.appDataStore.edit { prefs ->
            prefs[key] = encode(transform(decode(prefs[key])))
        }
    }

    private fun encode(list: List<MedicalRecord>): String {
        val array = JSONArray()
        list.forEach { record ->
            array.put(
                JSONObject().apply {
                    put("id", record.id)
                    put("category", record.category.name)
                    put("title", record.title)
                    put("source", record.source)
                    put("date", record.date.toString())
                    put("notes", record.notes)
                    record.imagePath?.let { put("image", it) }
                    put("createdAt", record.createdAt)
                }
            )
        }
        return array.toString()
    }

    private fun decode(json: String?): List<MedicalRecord> {
        if (json.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(json)
            List(array.length()) { index ->
                val obj = array.getJSONObject(index)
                MedicalRecord(
                    id = obj.getLong("id"),
                    category = RecordCategory.entries.firstOrNull { it.name == obj.optString("category") }
                        ?: RecordCategory.REPORT,
                    title = obj.optString("title"),
                    source = obj.optString("source"),
                    date = LocalDate.parse(obj.getString("date")),
                    notes = obj.optString("notes"),
                    imagePath = obj.optString("image", "").takeIf { it.isNotEmpty() },
                    createdAt = obj.optLong("createdAt", 0L)
                )
            }
        }.getOrDefault(emptyList())
    }

    companion object {
        @Volatile
        private var instance: RecordsRepository? = null

        fun get(context: Context): RecordsRepository =
            instance ?: synchronized(this) {
                instance ?: RecordsRepository(context.applicationContext).also { instance = it }
            }
    }
}