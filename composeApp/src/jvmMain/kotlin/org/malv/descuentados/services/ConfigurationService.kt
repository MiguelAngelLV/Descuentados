package org.malv.descuentados.services

import org.malv.descuentados.models.Language
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Paths
import java.util.Properties

class ConfigurationService {
    private val configDir = userDirectory()
    private val configFile = File(configDir, "app.properties")

    private val properties by lazy { load() }

    private fun load(): Properties {
        configDir.mkdirs()
        migrateOldConfig()
        val file = Properties()
        if (configFile.exists()) {
            FileInputStream(configFile).use {
                file.load(it)
            }
        }
        return file
    }

    fun getDirectory(name: String): File {
        return File(configDir, name)
    }

    fun save() {
        if (!configFile.parentFile.exists()) configFile.parentFile.mkdirs()
        FileOutputStream(configFile).use { properties.store(it, "YouTube App Config") }
    }

    var codes: String
        get() = properties.getProperty("codes", "")
        set(value) {
            properties.setProperty("codes", value)
        }

    var languages: List<Language>
        get() = getCodeLanguages()
        set(value) {
            saveCodeLanguages(value)
        }

    var clientSecretPath: String
        get() = properties.getProperty("client_secret_path", "")
        set(value) {
            properties.setProperty("client_secret_path", value)
        }

    private fun getCodeLanguages(): List<Language> {
        return listOf("Español" to "es-ES", "Inglés" to "en-US")
            .map {
                val template = properties.getProperty("${it.second}_template", DEFAULT_TEMPLATE)
                val start = properties.getProperty("${it.second}_start", DEFAULT_START)
                val end = properties.getProperty("${it.second}_end", DEFAULT_END)
                Language(title = it.first, template = template, start = start, end = end, code = it.second)
            }
    }

    private fun saveCodeLanguages(languages: List<Language>) {
        languages.forEach { language ->
            properties.setProperty("${language.code}_template", language.template)
            properties.setProperty("${language.code}_start", language.start)
            properties.setProperty("${language.code}_end", language.end)
        }
    }

    private fun userDirectory(): File {
        val os = System.getProperty("os.name").lowercase()

        return when {
            os.contains("win") -> {
                val appData = System.getenv("APPDATA") ?: System.getProperty("user.home")
                Paths.get(appData, APP_DIRECTORY).toFile()
            }

            os.contains("mac") -> {
                val home = System.getProperty("user.home")
                Paths.get(home, "Library", "Application Support", APP_DIRECTORY).toFile()
            }

            else -> { // Linux y demás Unix
                val xdg = System.getenv("XDG_CONFIG_HOME")
                val baseDir = xdg ?: Paths.get(System.getProperty("user.home"), ".config").toString()
                Paths.get(baseDir, APP_DIRECTORY).toFile()
            }
        }
    }

    private fun migrateOldConfig() {
        val oldDir = File(configDir.parent, OLD_DIRECTORY)
        if (oldDir.exists()) {
            oldDir.copyRecursively(configDir, overwrite = true)
            oldDir.deleteRecursively()
        }
    }

    companion object {
        private const val DEFAULT_TEMPLATE = "{{discount}}$ en pedidos de {{minOrder}}$: {{code}}"
        private const val DEFAULT_START = "*Descuentos Aliexpress*"
        private const val DEFAULT_END = "*Capítulos*"
        private const val APP_DIRECTORY = "descuentados"
        private const val OLD_DIRECTORY = "descontados"

        val instance = ConfigurationService()
    }
}
