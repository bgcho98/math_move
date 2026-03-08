package com.pinkmandarin.mathmove

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MathMoveApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize any app-level dependencies here
    }
}
