package org.malv.descuentados.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import org.malv.descuentados.models.Routes

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun App() {
    CompactTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            var selectedIndex by rememberSaveable { mutableIntStateOf(Routes.CODES.ordinal) }

            Column {
                PrimaryTabRow(
                    selectedTabIndex = selectedIndex,
                ) {
                    Routes.entries.forEachIndexed { index, route ->
                        Tab(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = route.icon, contentDescription = route.title)
                                    Text(route.title)
                                }
                            },
                            selected = false,
                            onClick = {
                                selectedIndex = index
                                navController.navigate(route.route)
                            }
                        )
                    }
                }
                AppNavHost(navController, Routes.CODES.route)
            }
        }
    }
}
