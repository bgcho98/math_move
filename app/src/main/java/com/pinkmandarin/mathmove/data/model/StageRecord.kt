package com.pinkmandarin.mathmove.data.model

data class StageRecord(
    val stageNumber: Int = 0,
    val stars: Int = 0,
    val bestTime: Long = 0L,
    val correctCount: Int = 0,
    val totalCount: Int = 0,
    val cleared: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
) {
    // No-arg constructor required for Firestore deserialization
    constructor() : this(stageNumber = 0)
}
