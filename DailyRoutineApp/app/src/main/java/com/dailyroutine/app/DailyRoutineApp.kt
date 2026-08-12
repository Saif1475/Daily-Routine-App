package com.dailyroutine.app

import android.app.Application
import com.google.firebase.FirebaseApp

class DailyRoutineApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
