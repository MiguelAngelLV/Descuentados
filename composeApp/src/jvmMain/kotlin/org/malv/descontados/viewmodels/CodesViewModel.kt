package org.malv.descontados.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.malv.descontados.models.Language
import org.malv.descontados.services.ConfigurationService

class CodesViewModel(private val configuration: ConfigurationService) : ViewModel() {

    private val _codes = MutableStateFlow(configuration.codes)
    val codes: StateFlow<String> = _codes.asStateFlow()

    private val _languages = MutableStateFlow(configuration.languages)
    val languages: StateFlow<List<Language>> = _languages.asStateFlow()

    fun updateCodes(input: String) {
        _codes.value = input
        configuration.codes = input
    }

    fun updateLanguage(language: Language) {
        val updated = configuration.languages.map { if (it.code == language.code) language else it }
        _languages.value = updated
        configuration.languages = updated
    }
}
