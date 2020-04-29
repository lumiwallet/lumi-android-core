package com.lumiwallet.lumi_core

import android.app.Application
import com.lumiwallet.lumi_core.di.AppComponent
import com.lumiwallet.lumi_core.di.AppContextModule
import com.lumiwallet.lumi_core.di.DaggerAppComponent
import com.lumiwallet.lumi_core.presentation.btcSigning.di.BtcSigningComponent

class App : Application() {

    companion object {
        @JvmStatic
        lateinit var appComponent: AppComponent

        @JvmStatic
        var btcSigningComponent: BtcSigningComponent? = null
            private set

        fun getOrCreateBtcSigningComponent(): BtcSigningComponent {
            if (btcSigningComponent == null)
                btcSigningComponent = appComponent.btcSigningComponent()
            return btcSigningComponent!!
        }

        fun destroyBtcSigningComponent() {
            btcSigningComponent = null
        }
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appContextModule(AppContextModule(this))
            .build()
    }
}