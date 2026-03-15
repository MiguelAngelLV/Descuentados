package org.malv.descuentados.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.malv.descuentados.models.Language
import org.malv.descuentados.services.ConfigurationService

class CodesViewModel(private val configuration: ConfigurationService) : ViewModel() {

    private val _codes = MutableStateFlow(configuration.codes)
    val codes: StateFlow<String> = _codes.asStateFlow()

    private val _languages = MutableStateFlow(configuration.languages)
    val languages: StateFlow<Map<String, Language>> = _languages.asStateFlow()

    fun updateCodes(input: String) {
        _codes.value = input
        configuration.codes = input
    }

    fun updateLanguage(language: Language) {
        val updated = configuration.languages + (language.code to language)
        configuration.languages = updated
        _languages.value = updated
    }

    fun addLanguage(language: Language) {
        val updated = configuration.languages + (language.code to language)
        configuration.languages = updated
        _languages.value = updated
    }

    fun deleteLanguage(code: String) {
        val updated = configuration.languages - code
        configuration.languages = updated
        _languages.value = updated
    }
}
