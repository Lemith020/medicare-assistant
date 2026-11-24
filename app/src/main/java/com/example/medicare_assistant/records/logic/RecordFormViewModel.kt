package com.example.medicare_assistant.records.logic

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare_assistant.R
import com.example.medicare_assistant.records.data.MedicalRecord
import com.example.medicare_assistant.records.data.RecordCategory
import com.example.medicare_assistant.records.data.RecordsRepository
import com.example.medicare_assistant.records.view.RecordFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class RecordFormViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = RecordsRepository.get(app)
    private val _state = MutableStateFlow(RecordFormUiState())
    val state: StateFlow<RecordFormUiState> = _state.asStateFlow()

    private var started = false
    private var existing: MedicalRecord? = null
    private var pendingPath: String? = null
    private var imageRemoved = false

    fun start(editId: Long?) {
        if (started) return
        started = true

        if (editId == null) {
            _state.value = RecordFormUiState(loaded = true)
            return
        }

        viewModelScope.launch {
            val record = repo.find(editId)
            if (record == null) {
                _state.value = RecordFormUiState(loaded = true)
                return@launch
            }
            existing = record
            _state.value = RecordFormUiState(
                loaded = true,
                editing = true,
                category = record.category,
                title = record.title,
                source = record.source,
                date = record.date,
                notes = record.notes,
                imagePath = record.imagePath
            )
        }
    }

    fun onCategoryChange(value: RecordCategory) = _state.update { it.copy(category = value) }

    fun onTitleChange(value: String) = _state.update { it.copy(title = value.take(80), titleError = null) }

    fun onSourceChange(value: String) = _state.update { it.copy(source = value.take(80), sourceError = null) }

    fun onDateChange(value: LocalDate) = _state.update { it.copy(date = value, dateError = null) }

    fun onNotesChange(value: String) = _state.update { it.copy(notes = value.take(500)) }

    fun onPhotoPicked(uri: Uri) {
        viewModelScope.launch {
            val path = RecordImageHelper.importToPending(getApplication<Application>(), uri)
            if (path != null) {
                pendingPath = path
                imageRemoved = false
                _state.update { it.copy(imagePath = path) }
            }
        }
    }

    fun onRemovePhoto() {
        pendingPath = null
        imageRemoved = true
        _state.update { it.copy(imagePath = null) }
    }

    fun save(onSaved: () -> Unit) {
        val s = _state.value
        if (s.saving) return

        val titleError = if (s.title.isBlank()) R.string.record_error_title else null
        val sourceError = if (s.source.isBlank()) R.string.record_error_source else null
        val dateError = if (s.date.isAfter(LocalDate.now())) R.string.record_error_date else null

        _state.update { it.copy(titleError = titleError, sourceError = sourceError, dateError = dateError) }

        if (listOf(titleError, sourceError, dateError).any { it != null }) return

        _state.update { it.copy(saving = true) }

        viewModelScope.launch {
            val app = getApplication<Application>()
            val old = existing
            val pending = pendingPath

            val image = when {
                pending != null -> RecordImageHelper.commit(app, pending, old?.imagePath)
                imageRemoved -> {
                    RecordImageHelper.delete(old?.imagePath)
                    null
                }
                else -> old?.imagePath
            }

            val record = MedicalRecord(
                id = old?.id ?: System.currentTimeMillis(),
                category = s.category,
                title = s.title.trim(),
                source = s.source.trim(),
                date = s.date,
                notes = s.notes.trim(),
                imagePath = image,
                createdAt = old?.createdAt ?: System.currentTimeMillis()
            )

            if (old != null) repo.update(record) else repo.add(record)
            onSaved()
        }
    }
}