package com.dailyroutine.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dailyroutine.app.data.model.RoutineCategory
import com.dailyroutine.app.data.model.RoutineItem
import com.dailyroutine.app.ui.components.ProgressRing
import com.dailyroutine.app.ui.components.RoutineCard
import com.dailyroutine.app.ui.theme.Border
import com.dailyroutine.app.ui.theme.Ink
import com.dailyroutine.app.ui.theme.InkFaint
import com.dailyroutine.app.ui.theme.InkMuted
import com.dailyroutine.app.ui.theme.Orange
import com.dailyroutine.app.ui.theme.Surface
import com.dailyroutine.app.ui.theme.Teal
import com.google.firebase.auth.FirebaseUser
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: RoutineViewModel,
    user: FirebaseUser?,
    onOpenDetail: (String) -> Unit,
) {
    val items by viewModel.routines.collectAsState()
    val today = LocalDate.now()
    val todayKey = today.format(DateTimeFormatter.ISO_LOCAL_DATE)

    val total = items.size
    val completed = items.count { it.history[todayKey] == true }
    val percent = if (total > 0) (completed * 100 / total) else 0
    val progressMessage = when {
        total == 0 -> "Add your first routine to get started."
        percent == 100 -> "Perfect day! Nicely done."
        percent >= 50 -> "You're on track today."
        else -> "Let's get a few more done."
    }

    val sections = RoutineCategory.entries
        .map { cat -> cat to items.filter { it.category == cat } }
        .filter { it.second.isNotEmpty() }

    val displayName = user?.displayName?.takeIf { it.isNotBlank() } ?: user?.email?.substringBefore("@") ?: "there"

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp, 20.dp, 20.dp, 100.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Good day, $displayName", style = MaterialTheme.typography.headlineSmall, color = Ink)
                    Text(
                        today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                        style = MaterialTheme.typography.bodySmall,
                        color = InkMuted,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Orange, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        displayName.take(1).uppercase(),
                        color = Surface,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            androidx.compose.foundation.layout.Spacer(Modifier.size(20.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ProgressRing(percent = percent, trackColor = Border, progressColor = Teal, labelColor = Ink)
                    Column {
                        Text("$completed of $total done today", style = MaterialTheme.typography.titleMedium, color = Ink)
                        Text(progressMessage, style = MaterialTheme.typography.bodySmall, color = InkMuted)
                    }
                }
            }
            androidx.compose.foundation.layout.Spacer(Modifier.size(22.dp))
        }

        sections.forEach { (category, sectionItems) ->
            item {
                Text(
                    category.label.uppercase(Locale.getDefault()),
                    style = MaterialTheme.typography.titleSmall,
                    color = InkFaint,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
            }
            items(sectionItems, key = { it.id }) { item: RoutineItem ->
                Box(Modifier.padding(bottom = 10.dp)) {
                    RoutineCard(
                        item = item,
                        onToggle = { viewModel.toggleToday(item) },
                        onOpen = { onOpenDetail(item.id) },
                    )
                }
            }
            item { androidx.compose.foundation.layout.Spacer(Modifier.size(12.dp)) }
        }

        if (total == 0) {
            item {
                Text(
                    "No routines yet — tap + to add your first habit or task.",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkMuted,
                )
            }
        }
    }
}
