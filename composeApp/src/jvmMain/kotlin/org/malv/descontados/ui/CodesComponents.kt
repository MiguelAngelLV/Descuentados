package org.malv.descontados.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NextPlan
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.malv.descontados.models.Language
import org.malv.descontados.models.VideoResult
import org.malv.descontados.models.VideoStatus
import org.malv.descontados.services.CodesService
import org.malv.descontados.services.ConfigurationService
import org.malv.descontados.services.DesktopService
import org.malv.descontados.viewmodels.CodesViewModel
import org.malv.descontados.viewmodels.YoutubeViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CodesInput(
    codes: String,
    visible: Boolean,
    onCodeChanges: (String) -> Unit,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CustomCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
                .onClick(enabled = true, onClick = onClicked)
        ) {
            Text("Códigos", style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth())

            AnimatedVisibility(visible) {
                CompactTextField(
                    value = codes,
                    onValueChange = onCodeChanges,
                    maxLines = Int.MAX_VALUE,
                    label = { Text("Códigos") },
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CodesPreview(
    languages: List<Language>,
    codes: String,
    visible: Boolean,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }

    CustomCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
                .onClick(enabled = true, onClick = onClicked)
        ) {
            Text("Preview", style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth())
            AnimatedVisibility(visible) {
                Column {
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

                    Text(
                        text = CodesService.generateCodes(codes = codes, language.template, language.start, language.end),
                        modifier = Modifier.fillMaxHeight(),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = Int.MAX_VALUE,
                    )
                }
            }
        }
    }
}

@Composable
fun CodesUI(
    codesViewModel: CodesViewModel = viewModel { CodesViewModel(ConfigurationService.instance) },
    youtubeViewModel: YoutubeViewModel = viewModel { YoutubeViewModel(ConfigurationService.instance) },
    modifier: Modifier = Modifier
) {
    val codes by codesViewModel.codes.collectAsState()
    val languages by codesViewModel.languages.collectAsState()
    val updateEnabled by youtubeViewModel.isLogged.collectAsState()
    val updating by youtubeViewModel.updating.collectAsState()
    var collapsed by rememberSaveable { mutableIntStateOf(0) }
    val videosList = youtubeViewModel.videos.collectAsState().value

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp), modifier =
                Modifier.padding(16.dp).fillMaxWidth(0.5f)
        ) {
            CodesInput(
                codes = codes,
                visible = collapsed == 0,
                onCodeChanges = codesViewModel::updateCodes,
                onClicked = { collapsed = 0 },
                modifier = if (collapsed == 0) Modifier.weight(1f) else Modifier.height(60.dp)
            )

            CodesPreview(
                languages = languages,
                codes = codes,
                visible = collapsed == 1,
                onClicked = { collapsed = 1 },
                modifier = if (collapsed == 1) Modifier.weight(1f) else Modifier.height(60.dp)
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxHeight().padding(8.dp)

        ) {
            VideosListUI(
                updateEnabled = updateEnabled,
                updating = updating,
                onUpdateClicked = { youtubeViewModel.updateVideos() },
                videoLists = videosList,
            )
        }
    }
}

@Composable
fun VideosListUI(
    updateEnabled: Boolean,
    updating: Boolean,
    onUpdateClicked: () -> Unit,
    videoLists: List<VideoResult>,
) {
    val scrollState = rememberLazyListState()
    var states by remember { mutableStateOf(VideoStatus.entries.toSet()) }
    var filterMenu by remember { mutableStateOf(false) }
    val videos = videoLists.filter { it.status in states }

    CustomCard(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
                Text("Videos", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))

                ApplyButton(
                    updating = updating,
                    enabled = updateEnabled,
                    onClicked = onUpdateClicked
                )

                Box {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Filtros",
                        modifier = Modifier.clickable { filterMenu = true }
                    )

                    FilterState(
                        states = states,
                        expanded = filterMenu,
                        onExpandChanges = { filterMenu = it },
                        onFilterChanges = { states = it }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(modifier = Modifier.fillMaxSize()
                    .padding(end = 12.dp), state = scrollState) {
                    items(videos) { v ->
                        VideoItem(v)
                    }
                }

                VerticalScrollbar(ScrollbarAdapter(scrollState), modifier = Modifier.align(Alignment.TopEnd))
            }

            Text(
                text = "${videos.size}/${videoLists.size}",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()

            )
        }
    }
}

@Composable
fun ApplyButton(enabled: Boolean, updating: Boolean, onClicked: () -> Unit) {
    when {
        updating -> UpdatingButton()
        enabled -> UpdateButton(onUpdateClicked = onClicked)
        else -> DisabledButton()
    }
}

@Composable
fun UpdatingButton() {
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "angle"
    )

    Icon(
        imageVector = Icons.Default.Refresh,
        contentDescription = "Actualizando",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.rotate(angle)
    )
}

@Composable
fun UpdateButton(onUpdateClicked: () -> Unit) {
    Icon(
        imageVector = Icons.Default.PlayArrow,
        contentDescription = "Actualizar",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.clickable { onUpdateClicked() }
    )
}

@Composable
fun DisabledButton() {
    Icon(
        imageVector = Icons.Default.Refresh,
        contentDescription = "Deshabilitado",
        tint = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun FilterState(
    states: Set<VideoStatus>,
    expanded: Boolean,
    onFilterChanges: (Set<VideoStatus>) -> Unit,
    onExpandChanges: (Boolean) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onExpandChanges(false) }
    ) {
        MenuItemFilter("Actualizados", VideoStatus.UPDATED in states) {
            if (it) {
                onFilterChanges(states + VideoStatus.UPDATED)
            } else {
                onFilterChanges(states - VideoStatus.UPDATED)
            }
        }
        MenuItemFilter("Omitidos", VideoStatus.SKIPPED in states) {
            if (it) {
                onFilterChanges(states + VideoStatus.SKIPPED)
            } else {
                onFilterChanges(states - VideoStatus.SKIPPED)
            }
        }
        MenuItemFilter("Error", VideoStatus.ERROR in states) {
            if (it) {
                onFilterChanges(states + VideoStatus.ERROR)
            } else {
                onFilterChanges(states - VideoStatus.ERROR)
            }
        }
    }
}

@Composable
fun MenuItemFilter(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    DropdownMenuItem(
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = checked, onCheckedChange = onCheckedChange)
                Text(text)
            }
        },
        onClick = { onCheckedChange(!checked) },
    )
}

@Composable
fun VideoItem(video: VideoResult) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconStatus(video)
            Text(
                text = video.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.Edit, contentDescription = video.status.name,
                modifier = Modifier.clickable {
                    DesktopService.browse("https://studio.youtube.com/video/${video.videoId}/edit")
                }, tint = MaterialTheme.colorScheme.secondary
            )
        }
        video.error?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Red,
                modifier = Modifier.padding(start = 32.dp)
            )
        }
    }

    Divider(modifier = Modifier.fillMaxWidth())
}

@Composable
fun IconStatus(video: VideoResult) {
    val icon = when (video.status) {
        VideoStatus.UPDATED -> Icons.Default.Check
        VideoStatus.SKIPPED -> Icons.AutoMirrored.Filled.NextPlan
        VideoStatus.ERROR -> Icons.Default.ErrorOutline
    }
    val color = when (video.status) {
        VideoStatus.UPDATED -> MaterialTheme.colorScheme.primary
        VideoStatus.SKIPPED -> MaterialTheme.colorScheme.secondary
        VideoStatus.ERROR -> MaterialTheme.colorScheme.error
    }

    Icon(imageVector = icon, contentDescription = video.status.name, tint = color)
}
