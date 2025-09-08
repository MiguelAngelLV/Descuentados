package org.malv.descontados.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.malv.descontados.models.Routes

@Composable
fun AppNavHost(
    navHostController: NavHostController,
    startDestination: String
) {
    NavHost(navHostController, startDestination = startDestination) {
        composable(Routes.CODES.route) {
            CodesUI()
        }

        composable(Routes.COMMISSIONS.route) {
            CommissionsUI()
        }

        composable(Routes.SETTINGS.route) {
            SettingsUI()
        }
    }
}
