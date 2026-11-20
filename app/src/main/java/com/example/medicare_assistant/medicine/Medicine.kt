package com.example.medicare_assistant.medicine

import java.time.LocalTime

data class Medicine(
    val id: Int,
    val name: String,
    val dosage: String,
    val times: List<LocalTime>,
    val stock: Int,
    val lowStockThreshold: Int = 5
) {
    val isLowStock: Boolean get() = stock <= lowStockThreshold
}