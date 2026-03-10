package com.pinkmandarin.mathmove.data.local

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate

class DailyPlayManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun canPlay(): Boolean {
        resetIfNewDay()
        return getPlayCount() < MAX_DAILY_PLAYS
    }

    fun getRemainingPlays(): Int {
        resetIfNewDay()
        return (MAX_DAILY_PLAYS - getPlayCount()).coerceAtLeast(0)
    }

    fun incrementPlayCount() {
        resetIfNewDay()
        prefs.edit().putInt(KEY_PLAY_COUNT, getPlayCount() + 1).apply()
    }

    private fun resetIfNewDay() {
        val today = LocalDate.now().toString()
        val savedDate = prefs.getString(KEY_DATE, null)
        if (savedDate != today) {
            prefs.edit()
                .putString(KEY_DATE, today)
                .putInt(KEY_PLAY_COUNT, 0)
                .apply()
        }
    }

    private fun getPlayCount(): Int = prefs.getInt(KEY_PLAY_COUNT, 0)

    companion object {
        private const val PREFS_NAME = "daily_play"
        private const val KEY_DATE = "play_date"
        private const val KEY_PLAY_COUNT = "play_count"
        const val MAX_DAILY_PLAYS = 3
    }
}
