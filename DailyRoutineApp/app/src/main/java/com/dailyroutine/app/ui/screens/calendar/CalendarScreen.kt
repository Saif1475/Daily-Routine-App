package com.dailyroutine.app.ui.screens.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dailyroutine.app.data.model.RoutineItem
import com.dailyroutine.app.ui.screens.home.RoutineViewModel
import com.dailyroutine.app.ui.theme.Background
import com.dailyroutine.app.ui.theme.Border
import com.dailyroutine.app.ui.theme.Ink
import com.dailyroutine.app.ui.theme.InkFaint
import com.dailyroutine.app.ui.theme.InkMuted
import com.dailyroutine.app.ui.theme.Orange
import com.dailyroutine.app.ui.theme.Red
import com.dailyroutine.app.ui.theme.Surface
import com.dailyroutine.app.ui.theme.Teal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(viewModel: RoutineViewModel) {
    val allRoutines by viewModel.routines.collectAsState()
    val today = remember { LocalDate.now() }
    var selectedDay by remember { mutableStateOf(today) }

    val weekStart = today.minusDays((today.dayOfWeek.value % 7).toLong())
    val weekDays = (0..6).map { weekStart.plusDays(it.toLong()) }

    fun rateFor(day: LocalDate): Int? {
        val key = day.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val applicable = allRoutines.filter { it.history.containsKey(key) }
        if (applicable.isEmpty()) return null
        val done = applicable.count { it.history[key] == true }
        return done * 100 / applicable.size
    }

    val selectedKey = selectedDay.format(DateTimeFormatter.ISO_LOCAL_DATE)
    val agendaItems = allRoutines.filter { it.history.containsKey(selectedKey) }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 100.dp)) {
        item {
            Text("Calendar", style = MaterialTheme.typography.headlineSmall, color = Ink)
            Text(
                today.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.bodySmall,
                color = InkMuted,
                modifier = Modifier.padding(bottom = 18.dp, top = 2.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                weekDays.forEach { day ->
                    val isToday = day == today
                    val isSelected = day == selectedDay
                    val rate = rateFor(day)
                    val dotColor = when {
                        rate == null -> Border
                        rate >= 75 -> Teal
                        rate >= 40 -> Orange
                        else -> Red
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedDay = day }
                            .padding(vertical = 4.dp),
                    ) {
                        Text(
                            day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(1),
                            style = MaterialTheme.typography.labelSmall,
                            color = InkFaint,
                        )
                        Spacer(Modifier.size(6.dp))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(if (isSelected) Teal else Surface, RoundedCornerShape(12.dp))
                                .border(
                                    BorderStroke(if (isToday && !isSelected) 2.dp else 1.dp, if (isToday && !isSelected) Teal else Border),
                                    RoundedCornerShape(12.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                day.dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) Surface else Ink,
                            )
                        }
                        Spacer(Modifier.size(6.dp))
                        Box(modifier = Modifier.size(5.dp).background(dotColor, RoundedCornerShape(50)))
                    }
                }
            }
            Spacer(Modifier.size(22.dp))
            Text(
                selectedDay.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                style = MaterialTheme.typography.titleSmall,
                color = InkFaint,
                modifier = Modifier.padding(bottom = 10.dp),
            )
        }

        if (agendaItems.isEmpty()) {
            item {
                Text("No data for this day yet.", style = MaterialTheme.typography.bodySmall, color = InkFaint)
            }
        }

        items(agendaItems, key = { it.id }) { item: RoutineItem ->
            val done = item.history[selectedKey] == true
            val main = if (item.color == com.dailyroutine.app.data.model.RoutineColor.TEAL) Teal else Orange
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border),
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(if (done) main else Surface, RoundedCornerShape(8.dp))
                            .then(Modifier),
                    )
                    Column {
                        Text(item.title, style = MaterialTheme.typography.bodyMedium, color = Ink)
                        Text(if (done) "Completed" else "Not completed", style = MaterialTheme.typography.bodySmall, color = InkMuted)
                    }
                }
            }
        }
    }
}
