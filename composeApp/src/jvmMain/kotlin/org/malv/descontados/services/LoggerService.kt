package org.malv.descontados.services

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoggerService {

    private val _log = MutableStateFlow("")
    val log: StateFlow<String> = _log.asStateFlow()

    fun info(message: String) {
        print("INFO: $message")
        _log.value += message
    }

    fun debug(message: String) {
        println("DEBUG: $message")
    }

    fun clean() {
        _log.value = ""
    }

    companion object {
        val instance = LoggerService()
    }
}
