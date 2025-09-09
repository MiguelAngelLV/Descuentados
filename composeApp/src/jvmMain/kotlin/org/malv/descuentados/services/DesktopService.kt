package org.malv.descuentados.services

import org.malv.descuentados.services.LoginService.InvalidOSError
import java.awt.Desktop
import java.net.URI

object DesktopService {

    fun browse(uri: String) {
        val osName by lazy(LazyThreadSafetyMode.NONE) { System.getProperty("os.name").lowercase() }
        val desktop = Desktop.getDesktop()
        when {
            Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE) -> desktop.browse(URI(uri))
            "mac" in osName -> Runtime.getRuntime().exec(arrayOf("open", uri))
            "nix" in osName || "nux" in osName -> Runtime.getRuntime().exec(arrayOf("xdg-open", uri))
            else -> throw InvalidOSError("cannot open $uri")
        }
    }
}
