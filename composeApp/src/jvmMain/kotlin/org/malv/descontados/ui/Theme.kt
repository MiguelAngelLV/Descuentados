package org.malv.descontados.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val LocalButtonContentPadding = compositionLocalOf { PaddingValues(horizontal = 8.dp, vertical = 4.dp) }
val LocalTextFieldPadding = compositionLocalOf { PaddingValues(horizontal = 1.dp, vertical = 1.dp) }

private val CompactShapes = Shapes(
    small = RoundedCornerShape(3.dp),
    medium = RoundedCornerShape(3.dp),
    large = RoundedCornerShape(4.dp),
    extraLarge = RoundedCornerShape(5.dp),
    extraSmall = RoundedCornerShape(3.dp),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentSize provides 8.dp,
        LocalButtonContentPadding provides PaddingValues(horizontal = 6.dp, vertical = 2.dp),
        LocalTextFieldPadding provides PaddingValues(horizontal = 6.dp, vertical = 2.dp),
    ) {
        MaterialTheme(
            shapes = CompactShapes,
            content = content
        )
    }
}

@Composable
fun CompactButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        content = content,
        contentPadding = LocalButtonContentPadding.current
    )
}

@Composable
fun CompactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    maxLines: Int = Int.MAX_VALUE,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        maxLines = maxLines,
        textStyle = MaterialTheme.typography.bodySmall,
        modifier = modifier
            .padding(LocalTextFieldPadding.current)
            .sizeIn(minHeight = 24.dp),
        label = label,
        enabled = enabled,
        readOnly = readOnly,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = Color.Gray,
            disabledLabelColor = Color.Gray,
            disabledBorderColor = Color.Gray,
        )

    )
}

@Composable
fun CustomCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            content()
        }
    }
}
