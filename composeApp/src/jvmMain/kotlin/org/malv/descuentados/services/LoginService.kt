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
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader

class LoginService(
    private val file: String,
    private val configurationService: ConfigurationService
) {
    private val logger = LoggerFactory.getLogger(LoginService::class.java)

    private var flow = getFlow()
    private var receiver: LocalServerReceiver? = null

    private fun getFlow(): GoogleAuthorizationCodeFlow? {
        logger.info("Inicializando flujo de autenticación OAuth2")
        val httpTransport = NetHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()
        val clientSecretsFile = File(file)

        if (!clientSecretsFile.exists()) {
            logger.error("Archivo de client secrets no encontrado: $file")
            return null
        }

        logger.debug("Archivo de client secrets encontrado: $file")
        val dataDirectory = configurationService.getDirectory(CONFIG)
        dataDirectory.mkdirs()
        logger.debug("Directorio de credenciales: ${dataDirectory.absolutePath}")

        val clientSecrets = GoogleClientSecrets.load(jsonFactory, FileReader(clientSecretsFile))

        return GoogleAuthorizationCodeFlow.Builder(
            httpTransport, jsonFactory, clientSecrets,
            listOf("https://www.googleapis.com/auth/youtube.force-ssl")
        ).setDataStoreFactory(FileDataStoreFactory(dataDirectory)).build()
    }

    fun getCredentials(): Credential? {
        logger.debug("Obteniendo credenciales almacenadas")
        val credential = flow?.loadCredential("user")?.takeIf { it.accessToken != null }
        if (credential != null) {
            logger.debug("Credenciales encontradas y válidas")
        } else {
            logger.debug("No hay credenciales válidas almacenadas")
        }
        return credential
    }

    fun getAuthorizationUrl(): String {
        logger.info("Generando URL de autorización")
        val receiver = LocalServerReceiver.Builder().setPort(PORT).build()

        val url = flow?.newAuthorizationUrl()
            ?.setRedirectUri(receiver.redirectUri)
            ?.build() ?: ""
        receiver.stop()
        logger.debug("URL de autorización generada: $url")
        return url
    }

    suspend fun requestLogin(): Credential = withContext(Dispatchers.IO) {
        logger.info("Iniciando proceso de login OAuth2")
        receiver?.stop()
        val url = getAuthorizationUrl()
        logger.info("Abriendo navegador para autorización")
        DesktopService.browse(url)
        receiver = LocalServerReceiver.Builder().setPort(PORT).build()
        logger.debug("Esperando respuesta de autorización en puerto $PORT")
        val credential = AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
        logger.info("Login exitoso, credenciales obtenidas")
        credential
    }

    fun isLoggedIn(): Boolean {
        val fileExists = File(file).exists()
        if (!fileExists) {
            logger.debug("Archivo de client secrets no existe, no se puede estar logueado")
            return false
        }
        val hasCredentials = getCredentials()?.accessToken != null
        logger.debug("Estado de login: ${if (hasCredentials) "logueado" else "no logueado"}")
        return hasCredentials
    }

    fun logout() {
        logger.info("Cerrando sesión y eliminando credenciales")
        flow?.credentialDataStore?.delete("user")
        logger.info("Sesión cerrada exitosamente")
    }

    companion object {
        private const val CONFIG = "credentials"
        private const val PORT = 8080
    }

    class InvalidOSError(message: String) : RuntimeException(message)
}
