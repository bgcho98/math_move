package com.pinkmandarin.mathmove.domain.model

data class Stage(
    val number: Int,
    val isUnlocked: Boolean,
    val stars: Int = 0, // 0-3
    val bestTime: Long = 0L
)
