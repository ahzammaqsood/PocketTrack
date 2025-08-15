package com.pockettrack.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.pockettrack.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import android.widget.Toast
import com.pockettrack.R

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var interstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Load banner
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        // Preload interstitial (test ad unit)
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
            }
        )

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(com.pockettrack.R.id.fragment_container, com.pockettrack.ui.transactions.TransactionListFragment())
                .commit()
        }
    }

    fun maybeShowInterstitial() {
        interstitialAd?.show(this) ?: Toast.makeText(this, "Ad not ready", Toast.LENGTH_SHORT).show()
    }
}