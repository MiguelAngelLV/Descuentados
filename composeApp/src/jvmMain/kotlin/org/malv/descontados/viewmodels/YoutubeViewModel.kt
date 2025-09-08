package org.malv.descontados.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.malv.descontados.models.VideoResult
import org.malv.descontados.services.ConfigurationService
import org.malv.descontados.services.LoginService
import org.malv.descontados.services.YoutubeService
import java.io.File

class YoutubeViewModel(
    private val configuration: ConfigurationService
) : ViewModel() {

    private val _clientSecretPath = MutableStateFlow(configuration.clientSecretPath)
    val clientSecretPath: StateFlow<String> = _clientSecretPath.asStateFlow()

    var loginService = LoginService(clientSecretPath.value, ConfigurationService.instance)

    private val _isLogged = MutableStateFlow(loginService.isLoggedIn())
    val isLogged: StateFlow<Boolean> = _isLogged.asStateFlow()

    private val _isFileSelected = MutableStateFlow(File(configuration.clientSecretPath).exists())
    val isFileSelected: StateFlow<Boolean> = _isFileSelected.asStateFlow()

    private val _updating = MutableStateFlow(false)
    val updating: StateFlow<Boolean> = _updating.asStateFlow()

    private val _videos = MutableStateFlow<List<VideoResult>>(emptyList())
    val videos: StateFlow<List<VideoResult>> = _videos.asStateFlow()

    fun updateClientSecretPath(path: String) {
        _clientSecretPath.value = path
        configuration.clientSecretPath = path
        loginService = LoginService(path, ConfigurationService.instance)
        _isLogged.value = loginService.isLoggedIn()
        _isFileSelected.value = File(path).exists()
    }

    fun login() = viewModelScope.launch {
        loginService.requestLogin()
        _isLogged.value = loginService.isLoggedIn()
    }

    fun logout() = viewModelScope.launch {
        loginService.logout()
        _isLogged.value = loginService.isLoggedIn()
    }

    fun updateVideos() = viewModelScope.launch {
        val credentials = loginService.getCredentials() ?: return@launch

        _updating.value = true
        _videos.value = emptyList()
        val youtube = YoutubeService(credentials)
        val videos = youtube.getVideos()
        val aliexpress = videos.filter { it.description.contains("aliexpress", true) }
        val languages = configuration.languages.associateBy { it.code }

        aliexpress.forEach { video ->
            _videos.value += youtube.updateVideo(video, languages, configuration.codes)
        }

        _updating.value = false
    }
}
