package com.pinkmandarin.mathmove.domain.model

data class GameResult(
    val stageNumber: Int,
    val correctCount: Int,
    val totalCount: Int,
    val timeMillis: Long,
    val stars: Int, // 1-3
    val cleared: Boolean
) {
    val accuracy: Float get() = if (totalCount > 0) correctCount.toFloat() / totalCount else 0f
}
