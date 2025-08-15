package com.pockettrack

import android.app.Application
import com.google.android.gms.ads.MobileAds

class PocketTrackApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
    }
}