package com.frolov.nikita.market

import android.app.Application
import com.cleveroad.bootstrap.kotlin_core.utils.misc.MiscellaneousUtils
import com.facebook.stetho.Stetho

class MarketApp : Application() {
    companion object {
        lateinit var instance: MarketApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
//        DatabaseCreator.createDb(this)
        MiscellaneousUtils.defaultPackageName = packageName
        Stetho.initializeWithDefaults(this)
    }

}