package com.example.medicare_assistant.records.logic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare_assistant.records.data.RecordsRepository
import com.example.medicare_assistant.records.view.RecordDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecordDetailViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = RecordsRepository.get(app)
    private val recordId = MutableStateFlow<Long?>(null)

    val state: StateFlow<RecordDetailUiState> = combine(repo.records, recordId) { all, id ->
        if (id == null) {
            RecordDetailUiState()
        } else {
            RecordDetailUiState(loaded = true, record = all.firstOrNull { it.id == id })
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, RecordDetailUiState())

    fun load(id: Long) {
        recordId.value = id
    }

    fun delete() {
        val id = recordId.value ?: return
        viewModelScope.launch {
            val record = repo.find(id)
            repo.delete(id)
            RecordImageHelper.delete(record?.imagePath)
        }
    }
}