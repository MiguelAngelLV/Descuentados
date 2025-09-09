package org.malv.descuentados.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.malv.descuentados.services.ConfigurationService
import org.malv.descuentados.viewmodels.YoutubeViewModel
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun ButtonClientSecret(onPathChanges: (String) -> Unit) {
    CompactButton(onClick = {
        val chooser = JFileChooser()
        chooser.fileSelectionMode = JFileChooser.FILES_ONLY
        chooser.fileFilter = FileNameExtensionFilter("JSON file", "json")
        val result = chooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            onPathChanges(chooser.selectedFile.absolutePath)
        }
    }) {
        Text("Select client_secret.json")
    }
}

@Composable
fun LoginButton(onClick: () -> Unit = {}) {
    CompactButton(onClick = onClick) {
        Text("Youtube Login")
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit = {}) {
    CompactButton(onClick = onClick) {
        Text("Youtube Logout")
    }
}

@Composable
fun YoutubeLogin(
    youtubeViewModel: YoutubeViewModel = viewModel { YoutubeViewModel(ConfigurationService.instance) }
) {
    val isLogged by youtubeViewModel.isLogged.collectAsState()
    val isFileSelected by youtubeViewModel.isFileSelected.collectAsState()
    val showLogin = !isLogged && isFileSelected
    val showLogout = isLogged

    CustomCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Youtube Login",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ButtonClientSecret(onPathChanges = { youtubeViewModel.updateClientSecretPath(it) })
                if (showLogin) LoginButton { youtubeViewModel.login() }
                if (showLogout) LogoutButton { youtubeViewModel.logout() }
            }
        }
    }
}
