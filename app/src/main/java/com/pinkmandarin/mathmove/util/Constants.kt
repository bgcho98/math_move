package com.pinkmandarin.mathmove.util

object Constants {
    // Game Settings
    const val PROBLEMS_PER_STAGE = 5
    const val INITIAL_LIVES = 3
    const val POSE_HOLD_DURATION = 1500L // milliseconds
    const val DEFAULT_PROBLEM_TIME = 60_000L // 60 seconds per problem
    const val MIN_PROBLEM_TIME = 30_000L // minimum 30 seconds for harder stages

    // Stars Calculation
    const val THREE_STAR_TIME_FACTOR = 0.5 // complete within 50% of total time
    const val TWO_STAR_TIME_FACTOR = 0.75 // complete within 75% of total time

    // AdMob
    const val AD_UNIT_ID_INTERSTITIAL_TEST = "ca-app-pub-3940256099942544/1033173712"
    const val AD_UNIT_ID_INTERSTITIAL_PROD = "" // Replace with production ad unit ID
    const val AD_UNIT_ID_REWARDED_EXTRA_STAGE = "ca-app-pub-4390370436238752/1776954081"

    // Daily Play Limit
    const val DAILY_FREE_PLAYS = 3

    // Firebase Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_STAGE_RECORDS = "stageRecords"
    const val COLLECTION_RECORDS = "records"
    const val COLLECTION_RANKINGS = "rankings"
    const val COLLECTION_STAGES = "stages"
    const val COLLECTION_GLOBAL = "global"
    const val COLLECTION_ENTRIES = "entries"

    // Navigation Routes
    const val ROUTE_SPLASH = "splash"
    const val ROUTE_LOGIN = "login"
    const val ROUTE_HOME = "home"
    const val ROUTE_GAME = "game/{stageNumber}"
    const val ROUTE_RESULT = "result/{stageNumber}/{correctCount}/{totalCount}/{timeMillis}"
    const val ROUTE_RANKING = "ranking"
    const val ROUTE_SETTINGS = "settings"

    // Navigation Arguments
    const val ARG_STAGE_NUMBER = "stageNumber"
    const val ARG_CORRECT_COUNT = "correctCount"
    const val ARG_TOTAL_COUNT = "totalCount"
    const val ARG_TIME_MILLIS = "timeMillis"

    // Pose Detection
    const val POSE_CONFIDENCE_THRESHOLD = 0.6f
    const val HAND_RAISE_Y_THRESHOLD = 50f // pixels above shoulder
    const val FOOT_RAISE_Y_THRESHOLD = 80f // pixels above normal position

    // Splash
    const val SPLASH_DELAY = 2000L

    // Ranking
    const val RANKING_PAGE_SIZE = 50
}
