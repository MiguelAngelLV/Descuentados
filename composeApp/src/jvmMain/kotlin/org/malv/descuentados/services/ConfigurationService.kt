package org.malv.descuentados.services

import com.google.gson.GsonBuilder
import org.malv.descuentados.models.Configuration
import org.malv.descuentados.models.Language
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Paths

class ConfigurationService {
    private val configDir = userDirectory()
    private val configFile = File(configDir, "config.json")

    private val configuration = load()

    private fun load(): Configuration {
        configDir.mkdirs()
        if (configFile.exists()) {
            FileInputStream(configFile).use {
                val gson = GsonBuilder().setPrettyPrinting().create()
                return gson.fromJson(FileReader(configFile), Configuration::class.java) ?: Configuration()
            }
        }
        return Configuration()
    }

    fun getDirectory(name: String): File {
        return File(configDir, name)
    }

    fun save() {
        if (!configFile.parentFile.exists()) configFile.parentFile.mkdirs()
        FileOutputStream(configFile).use {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val json = gson.toJson(configuration)
            val writer = FileWriter(configFile)
            writer.write(json)
            writer.flush()
        }
    }

    var codes: String
        get() = configuration.codes
        set(value) {
            configuration.codes = value
        }

    var languages: Map<String, Language>
        get() = configuration.languages
        set(value) {
            configuration.languages = value
        }

    var clientSecretPath: String
        get() = configuration.clientSecretPath
        set(value) {
            configuration.clientSecretPath = value
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

    companion object {
        private const val APP_DIRECTORY = "descuentados"

        val instance = ConfigurationService()
    }
}
