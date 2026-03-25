package org.malv.descuentados

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.malv.descuentados.services.ConfigurationService
import org.malv.descuentados.ui.App
import org.malv.descuentados.utils.LogConfig
import org.slf4j.LoggerFactory

fun main(args: Array<String>) = application {
    LogConfig.ensureLogDirectoryExists()

    val logger = LoggerFactory.getLogger("Main")
    logger.info("Iniciando aplicación Descuentados")
    logger.debug("Argumentos de línea de comandos: ${args.joinToString()}")
    logger.info("Sistema operativo: ${System.getProperty("os.name")} ${System.getProperty("os.version")}")
    logger.info("Java version: ${System.getProperty("java.version")}")
    logger.info("Archivo de log: ${LogConfig.getLogFilePath()}")

    Window(
        onCloseRequest = {
            ConfigurationService.instance.save()
            exitApplication()
        },
        title = "Descuentados",
        state = WindowState(width = 1000.dp, height = 800.dp, position = WindowPosition.Aligned(Alignment.TopEnd)),
    ) {
        App()
    }
}
