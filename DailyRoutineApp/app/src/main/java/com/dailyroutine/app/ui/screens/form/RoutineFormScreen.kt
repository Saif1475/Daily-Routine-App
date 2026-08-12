package com.dailyroutine.app.ui.screens.form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dailyroutine.app.data.model.RoutineCategory
import com.dailyroutine.app.data.model.RoutineColor
import com.dailyroutine.app.data.model.RoutineType
import com.dailyroutine.app.ui.screens.home.RoutineViewModel
import com.dailyroutine.app.ui.theme.Background
import com.dailyroutine.app.ui.theme.Border
import com.dailyroutine.app.ui.theme.Ink
import com.dailyroutine.app.ui.theme.InkFaint
import com.dailyroutine.app.ui.theme.InkMuted
import com.dailyroutine.app.ui.theme.Orange
import com.dailyroutine.app.ui.theme.OrangeDeep
import com.dailyroutine.app.ui.theme.Surface
import com.dailyroutine.app.ui.theme.Teal
import com.dailyroutine.app.ui.theme.TealDeep

/** [routineId] null means "create new"; non-null means "edit existing". */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoutineFormScreen(
    routineId: String?,
    viewModel: RoutineViewModel,
    onDone: () -> Unit,
) {
    val items by viewModel.routines.collectAsState()
    val existing = routineId?.let { id -> items.firstOrNull { it.id == id } }

    var title by remember(existing) { mutableStateOf(existing?.title ?: "") }
    var type by remember(existing) { mutableStateOf(existing?.type ?: RoutineType.HABIT) }
    var category by remember(existing) { mutableStateOf(existing?.category ?: RoutineCategory.MORNING) }
    var time by remember(existing) { mutableStateOf(existing?.time ?: "") }
    var color by remember(existing) { mutableStateOf(existing?.color ?: RoutineColor.TEAL) }

    val canSave = title.isNotBlank()

    fun save() {
        if (!canSave) return
        if (existing != null) {
            viewModel.updateRoutine(existing, title, type, category, time, color)
        } else {
            viewModel.addRoutine(title, type, category, time, color)
        }
        onDone()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Cancel", style = MaterialTheme.typography.bodyMedium, color = InkMuted, modifier = Modifier.clickable { onDone() })
            Text(if (existing != null) "Edit routine" else "New routine", style = MaterialTheme.typography.titleMedium, color = Ink)
            Text(
                "Save",
                style = MaterialTheme.typography.bodyMedium,
                color = if (canSave) Teal else InkFaint,
                modifier = Modifier.clickable(enabled = canSave) { save() },
            )
        }
        Spacer(Modifier.size(24.dp))

        FormLabel("TITLE")
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("e.g. Drink a glass of water") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Surface, unfocusedContainerColor = Surface),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.size(18.dp))

        FormLabel("TYPE")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Border, RoundedCornerShape(12.dp))
                .padding(3.dp),
        ) {
            TabChip("Habit", type == RoutineType.HABIT, Modifier.weight(1f)) { type = RoutineType.HABIT }
            TabChip("Task", type == RoutineType.TASK, Modifier.weight(1f)) { type = RoutineType.TASK }
        }
        Spacer(Modifier.size(18.dp))

        FormLabel("CATEGORY")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RoutineCategory.entries.forEach { cat ->
                val active = category == cat
                Box(
                    modifier = Modifier
                        .background(if (active) Teal else Surface, RoundedCornerShape(50))
                        .border(BorderStroke(1.dp, if (active) Teal else Border), RoundedCornerShape(50))
                        .clickable { category = cat }
                        .padding(horizontal = 16.dp, vertical = 9.dp),
                ) {
                    Text(cat.label, style = MaterialTheme.typography.bodySmall, color = if (active) Surface else Ink)
                }
            }
        }
        Spacer(Modifier.size(18.dp))

        FormLabel("TIME (OPTIONAL)")
        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            placeholder = { Text("e.g. 7:00 AM") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Surface, unfocusedContainerColor = Surface),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.size(20.dp))

        FormLabel("COLOR")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ColorDot(Teal, TealDeep, color == RoutineColor.TEAL) { color = RoutineColor.TEAL }
            ColorDot(Orange, OrangeDeep, color == RoutineColor.ORANGE) { color = RoutineColor.ORANGE }
        }
        Spacer(Modifier.size(28.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (canSave) Teal else Border, RoundedCornerShape(16.dp))
                .clickable(enabled = canSave) { save() }
                .padding(15.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                if (existing != null) "Save changes" else "Add routine",
                color = Surface,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelSmall, color = InkMuted, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun TabChip(label: String, active: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .background(if (active) Surface else Color.Transparent, RoundedCornerShape(9.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = if (active) Ink else InkMuted)
    }
}

@Composable
private fun ColorDot(main: Color, ring: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .background(main, CircleShape)
            .border(BorderStroke(3.dp, if (selected) ring else Color.Transparent), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        if (selected) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
    }
}
