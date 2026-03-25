package org.malv.descuentados.utils

import java.io.File
import java.nio.file.Paths

object LogConfig {
    init {
        // Configurar la propiedad del sistema antes de que logback se inicialice
        System.setProperty("descuentados.log.path", getLogFilePath())
    }

    fun getLogFilePath(): String {
        val os = System.getProperty("os.name").lowercase()

        return when {
            os.contains("win") -> {
                val appData = System.getenv("APPDATA") ?: System.getProperty("user.home")
                Paths.get(appData, "descuentados", "descuentados.log").toString()
            }
            os.contains("mac") -> {
                val home = System.getProperty("user.home")
                Paths.get(home, "Library", "Application Support", "descuentados", "descuentados.log").toString()
            }
            else -> { // Linux y demás Unix
                val xdg = System.getenv("XDG_CONFIG_HOME")
                val baseDir = xdg ?: Paths.get(System.getProperty("user.home"), ".config").toString()
                Paths.get(baseDir, "descuentados", "descuentados.log").toString()
            }
        }
    }

    fun ensureLogDirectoryExists() {
        val logFile = File(getLogFilePath())
        logFile.parentFile.mkdirs()
    }

    fun readLogFile(): String {
        val logFile = File(getLogFilePath())
        return if (logFile.exists()) {
            logFile.readText()
        } else {
            "No hay logs disponibles"
        }
    }
}
