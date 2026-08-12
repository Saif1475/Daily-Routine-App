package com.dailyroutine.app.ui.screens.stats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dailyroutine.app.data.model.RoutineType
import com.dailyroutine.app.ui.components.ProgressRing
import com.dailyroutine.app.ui.components.main
import com.dailyroutine.app.ui.screens.home.RoutineViewModel
import com.dailyroutine.app.ui.theme.Border
import com.dailyroutine.app.ui.theme.Ink
import com.dailyroutine.app.ui.theme.InkFaint
import com.dailyroutine.app.ui.theme.InkMuted
import com.dailyroutine.app.ui.theme.Orange
import com.dailyroutine.app.ui.theme.Surface
import com.dailyroutine.app.ui.theme.Teal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun StatsScreen(viewModel: RoutineViewModel) {
    val items by viewModel.routines.collectAsState()
    val today = LocalDate.now()
    val last7Days = (0..6).map { today.minusDays((6 - it).toLong()) }

    fun rateFor(day: LocalDate): Int? {
        val key = day.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val applicable = items.filter { it.history.containsKey(key) }
        if (applicable.isEmpty()) return null
        val done = applicable.count { it.history[key] == true }
        return done * 100 / applicable.size
    }

    val rates = last7Days.map { rateFor(it) }
    val validRates = rates.filterNotNull()
    val overallPct = if (validRates.isNotEmpty()) validRates.sum() / validRates.size else 0

    val habitItems = items.filter { it.type == RoutineType.HABIT }
    val bestStreak = habitItems.maxOfOrNull { it.streak } ?: 0
    val perfectDays = last7Days.count { rateFor(it) == 100 }
    val streakList = habitItems.sortedByDescending { it.streak }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 100.dp)) {
        item {
            Text("Your progress", style = MaterialTheme.typography.headlineSmall, color = Ink, modifier = Modifier.padding(bottom = 18.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Teal),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ProgressRing(percent = overallPct, trackColor = Color.White.copy(alpha = 0.35f), progressColor = Color.White, labelColor = Color.White, size = 64.dp, strokeWidth = 7.dp)
                    Column {
                        Text("This week's completion", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Text("Averaged across all routines", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
                    }
                }
            }
            Spacer(Modifier.size(18.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("$bestStreak", "Best streak", Modifier.weight(1f))
                StatTile("${habitItems.size}", "Active habits", Modifier.weight(1f))
                StatTile("$perfectDays", "Perfect days", Modifier.weight(1f))
            }
            Spacer(Modifier.size(22.dp))

            Text("LAST 7 DAYS", style = MaterialTheme.typography.titleSmall, color = InkFaint, modifier = Modifier.padding(bottom = 12.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(modifier = Modifier.padding(14.dp).height(100.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    last7Days.forEachIndexed { idx, day ->
                        val rate = rates[idx]
                        val isToday = day == today
                        Column(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .fillMaxHeight(if (rate == null) 0.06f else (maxOf(rate, 6) / 100f))
                                    .background(if (rate == null) Border else if (isToday) Orange else Teal, RoundedCornerShape(6.dp)),
                            )
                            Spacer(Modifier.size(6.dp))
                            Text(
                                day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(1),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isToday) Orange else InkFaint,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.size(26.dp))
            Text("HABIT STREAKS", style = MaterialTheme.typography.titleSmall, color = InkFaint, modifier = Modifier.padding(bottom = 10.dp))
        }

        if (streakList.isEmpty()) {
            item { Text("No habits yet.", style = MaterialTheme.typography.bodySmall, color = InkMuted) }
        }

        items(streakList, key = { it.id }) { habit ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border),
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            ) {
                Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(10.dp).background(habit.color.main(), RoundedCornerShape(50)))
                    Text(habit.title, style = MaterialTheme.typography.bodyMedium, color = Ink, modifier = Modifier.weight(1f))
                    Text("${habit.streak}", style = MaterialTheme.typography.titleMedium, color = habit.color.main(), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatTile(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge, color = Ink, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = InkFaint)
        }
    }
}
