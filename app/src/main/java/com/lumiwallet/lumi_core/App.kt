package com.lumiwallet.lumi_core

import android.app.Application
import com.lumiwallet.lumi_core.di.AppComponent
import com.lumiwallet.lumi_core.di.AppContextModule
import com.lumiwallet.lumi_core.di.DaggerAppComponent
import com.lumiwallet.lumi_core.presentation.bchSigning.di.BchSigningComponent
import com.lumiwallet.lumi_core.presentation.btcSigning.di.BtcSigningComponent
import com.lumiwallet.lumi_core.presentation.dogeSigning.di.DogeSigningComponent

class App : Application() {

    companion object {
        @JvmStatic
        lateinit var appComponent: AppComponent

        @JvmStatic
        var btcSigningComponent: BtcSigningComponent? = null
            private set

        @JvmStatic
        var bchSigningComponent: BchSigningComponent? = null
            private set

        @JvmStatic
        var dogeSigningComponent: DogeSigningComponent? = null
            private set

        fun getOrCreateBtcSigningComponent(): BtcSigningComponent {
            if (btcSigningComponent == null)
                btcSigningComponent = appComponent.btcSigningComponent()
            return btcSigningComponent!!
        }

        fun destroyBtcSigningComponent() {
            btcSigningComponent = null
        }

        fun getOrCreateBchSigningComponent(): BchSigningComponent {
            if (bchSigningComponent == null)
                bchSigningComponent = appComponent.bchSigningComponent()
            return bchSigningComponent!!
        }

        fun destroyBchSigningComponent() {
            bchSigningComponent = null
        }

        fun getOrCreateDogeSigningComponent(): DogeSigningComponent {
            if (dogeSigningComponent == null)
                dogeSigningComponent = appComponent.dogeSigningComponent()
            return dogeSigningComponent!!
        }

        fun destroyDogeSigningComponent() {
            dogeSigningComponent = null
        }
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appContextModule(AppContextModule(this))
            .build()
    }
}