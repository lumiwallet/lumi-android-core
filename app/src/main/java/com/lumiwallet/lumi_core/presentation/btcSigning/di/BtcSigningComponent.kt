package com.lumiwallet.lumi_core.presentation.btcSigning.di

import com.lumiwallet.lumi_core.presentation.btcSigning.addInputScreen.AddInputPresenter
import com.lumiwallet.lumi_core.presentation.btcSigning.addOutputScreen.AddOutputPresenter
import com.lumiwallet.lumi_core.presentation.btcSigning.editInputScreen.EditInputPresenter
import com.lumiwallet.lumi_core.presentation.btcSigning.editOutputScreen.EditOutputPresenter
import com.lumiwallet.lumi_core.presentation.btcSigning.mainScreen.BtcSigningFragment
import com.lumiwallet.lumi_core.presentation.btcSigning.mainScreen.BtcSigningPresenter
import dagger.Subcomponent

@BtcSigningScope
@Subcomponent(modules = [
    BtcSigningModule::class
])
interface BtcSigningComponent {
    fun inject(btcSigningFragment: BtcSigningFragment)
    fun inject(btcSigningFragment: BtcSigningPresenter)
    fun inject(addInputPresenter: AddInputPresenter)
    fun inject(addOutputPresenter: AddOutputPresenter)
    fun inject(editInputPresenter: EditInputPresenter)
    fun inject(editOutputPresenter: EditOutputPresenter)
}