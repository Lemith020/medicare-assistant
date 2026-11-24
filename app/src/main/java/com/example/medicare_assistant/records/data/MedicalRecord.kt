package com.example.medicare_assistant.records.data

import androidx.annotation.StringRes
import com.example.medicare_assistant.R
import java.time.LocalDate

enum class RecordCategory(@StringRes val labelRes: Int) {
    RECEIPT(R.string.record_category_receipt),
    REPORT(R.string.record_category_report)
}

data class MedicalRecord(
    val id: Long,
    val category: RecordCategory,
    val title: String,
    val source: String,
    val date: LocalDate,
    val notes: String = "",
    val imagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)