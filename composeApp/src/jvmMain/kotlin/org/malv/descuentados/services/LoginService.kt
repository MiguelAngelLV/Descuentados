package org.malv.descuentados.services

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader

class LoginService(
    private val file: String,
    private val configurationService: ConfigurationService
) {

    private var flow = getFlow()
    private var receiver: LocalServerReceiver? = null

    private fun getFlow(): GoogleAuthorizationCodeFlow? {
        val httpTransport = NetHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()
        val clientSecretsFile = File(file)

        if (!clientSecretsFile.exists()) return null

        val dataDirectory = configurationService.getDirectory(CONFIG)
        dataDirectory.mkdirs()

        val clientSecrets = GoogleClientSecrets.load(jsonFactory, FileReader(clientSecretsFile))

        return GoogleAuthorizationCodeFlow.Builder(
            httpTransport, jsonFactory, clientSecrets,
            listOf("https://www.googleapis.com/auth/youtube.force-ssl")
        ).setDataStoreFactory(FileDataStoreFactory(dataDirectory)).build()
    }

    fun getCredentials(): Credential? {
        return flow?.loadCredential("user")?.takeIf { it.accessToken != null }
    }

    fun getAuthorizationUrl(): String {
        val receiver = LocalServerReceiver.Builder().setPort(PORT).build()

        val url = flow?.newAuthorizationUrl()
            ?.setRedirectUri(receiver.redirectUri)
            ?.build() ?: ""
        receiver.stop()
        return url
    }

    suspend fun requestLogin(): Credential = withContext(Dispatchers.IO) {
        receiver?.stop()
        val url = getAuthorizationUrl()
        DesktopService.browse(url)
        receiver = LocalServerReceiver.Builder().setPort(PORT).build()
        AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    fun isLoggedIn(): Boolean {
        if (!File(file).exists()) return false
        return getCredentials()?.accessToken != null
    }

    fun logout() {
        flow?.credentialDataStore?.delete("user")
    }

    companion object {
        private const val CONFIG = "credentials"
        private const val PORT = 8080
    }

    class InvalidOSError(message: String) : RuntimeException(message)
}
