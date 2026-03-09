package com.pinkmandarin.mathmove.data.model

data class RankingEntry(
    val uid: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val maxClearedStage: Int = 0,
    val totalStars: Int = 0,
    val bestTime: Long = 0L,
    val avgBestTime: Long = 0L,
    val stageNumber: Int = 0,
    val stars: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
) {
    constructor() : this(uid = "")
}
