package com.lumiwallet.lumi_core.di

import com.lumiwallet.lumi_core.presentation.bchSigning.di.BchSigningComponent
import com.lumiwallet.lumi_core.presentation.btcSigning.di.BtcSigningComponent
import com.lumiwallet.lumi_core.presentation.derivationScreen.DerivationPresenter
import com.lumiwallet.lumi_core.presentation.dogeSigning.di.DogeSigningComponent
import com.lumiwallet.lumi_core.presentation.eosSigning.EosSigningPresenter
import com.lumiwallet.lumi_core.presentation.generateRandomScreen.GenerateRandomPresenter
import com.lumiwallet.lumi_core.presentation.mainScreen.MainPresenter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppContextModule::class,
        AppModule::class
    ]
)
interface AppComponent {
    fun inject(mainPresenter: MainPresenter)
    fun inject(mainPresenter: DerivationPresenter)
    fun inject(eosSigningPresenter: EosSigningPresenter)
    fun inject(generateRandomPresenter: GenerateRandomPresenter)

    fun btcSigningComponent(): BtcSigningComponent
    fun bchSigningComponent(): BchSigningComponent
    fun dogeSigningComponent(): DogeSigningComponent
}