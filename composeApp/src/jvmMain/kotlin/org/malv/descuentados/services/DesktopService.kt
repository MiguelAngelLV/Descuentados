package org.malv.descuentados.services

import org.malv.descuentados.services.LoginService.InvalidOSError
import org.slf4j.LoggerFactory
import java.awt.Desktop
import java.net.URI

object DesktopService {
    private val logger = LoggerFactory.getLogger(DesktopService::class.java)

    fun browse(uri: String) {
        logger.info("Intentando abrir URI en navegador: $uri")
        val osName by lazy(LazyThreadSafetyMode.NONE) { System.getProperty("os.name").lowercase() }
        val desktop = Desktop.getDesktop()

        try {
            when {
                Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE) -> {
                    logger.debug("Usando Desktop.browse()")
                    desktop.browse(URI(uri))
                }
                "mac" in osName -> {
                    logger.debug("Usando comando 'open' de macOS")
                    Runtime.getRuntime().exec(arrayOf("open", uri))
                }
                "nix" in osName || "nux" in osName -> {
                    logger.debug("Usando comando 'xdg-open' de Linux")
                    Runtime.getRuntime().exec(arrayOf("xdg-open", uri))
                }
                else -> {
                    logger.error("Sistema operativo no soportado para abrir URIs: $osName")
                    throw InvalidOSError("cannot open $uri")
                }
            }
            logger.info("URI abierto exitosamente")
        } catch (e: Exception) {
            logger.error("Error al abrir URI: ${e.message}", e)
            throw e
        }
    }
}
