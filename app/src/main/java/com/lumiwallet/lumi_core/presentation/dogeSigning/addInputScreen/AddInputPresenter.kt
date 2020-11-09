package com.lumiwallet.lumi_core.presentation.dogeSigning.addInputScreen

import com.arellomobile.mvp.InjectViewState
import com.lumiwallet.lumi_core.App
import com.lumiwallet.lumi_core.domain.dogeSigning.AddInputUseCase
import com.lumiwallet.lumi_core.presentation.BasePresenter
import com.lumiwallet.lumi_core.utils.addTo
import com.lumiwallet.lumi_core.utils.androidAsync
import javax.inject.Inject

@InjectViewState
class AddInputPresenter : BasePresenter<AddInputView>() {

    init {
        App.getOrCreateDogeSigningComponent().inject(this)
    }

    @Inject
    lateinit var addInput: AddInputUseCase

    fun onBackClick() {
        viewState.back()
    }

    fun onAddInputClick(
        address: String,
        amount: String,
        outN: String,
        script: String,
        hash: String,
        privateKey: String
    ) {
        addInput(address, amount, outN, script, hash, privateKey)
            .androidAsync()
            .subscribe({
                viewState.back()
            }) {
                it.printStackTrace()
                viewState.showMessage(it.message)
            }
            .addTo(compositeDisposable)

    }
}