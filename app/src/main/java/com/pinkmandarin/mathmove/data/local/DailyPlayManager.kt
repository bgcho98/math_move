package com.pinkmandarin.mathmove.data.local

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate

class DailyPlayManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun canPlay(): Boolean {
        if (isPremium()) return true
        resetIfNewDay()
        return getPlayCount() < MAX_DAILY_PLAYS + getBonusPlays()
    }

    fun getRemainingPlays(): Int {
        if (isPremium()) return Int.MAX_VALUE
        resetIfNewDay()
        return (MAX_DAILY_PLAYS + getBonusPlays() - getPlayCount()).coerceAtLeast(0)
    }

    fun incrementPlayCount() {
        if (isPremium()) return
        resetIfNewDay()
        prefs.edit().putInt(KEY_PLAY_COUNT, getPlayCount() + 1).apply()
    }

    fun addBonusPlays(count: Int) {
        resetIfNewDay()
        prefs.edit().putInt(KEY_BONUS_PLAYS, getBonusPlays() + count).apply()
    }

    fun isPremium(): Boolean = prefs.getBoolean(KEY_PREMIUM, false)

    fun setPremium(premium: Boolean) {
        prefs.edit().putBoolean(KEY_PREMIUM, premium).apply()
    }

    private fun resetIfNewDay() {
        val today = LocalDate.now().toString()
        val savedDate = prefs.getString(KEY_DATE, null)
        if (savedDate != today) {
            prefs.edit()
                .putString(KEY_DATE, today)
                .putInt(KEY_PLAY_COUNT, 0)
                .putInt(KEY_BONUS_PLAYS, 0)
                .apply()
        }
    }

    private fun getPlayCount(): Int = prefs.getInt(KEY_PLAY_COUNT, 0)

    private fun getBonusPlays(): Int = prefs.getInt(KEY_BONUS_PLAYS, 0)

    companion object {
        private const val PREFS_NAME = "daily_play"
        private const val KEY_DATE = "play_date"
        private const val KEY_PLAY_COUNT = "play_count"
        private const val KEY_BONUS_PLAYS = "bonus_plays"
        private const val KEY_PREMIUM = "premium"
        const val MAX_DAILY_PLAYS = 3
    }
}
