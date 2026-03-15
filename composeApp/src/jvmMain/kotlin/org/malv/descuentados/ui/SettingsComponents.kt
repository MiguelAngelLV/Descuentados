package org.malv.descuentados.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.malv.descuentados.models.Language
import org.malv.descuentados.services.ConfigurationService
import org.malv.descuentados.viewmodels.CodesViewModel

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
                },
                onAddLanguage = {
                    codesViewModel.addLanguage(it)
                },
                onDeleteLanguage = {
                    codesViewModel.deleteLanguage(it)
                }
            )
        }
    }
}

@Composable
fun CodesPreview(
    languages: Map<String, Language>,
    onUpdateLanguage: (Language) -> Unit,
    onAddLanguage: (Language) -> Unit,
    onDeleteLanguage: (String) -> Unit,
) {
    var codeSelected by rememberSaveable { mutableStateOf(languages.keys.first()) }
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    val selected = languages[codeSelected]!!

    CustomCard(
        modifier =
            Modifier.fillMaxWidth()
                .fillMaxHeight()
                .sizeIn(minHeight = 800.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Idiomas",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir idioma")
                    Text("Añadir idioma", modifier = Modifier.padding(start = 4.dp))
                }
            }

            Row(verticalAlignment = Alignment.Top) {
                LanguageList(
                    languages = languages,
                    codeSelected = codeSelected,
                    onDeleteLanguage = onDeleteLanguage,
                    onCodeSelected = {
                        codeSelected = it
                    })

                VerticalDivider()

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 16.dp)
                        .weight(0.7f)
                ) {
                    CodesTemplate(template = selected.template, onTemplateChanges = {
                        onUpdateLanguage(selected.copy(template = it))
                    })

                    RangeCodes(
                        start = selected.start,
                        end = selected.end,
                        onStartChanges = {
                            onUpdateLanguage(selected.copy(start = it))
                        },
                        onEndChanges = {
                            onUpdateLanguage(selected.copy(end = it))
                        }
                    )

                    VariablesDescription()
                }
            }
        }

        if (showAddDialog) {
            AddDialog(
                languages = languages,
                onDismiss = {
                    showAddDialog = false
                            },
                onLanguageAdded = { language ->
                    showAddDialog = false
                    codeSelected = language.code
                    onAddLanguage(language)
                }
            )
        }
    }
}

@Composable
private fun RowScope.LanguageList(
    languages: Map<String, Language>,
    codeSelected: String,
    onDeleteLanguage: (String) -> Unit,
    onCodeSelected: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.weight(0.3f)
            .padding(end = 16.dp)
    ) {
        items(languages.entries.toList()) { entry ->
            val language = entry.value
            val isOnlyLanguage = languages.size == 1

            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(
                        if (entry.value.code == codeSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceBright
                        }
                    )
                    .clickable { onCodeSelected(entry.key) }
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.code,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (entry.value.code == codeSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        if (!isOnlyLanguage) {
                            onDeleteLanguage(language.code)
                            if (codeSelected == language.code) {
                                onCodeSelected(languages.keys.first { it != language.code })
                            }
                        }
                    },
                    enabled = !isOnlyLanguage
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar idioma",
                        tint = if (isOnlyLanguage) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            }

            HorizontalDivider()
        }
    }
}

@Composable
fun AddDialog(
    languages: Map<String, Language>,
    onDismiss: () -> Unit = {},
    onLanguageAdded: (Language) -> Unit = {},
) {
    var newLanguageCode by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Añadir nuevo idioma") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Ingresa el código del idioma (por ejemplo: es, en, fr)")
                CompactTextField(
                    value = newLanguageCode,
                    onValueChange = { newLanguageCode = it },
                    label = { Text("Código de idioma") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newLanguageCode.isNotBlank() && !languages.containsKey(newLanguageCode)) {
                        val newLanguage = Language(
                            code = newLanguageCode.trim(),
                            template = "",
                            start = "",
                            end = ""
                        )
                        onLanguageAdded(newLanguage)
                    }
                },
                enabled = newLanguageCode.isNotBlank() && !languages.containsKey(newLanguageCode.trim())
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun VariablesDescription() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Variables disponibles:", style = MaterialTheme.typography.titleMedium)
        Text("- {{discount}}: El descuento del cupón.")
        Text("- {{minOrder}}: El mínimo de pedido para usar el cupón.")
        Text("- {{code}}: El código del cupón.")
        Text("- {{currency}}: La moneda del cupón.")
        Text("- {{percentage}}: El porcentaje de descuento del cupón.")
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
