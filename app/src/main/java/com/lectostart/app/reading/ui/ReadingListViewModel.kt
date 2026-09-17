package com.lectostart.app.reading.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lectostart.app.reading.data.ReadingEntity
import com.lectostart.app.reading.data.ReadingImporter
import com.lectostart.app.reading.data.ReadingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ReadingListViewModel @Inject constructor(
  readingRepository: ReadingRepository,
  private val readingImporter: ReadingImporter,
) : ViewModel() {
  val readings: StateFlow<List<ReadingEntity>> =
    readingRepository.observeReadings().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  /** Evento de un solo disparo para mostrar errores de importación (patrón de ProgressViewModel, T-025). */
  private val _importError = MutableSharedFlow<String>()
  val importError: SharedFlow<String> = _importError.asSharedFlow()

  fun onFileSelected(uri: Uri) {
    viewModelScope.launch {
      when (val result = readingImporter.import(uri)) {
        is ReadingImporter.Result.Error -> _importError.emit(result.message)
        is ReadingImporter.Result.Success -> Unit
      }
    }
  }
}
