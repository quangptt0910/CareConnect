package com.example.careconnect.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * A single clickable text action with an icon at the end
 */
@Composable
fun ActionTextButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false),
                enabled = enabled,
                onClick = onClick
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // The text
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Icon
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * A row containing Filter, Sort and Reset options
 */
@Composable
fun FilterSortResetRow(
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
    filterEnabled: Boolean = true,
    sortEnabled: Boolean = true,
    resetEnabled: Boolean = true
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Filter button
        ActionTextButton(
            text = "Filter",
            icon = Icons.Default.FilterList,
            onClick = onFilterClick,
            enabled = filterEnabled
        )

        // Vertical divider
        VerticalDivider(
            modifier = Modifier
                .height(16.dp)
                .width(1.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )

        // Sort button
        ActionTextButton(
            text = "Sort",
            icon = Icons.Default.Sort,
            onClick = onSortClick,
            enabled = sortEnabled
        )

        // Vertical divider
        Divider(
            modifier = Modifier
                .height(16.dp)
                .width(1.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )

        // Reset button
        ActionTextButton(
            text = "Reset",
            icon = Icons.Default.RestartAlt,
            onClick = onResetClick,
            enabled = resetEnabled
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FilterSortResetRowPreview() {
    MaterialTheme {
        FilterSortResetRow(
            onFilterClick = { /* Filter action */ },
            onSortClick = { /* Sort action */ },
            onResetClick = { /* Reset action */ }
        )
    }
}