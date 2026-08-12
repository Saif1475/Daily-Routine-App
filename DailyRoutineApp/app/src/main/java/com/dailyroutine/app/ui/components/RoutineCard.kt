package com.dailyroutine.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.dailyroutine.app.data.model.RoutineColor
import com.dailyroutine.app.data.model.RoutineItem
import com.dailyroutine.app.data.model.RoutineType
import com.dailyroutine.app.ui.theme.Border
import com.dailyroutine.app.ui.theme.Ink
import com.dailyroutine.app.ui.theme.InkMuted
import com.dailyroutine.app.ui.theme.Orange
import com.dailyroutine.app.ui.theme.Surface
import com.dailyroutine.app.ui.theme.Teal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun RoutineColor.main(): Color = if (this == RoutineColor.TEAL) Teal else Orange

@Composable
fun RoutineCard(item: RoutineItem, onToggle: () -> Unit, onOpen: () -> Unit) {
    val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    val checked = item.history[today] == true
    val main = item.color.main()

    Card(
        onClick = onOpen,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(main, checked)
                    .border(BorderStroke(2.dp, main), RoundedCornerShape(9.dp))
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center,
            ) {
                if (checked) Icon(Icons.Filled.Check, contentDescription = "Completed", tint = Color.White, modifier = Modifier.size(15.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink,
                    textDecoration = if (checked) TextDecoration.LineThrough else null,
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    if (item.type == RoutineType.HABIT) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = main, modifier = Modifier.size(10.dp))
                        Text("${item.streak} day streak", style = MaterialTheme.typography.bodySmall, color = InkMuted)
                    } else if (item.time.isNotBlank()) {
                        Text(item.time, style = MaterialTheme.typography.bodySmall, color = InkMuted)
                    }
                }
            }
        }
    }
}

private fun Modifier.clip(main: Color, checked: Boolean): Modifier =
    this.background(if (checked) main else Color.White, RoundedCornerShape(9.dp))
