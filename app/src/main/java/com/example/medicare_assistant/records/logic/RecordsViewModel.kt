package com.example.medicare_assistant.records.logic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare_assistant.records.data.MedicalRecord
import com.example.medicare_assistant.records.data.RecordsRepository
import com.example.medicare_assistant.records.view.RecordFilter
import com.example.medicare_assistant.records.view.RecordsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class RecordsViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = RecordsRepository.get(app)
    private val filter = MutableStateFlow(RecordFilter.ALL)
    private val query = MutableStateFlow("")
    private val searching = MutableStateFlow(false)

    val state: StateFlow<RecordsUiState> = combine(repo.records, filter, query, searching) { all, selected, text, isSearching ->
        val needle = text.trim()
        val visible = all
            .filter { selected.category == null || it.category == selected.category }
            .filter {
                needle.isEmpty() ||
                        it.title.contains(needle, ignoreCase = true) ||
                        it.source.contains(needle, ignoreCase = true) ||
                        it.notes.contains(needle, ignoreCase = true)
            }
            .sortedWith(
                compareByDescending<MedicalRecord> { it.date }.thenByDescending { it.createdAt }
            )

        RecordsUiState(
            loaded = true,
            records = visible,
            filter = selected,
            query = text,
            searching = isSearching,
            totalCount = all.size
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, RecordsUiState())

    fun setFilter(value: RecordFilter) {
        filter.value = value
    }

    fun toggleSearch() {
        searching.value = !searching.value
        if (!searching.value) query.value = ""
    }

    fun setQuery(value: String) {
        query.value = value.take(60)
    }
}