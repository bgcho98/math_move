package com.pinkmandarin.mathmove.data.model

data class UserData(
    val uid: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val maxClearedStage: Int = 0
) {
    constructor() : this(uid = "")
}
