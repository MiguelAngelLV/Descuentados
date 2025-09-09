package org.malv.descuentados

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.malv.descuentados.services.ConfigurationService
import org.malv.descuentados.ui.App

fun main(args: Array<String>) = application {
    Window(
        onCloseRequest = {
            ConfigurationService.instance.save()
            exitApplication()
        },
        title = "Descuentados",
        state = WindowState(width = 1000.dp, height = 800.dp, position = WindowPosition.Aligned(Alignment.TopEnd))
    ) {
        App()
    }
}
