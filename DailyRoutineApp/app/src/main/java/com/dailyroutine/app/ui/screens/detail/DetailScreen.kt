package com.dailyroutine.app.ui.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.dailyroutine.app.data.model.RoutineType
import com.dailyroutine.app.ui.components.main
import com.dailyroutine.app.ui.screens.home.RoutineViewModel
import com.dailyroutine.app.ui.theme.Border
import com.dailyroutine.app.ui.theme.Ink
import com.dailyroutine.app.ui.theme.InkFaint
import com.dailyroutine.app.ui.theme.InkMuted
import com.dailyroutine.app.ui.theme.Red
import com.dailyroutine.app.ui.theme.Surface
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DetailScreen(
    routineId: String,
    viewModel: RoutineViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
) {
    val items by viewModel.routines.collectAsState()
    val item = items.firstOrNull { it.id == routineId } ?: run {
        onBack()
        return
    }

    val main = item.color.main()
    val today = LocalDate.now()
    val todayKey = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
    val checked = item.history[todayKey] == true

    val weekStart = today.minusDays((today.dayOfWeek.value % 7).toLong())
    val weekDays = (0..6).map { weekStart.plusDays(it.toLong()) }

    val past = weekDays.filter { !it.isAfter(today) }
    val applicable = past.count { item.history.containsKey(it.format(DateTimeFormatter.ISO_LOCAL_DATE)) }
    val done = past.count { item.history[it.format(DateTimeFormatter.ISO_LOCAL_DATE)] == true }
    val rate = if (applicable > 0) done * 100 / applicable else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.dailyroutine.app.ui.theme.Background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Surface, RoundedCornerShape(12.dp))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink, modifier = Modifier.size(18.dp))
            }
            Text("Edit", style = MaterialTheme.typography.bodyMedium, color = main, modifier = Modifier.clickable { onEdit(item.id) })
        }
        Spacer(Modifier.size(22.dp))

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(64.dp).background(main.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(item.title.take(1).uppercase(), style = MaterialTheme.typography.titleLarge, color = main, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.size(12.dp))
            Text(item.title, style = MaterialTheme.typography.titleLarge, color = Ink)
            Text(
                item.category.label + if (item.time.isNotBlank()) " · ${item.time}" else "",
                style = MaterialTheme.typography.bodySmall,
                color = InkMuted,
            )
        }
        Spacer(Modifier.size(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (checked) main.copy(alpha = 0.15f) else main, RoundedCornerShape(16.dp))
                .clickable { viewModel.toggleToday(item) }
                .padding(14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                if (checked) "Marked complete for today" else "Mark complete for today",
                color = if (checked) main else Surface,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.size(22.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(modifier = Modifier.padding(vertical = 14.dp, horizontal = 10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                weekDays.forEach { day ->
                    val v = item.history[day.format(DateTimeFormatter.ISO_LOCAL_DATE)]
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(1),
                            style = MaterialTheme.typography.labelSmall,
                            color = InkFaint,
                        )
                        Spacer(Modifier.size(6.dp))
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .background(if (v == true) main else Border.copy(alpha = 0.5f), RoundedCornerShape(9.dp)),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.size(22.dp))

        if (item.type == RoutineType.HABIT) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DetailStat("${item.streak}", "Current streak", Modifier.weight(1f))
                DetailStat("${item.bestStreak}", "Best streak", Modifier.weight(1f))
                DetailStat("$rate%", "This week", Modifier.weight(1f))
            }
            Spacer(Modifier.size(22.dp))
        }

        Text(
            "Delete routine",
            style = MaterialTheme.typography.bodyMedium,
            color = Red,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.deleteRoutine(item.id)
                    onBack()
                },
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun DetailStat(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(14.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleLarge, color = Ink, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = InkFaint)
        }
    }
}
