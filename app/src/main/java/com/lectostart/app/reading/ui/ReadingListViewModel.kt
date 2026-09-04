package com.lectostart.app.reading.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lectostart.app.reading.data.ReadingEntity
import com.lectostart.app.reading.data.ReadingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ReadingListViewModel @Inject constructor(readingRepository: ReadingRepository) : ViewModel() {
  val readings: StateFlow<List<ReadingEntity>> =
    readingRepository.observeReadings().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
