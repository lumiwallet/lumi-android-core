package com.lumiwallet.lumi_core.presentation.bchSigning.di

import com.lumiwallet.lumi_core.presentation.bchSigning.addInputScreen.AddInputPresenter
import com.lumiwallet.lumi_core.presentation.bchSigning.addOutputScreen.AddOutputPresenter
import com.lumiwallet.lumi_core.presentation.bchSigning.editInputScreen.EditInputPresenter
import com.lumiwallet.lumi_core.presentation.bchSigning.editOutputScreen.EditOutputPresenter
import com.lumiwallet.lumi_core.presentation.bchSigning.mainScreen.BchSigningFragment
import com.lumiwallet.lumi_core.presentation.bchSigning.mainScreen.BchSigningPresenter

import dagger.Subcomponent

@BchSigningScope
@Subcomponent(modules = [
    BchSigningModule::class
])
interface BchSigningComponent {
    fun inject(bthSigningFragment: BchSigningFragment)
    fun inject(bthSigningFragment: BchSigningPresenter)
    fun inject(addInputPresenter: AddInputPresenter)
    fun inject(addOutputPresenter: AddOutputPresenter)
    fun inject(editInputPresenter: EditInputPresenter)
    fun inject(editOutputPresenter: EditOutputPresenter)
}