package com.lumiwallet.lumi_core.presentation.dogeSigning.di

import com.lumiwallet.lumi_core.presentation.dogeSigning.addInputScreen.AddInputPresenter
import com.lumiwallet.lumi_core.presentation.dogeSigning.addOutputScreen.AddOutputPresenter
import com.lumiwallet.lumi_core.presentation.dogeSigning.editInputScreen.EditInputPresenter
import com.lumiwallet.lumi_core.presentation.dogeSigning.editOutputScreen.EditOutputPresenter
import com.lumiwallet.lumi_core.presentation.dogeSigning.mainScreen.DogeSigningFragment
import com.lumiwallet.lumi_core.presentation.dogeSigning.mainScreen.DogeSigningPresenter
import dagger.Subcomponent

@DogeSigningScope
@Subcomponent(modules = [
    DogeSigningModule::class
])
interface DogeSigningComponent {
    fun inject(btcSigningFragment: DogeSigningFragment)
    fun inject(btcSigningFragment: DogeSigningPresenter)
    fun inject(addInputPresenter: AddInputPresenter)
    fun inject(addOutputPresenter: AddOutputPresenter)
    fun inject(editInputPresenter: EditInputPresenter)
    fun inject(editOutputPresenter: EditOutputPresenter)
}