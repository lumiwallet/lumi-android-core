package com.lumiwallet.lumi_core.presentation.dogeSigning.addOutputScreen

import com.arellomobile.mvp.InjectViewState
import com.lumiwallet.lumi_core.App
import com.lumiwallet.lumi_core.domain.dogeSigning.AddOutputUseCase
import com.lumiwallet.lumi_core.presentation.BasePresenter
import com.lumiwallet.lumi_core.utils.addTo
import com.lumiwallet.lumi_core.utils.androidAsync
import javax.inject.Inject

@InjectViewState
class AddOutputPresenter : BasePresenter<AddOutputView>() {

    init {
        App.getOrCreateDogeSigningComponent().inject(this)
    }

    @Inject
    lateinit var addOutput: AddOutputUseCase

    fun onAddCLick(
        address: String,
        amount: String
    ) {
        addOutput(address, amount)
            .androidAsync()
            .subscribe({
                viewState.back()
            }) {
                it.printStackTrace()
                viewState.showMessage(it.message)
            }
            .addTo(compositeDisposable)

    }

    fun onBackClick() {
        viewState.back()
    }
}