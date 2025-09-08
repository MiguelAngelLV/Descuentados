package org.malv.descontados.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.malv.descontados.models.Language
import org.malv.descontados.services.ConfigurationService
import org.malv.descontados.viewmodels.CodesViewModel

@Composable
fun SettingsUI(
    codesViewModel: CodesViewModel = viewModel { CodesViewModel(ConfigurationService.instance) },
) {
    val languages by codesViewModel.languages.collectAsState()

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            YoutubeLogin()
            CodesPreview(
                languages = languages,
                onUpdateLanguage = {
                    codesViewModel.updateLanguage(it)
                }
            )
        }
    }
}

@Composable
fun CodesPreview(
    languages: List<Language>,
    onUpdateLanguage: (Language) -> Unit,
) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    CustomCard(
        modifier =
            Modifier.fillMaxWidth()
                .fillMaxHeight()
                .sizeIn(minHeight = 800.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
            Text(
                "Idiomas",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TabRow(selectedTabIndex = tabIndex) {
                languages.forEachIndexed { index, language ->
                    Tab(
                        text = { Text(language.title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index }
                    )
                }
            }

            val language = languages[tabIndex]
            RangeCodes(
                language.start,
                language.end,
                onStartChanges = { onUpdateLanguage(language.copy(start = it)) },
                onEndChanges = { onUpdateLanguage(language.copy(end = it)) }
            )
            CodesTemplate(language.template, onTemplateChanges = { onUpdateLanguage(language.copy(template = it)) })
        }
    }
}

@Composable
fun CodesTemplate(template: String, onTemplateChanges: (String) -> Unit) {
    CompactTextField(
        value = template,
        onValueChange = onTemplateChanges,
        label = { Text("Plantilla") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun StartCodes(start: String, onStartChanges: (String) -> Unit) {
    CompactTextField(
        value = start,
        onValueChange = onStartChanges,
        label = { Text("Inicio") },
        modifier = Modifier.fillMaxWidth(0.5f)
    )
}

@Composable
fun EndCodes(end: String, onEndChanges: (String) -> Unit) {
    CompactTextField(
        value = end,
        onValueChange = onEndChanges,
        label = { Text("Fin") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun RangeCodes(start: String, end: String, onStartChanges: (String) -> Unit, onEndChanges: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        StartCodes(start, onStartChanges)
        EndCodes(end, onEndChanges)
    }
}
