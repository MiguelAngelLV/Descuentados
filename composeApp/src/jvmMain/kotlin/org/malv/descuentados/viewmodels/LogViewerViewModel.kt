package org.malv.descuentados.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.malv.descuentados.utils.LogConfig
import org.slf4j.LoggerFactory

class LogViewerViewModel : ViewModel() {
    private val logger = LoggerFactory.getLogger(LogViewerViewModel::class.java)

    private val _logText = MutableStateFlow("")
    val logText: StateFlow<String> = _logText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        logger.debug("Inicializando LogViewerViewModel")
        refreshLogs()
    }

    fun refreshLogs() {
        viewModelScope.launch {
            try {
                val newText = LogConfig.readLogFile()

                if (newText != _logText.value) {
                    _logText.value = newText
                }
            } catch (e: Exception) {
                logger.error("Error al cargar los logs", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
