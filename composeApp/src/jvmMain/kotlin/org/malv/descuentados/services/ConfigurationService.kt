package org.malv.descuentados.services

import com.google.gson.GsonBuilder
import org.malv.descuentados.models.Configuration
import org.malv.descuentados.models.Language
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths

class ConfigurationService {
    private val logger = LoggerFactory.getLogger(ConfigurationService::class.java)
    private val configDir = userDirectory()
    private val configFile = File(configDir, "config.json")

    private val configuration = load()

    private fun load(): Configuration {
        logger.info("Cargando configuración desde: ${configFile.absolutePath}")
        configDir.mkdirs()
        if (configFile.exists()) {
            logger.debug("Archivo de configuración encontrado")
            configFile.reader().use { reader ->
                val gson = GsonBuilder().setPrettyPrinting().create()
                val config = gson.fromJson(reader, Configuration::class.java) ?: Configuration()
                logger.info("Configuración cargada exitosamente")
                logger.debug("Idiomas configurados: ${config.languages.keys}")
                return config
            }
        }
        logger.warn("Archivo de configuración no encontrado, usando configuración por defecto")
        return Configuration()
    }

    fun getDirectory(name: String): File {
        val dir = File(configDir, name)
        logger.debug("Obteniendo directorio: ${dir.absolutePath}")
        return dir
    }

    fun save() {
        logger.info("Guardando configuración en: ${configFile.absolutePath}")
        if (!configFile.parentFile.exists()) {
            logger.debug("Creando directorio de configuración")
            configFile.parentFile.mkdirs()
        }
        configFile.writer().use { writer ->
            val gson = GsonBuilder().setPrettyPrinting().create()
            val json = gson.toJson(configuration)
            writer.write(json)
        }
        logger.info("Configuración guardada exitosamente")
        logger.debug("Idiomas guardados: ${configuration.languages.keys}")
    }

    var codes: String
        get() = configuration.codes
        set(value) {
            logger.debug("Actualizando códigos (longitud: ${value.length})")
            configuration.codes = value
        }

    var languages: Map<String, Language>
        get() = configuration.languages
        set(value) {
            logger.info("Actualizando idiomas: ${value.keys}")
            configuration.languages = value
        }

    var clientSecretPath: String
        get() = configuration.clientSecretPath
        set(value) {
            logger.info("Actualizando ruta de client secret: $value")
            configuration.clientSecretPath = value
        }

    private fun userDirectory(): File {
        val os = System.getProperty("os.name").lowercase()

        val dir = when {
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

        logger.debug("Directorio de configuración: ${dir.absolutePath}")
        return dir
    }

    companion object {
        private const val APP_DIRECTORY = "descuentados"

        val instance = ConfigurationService()
    }
}
