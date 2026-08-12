package com.dailyroutine.app.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dailyroutine.app.ui.theme.Border
import com.dailyroutine.app.ui.theme.Ink
import com.dailyroutine.app.ui.theme.InkFaint
import com.dailyroutine.app.ui.theme.InkMuted
import com.dailyroutine.app.ui.theme.Orange
import com.dailyroutine.app.ui.theme.Red
import com.dailyroutine.app.ui.theme.Surface
import com.dailyroutine.app.ui.theme.Teal
import com.google.firebase.auth.FirebaseUser

@Composable
fun SettingsScreen(user: FirebaseUser?, onSignOut: () -> Unit) {
    var notifications by remember { mutableStateOf(true) }
    var sound by remember { mutableStateOf(false) }

    val displayName = user?.displayName?.takeIf { it.isNotBlank() } ?: user?.email?.substringBefore("@") ?: "Guest"

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 100.dp)) {
        item {
            Text("Settings", style = MaterialTheme.typography.headlineSmall, color = Ink, modifier = Modifier.padding(bottom = 18.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.size(52.dp).background(Orange, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(displayName.take(1).uppercase(), color = Surface, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(displayName, style = MaterialTheme.typography.titleMedium, color = Ink)
                        Text(user?.email ?: "", style = MaterialTheme.typography.bodySmall, color = InkMuted)
                    }
                }
            }
            Spacer(Modifier.size(22.dp))

            Text("PREFERENCES", style = MaterialTheme.typography.titleSmall, color = InkFaint, modifier = Modifier.padding(bottom = 10.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column {
                    SettingRow("Notifications") {
                        Switch(
                            checked = notifications,
                            onCheckedChange = { notifications = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = Teal),
                        )
                    }
                    SettingRow("Sound & haptics") {
                        Switch(
                            checked = sound,
                            onCheckedChange = { sound = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = Teal),
                        )
                    }
                }
            }
            Spacer(Modifier.size(22.dp))

            Text("ACCOUNT", style = MaterialTheme.typography.titleSmall, color = InkFaint, modifier = Modifier.padding(bottom = 10.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSignOut() }
                            .padding(16.dp),
                    ) {
                        Text("Sign out", style = MaterialTheme.typography.bodyMedium, color = Red)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingRow(label: String, trailing: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Ink, modifier = Modifier.weight(1f))
        trailing()
    }
}
