package com.dailyroutine.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dailyroutine.app.ui.navigation.RoutineNavGraph
import com.dailyroutine.app.ui.theme.DailyRoutineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyRoutineTheme {
                RoutineNavGraph()
            }
        }
    }
}
