package com.dailyroutine.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.dailyroutine.app.ui.theme.Border
import com.dailyroutine.app.ui.theme.InkFaint
import com.dailyroutine.app.ui.theme.Surface
import com.dailyroutine.app.ui.theme.Teal

enum class RoutineTab(val label: String) { HOME("Home"), CALENDAR("Calendar"), STATS("Stats"), SETTINGS("Settings") }

@Composable
fun BottomNavBar(current: RoutineTab, onSelect: (RoutineTab) -> Unit) {
    NavigationBar(containerColor = Surface, tonalElevation = 0.dp, modifier = androidx.compose.ui.Modifier.padding(top = 1.dp)) {
        val items = listOf(
            Triple(RoutineTab.HOME, Icons.Filled.Home, "Home"),
            Triple(RoutineTab.CALENDAR, Icons.Filled.CalendarMonth, "Calendar"),
            Triple(RoutineTab.STATS, Icons.Filled.BarChart, "Stats"),
            Triple(RoutineTab.SETTINGS, Icons.Filled.Settings, "Settings"),
        )
        items.forEach { (tab, icon, label) ->
            NavigationBarItem(
                selected = current == tab,
                onClick = { onSelect(tab) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Teal,
                    selectedTextColor = Teal,
                    unselectedIconColor = InkFaint,
                    unselectedTextColor = InkFaint,
                    indicatorColor = Surface,
                ),
            )
        }
    }
}
