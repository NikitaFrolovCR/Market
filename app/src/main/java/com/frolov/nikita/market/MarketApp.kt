package com.frolov.nikita.market

import android.app.Application
import android.support.annotation.StringRes
import com.cleveroad.bootstrap.kotlin_core.utils.misc.MiscellaneousUtils
import com.facebook.stetho.Stetho
import com.frolov.nikita.market.database.DatabaseCreator

class MarketApp : Application() {
    companion object {
        lateinit var instance: MarketApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        DatabaseCreator.createDb(this)
        MiscellaneousUtils.defaultPackageName = packageName
        Stetho.initializeWithDefaults(this)
    }

}

fun getStringApp(@StringRes stringId: Int) = MarketApp.instance.getString(stringId)