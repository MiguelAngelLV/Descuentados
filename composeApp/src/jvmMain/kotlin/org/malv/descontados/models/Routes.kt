package org.malv.descontados.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class Routes(val title: String, val route: String, val icon: ImageVector) {
    CODES("Códigos", "codes", Icons.Default.Savings),
    COMMISSIONS("Comisiones", "commission", Icons.Default.QueryStats),
    SETTINGS("Configuración", "settings", Icons.Default.Settings),
}
