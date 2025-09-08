package org.malv.descontados.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextAlign.Companion.End
import androidx.compose.ui.unit.dp
import org.malv.descontados.services.CommissionService
import org.malv.descontados.viewmodels.CommissionViewModel
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun CommissionsUI(
    commissionViewModel: CommissionViewModel = CommissionViewModel(CommissionService.instance)
) {
    val commissions by commissionViewModel.commissions.collectAsState()
    val selected by commissionViewModel.selected.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
        CustomCard(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                SelectCommissionFile(
                    selected = selected,
                    onSelectFile = {
                        commissionViewModel.onFileSelected(it)
                    })
            }
        }

        CustomCard(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            LazyColumn {
                stickyHeader {
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Header("Mes", modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                        Header("Comisión", Modifier.width(100.dp))
                        Header("Pedidos", Modifier.width(100.dp))
                        Header("Artículos", Modifier.width(100.dp))
                    }
                }

                items(commissions) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            NormalCell(it.month.toText())
                            NumericCell("%.2f (%.0f%%)".format(it.commission, it.diff))
                            NumericCell("${it.orders}")
                            NumericCell("${it.items}")
                        }
                        Divider(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@Composable
fun NumericCell(value: String) {
    Text(value, modifier = Modifier.width(100.dp), textAlign = End, maxLines = 1, style = MaterialTheme.typography.bodyMedium)
}

@Composable
fun RowScope.NormalCell(value: String) {
    Text(value, modifier = Modifier.weight(1f), maxLines = 1, style = MaterialTheme.typography.bodyMedium)
}

@Composable
fun Header(value: String, modifier: Modifier = Modifier, textAlign: TextAlign = TextAlign.End) {
    Text(
        text = value,
        modifier = modifier,
        maxLines = 1,
        textAlign = textAlign,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    )
}

@Composable
fun SelectCommissionFile(
    selected: String,
    onSelectFile: (String) -> Unit
) {
    CompactButton(onClick = {
        val chooser = JFileChooser()
        chooser.fileSelectionMode = JFileChooser.FILES_ONLY
        chooser.fileFilter = FileNameExtensionFilter("CSV", "csv")
        val result = chooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            onSelectFile(chooser.selectedFile.absolutePath)
        }
    }) {
        Text(selected)
    }
}

@Suppress("MagicNumber")
private fun Int.toText() = when (this) {
    1 -> "Enero"
    2 -> "Febrero"
    3 -> "Marzo"
    4 -> "Abril"
    5 -> "Mayo"
    6 -> "Junio"
    7 -> "Julio"
    8 -> "Agosto"
    9 -> "Septiembre"
    10 -> "Octubre"
    11 -> "Noviembre"
    12 -> "Diciembre"
    else -> ""
}
