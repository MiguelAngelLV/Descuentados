package org.malv.descuentados.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.malv.descuentados.utils.LogConfig
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("LogViewerWindow")

@Composable
fun LogViewerWindow(
    onClose: () -> Unit
) {
    var logText by remember { mutableStateOf(LogConfig.readLogFile()) }
    var autoRefresh by remember { mutableStateOf(true) }
    var refreshInterval by remember { mutableStateOf(2000L) }
    val scrollState = rememberScrollState(Int.MAX_VALUE)

    // Auto-refresh logs
    LaunchedEffect(autoRefresh, refreshInterval) {
        if (autoRefresh) {
            while (true) {
                delay(refreshInterval)
                try {
                    val newText = LogConfig.readLogFile()
                    if (newText != logText) {
                        logText = newText
                    }
                } catch (e: Exception) {
                    logger.error("Error al refrescar los logs", e)
                }
            }
        }
    }

    // Scroll inicial al final
    LaunchedEffect(logText) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header con controles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Visor de Logs",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Mostrando las últimas 500 líneas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Auto-refresh toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Checkbox(
                        checked = autoRefresh,
                        onCheckedChange = { autoRefresh = it }
                    )
                    Text("Auto-refrescar", style = MaterialTheme.typography.bodyMedium)
                }

                // Manual refresh button
                IconButton(
                    onClick = {
                        logger.debug("Refrescando logs manualmente")
                        logText = LogConfig.readLogFile()
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refrescar logs")
                }

                Button(onClick = onClose) {
                    Text("Cerrar")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Archivo: ${LogConfig.getLogFilePath()}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium
        ) {
            SelectionContainer {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    BasicTextField(
                        value = logText,
                        onValueChange = { }, // Solo lectura
                        readOnly = true,
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp)
                            .verticalScroll(scrollState)
                    )
                }
            }
        }
    }
}
