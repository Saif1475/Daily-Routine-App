package com.dailyroutine.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dailyroutine.app.ui.navigation.RoutineNavGraph
import com.dailyroutine.app.ui.theme.DailyRoutineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The app always renders a light background, so pin dark (visible) status/nav bar
        // icons regardless of the system theme instead of letting them auto-flip to white.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            DailyRoutineTheme {
                RoutineNavGraph()
            }
        }
    }
}
