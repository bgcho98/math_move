package com.pinkmandarin.mathmove.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {
    private const val TAG = "AdManager"
    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false

    /**
     * Initialize AdMob with COPPA compliance for child-directed content.
     */
    fun initialize(context: Context) {
        val requestConfiguration = RequestConfiguration.Builder()
            .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        MobileAds.initialize(context) { initializationStatus ->
            Log.d(TAG, "AdMob initialized: ${initializationStatus.adapterStatusMap}")
            loadInterstitialAd(context)
        }
    }

    /**
     * Load an interstitial ad. Uses test ad unit ID during development.
     */
    fun loadInterstitialAd(context: Context) {
        if (isAdLoading || interstitialAd != null) return

        isAdLoading = true

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            Constants.AD_UNIT_ID_INTERSTITIAL_TEST,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded successfully")
                    interstitialAd = ad
                    isAdLoading = false
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Interstitial ad failed to load: ${adError.message}")
                    interstitialAd = null
                    isAdLoading = false
                }
            }
        )
    }

    /**
     * Show an interstitial ad if available.
     * @param activity The activity context to show the ad in.
     * @param onAdDismissed Callback when the ad is dismissed or if no ad is available.
     */
    fun showInterstitialAd(activity: Activity, onAdDismissed: () -> Unit) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad dismissed")
                    interstitialAd = null
                    loadInterstitialAd(activity) // Preload next ad
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Interstitial ad failed to show: ${adError.message}")
                    interstitialAd = null
                    loadInterstitialAd(activity) // Try to load another ad
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad showed")
                }
            }
            ad.show(activity)
        } else {
            Log.d(TAG, "No interstitial ad available, proceeding without ad")
            loadInterstitialAd(activity) // Try to load for next time
            onAdDismissed()
        }
    }

    /**
     * Check if an interstitial ad is ready to show.
     */
    fun isAdReady(): Boolean = interstitialAd != null
}
