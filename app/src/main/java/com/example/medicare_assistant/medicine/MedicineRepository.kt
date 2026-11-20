package com.example.medicare_assistant.medicine

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.medicare_assistant.core.appDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime

class MedicineRepository private constructor(private val context: Context) {

    private val key = stringPreferencesKey("medicines_json")

    val medicines: Flow<List<Medicine>> = context.appDataStore.data.map { decode(it[key]) }

    suspend fun add(medicine: Medicine) = modify { it + medicine }

    suspend fun update(medicine: Medicine) = modify { list ->
        list.map { if (it.id == medicine.id) medicine else it }
    }

    suspend fun delete(id: Long) = modify { list -> list.filterNot { it.id == id } }

    suspend fun dismissCompletion(id: Long) = modify { list ->
        list.map { if (it.id == id) it.copy(completionDismissed = true) else it }
    }

    private suspend fun modify(transform: (List<Medicine>) -> List<Medicine>) {
        context.appDataStore.edit { prefs ->
            prefs[key] = encode(transform(decode(prefs[key])))
        }
    }

    private fun encode(list: List<Medicine>): String {
        val array = JSONArray()
        list.forEach { array.put(it.toJson()) }
        return array.toString()
    }

    private fun decode(json: String?): List<Medicine> {
        if (json.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(json)
            List(array.length()) { array.getJSONObject(it).toMedicine() }
        }.getOrDefault(emptyList())
    }

    companion object {
        @Volatile
        private var instance: MedicineRepository? = null

        fun get(context: Context): MedicineRepository =
            instance ?: synchronized(this) {
                instance ?: MedicineRepository(context.applicationContext).also { instance = it }
            }
    }
}

private fun Medicine.toJson(): JSONObject = JSONObject().apply {
    put("id", id)
    put("name", name)
    put("dosage", dosage)
    put("category", category.name)
    put("times", JSONObject().apply { times.forEach { (slot, time) -> put(slot.name, time.toString()) } })
    put("stock", stock)
    put("threshold", lowStockThreshold)
    put("start", startDate.toString())
    put("days", courseDays ?: -1)
    put("dismissed", completionDismissed)
}

private fun JSONObject.toMedicine(): Medicine {
    val timesObject = getJSONObject("times")
    val times = DoseSlot.entries.mapNotNull { slot ->
        timesObject.optString(slot.name).takeIf { it.isNotEmpty() }?.let { slot to LocalTime.parse(it) }
    }.toMap()

    return Medicine(
        id = getLong("id"),
        name = getString("name"),
        dosage = getString("dosage"),
        category = MedicineCategory.entries.firstOrNull { it.name == optString("category") }
            ?: MedicineCategory.OTHER,
        times = times,
        stock = getInt("stock"),
        lowStockThreshold = getInt("threshold"),
        startDate = LocalDate.parse(getString("start")),
        courseDays = getInt("days").takeIf { it > 0 },
        completionDismissed = optBoolean("dismissed", false)
    )
}