package com.lumiwallet.lumi_core.presentation.dogeSigning.editOutputScreen

import com.arellomobile.mvp.InjectViewState
import com.lumiwallet.lumi_core.App
import com.lumiwallet.lumi_core.domain.dogeSigning.UpdateOutputUseCase
import com.lumiwallet.lumi_core.domain.entity.Output
import com.lumiwallet.lumi_core.presentation.BasePresenter
import com.lumiwallet.lumi_core.utils.addTo
import com.lumiwallet.lumi_core.utils.androidAsync
import javax.inject.Inject

@InjectViewState
class EditOutputPresenter(
    private val oldOutput: Output
): BasePresenter<EditOutputView>() {

    init {
        App.getOrCreateDogeSigningComponent().inject(this)
    }

    @Inject
    lateinit var updateOutput: UpdateOutputUseCase

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showOutput(
            oldOutput.address,
            oldOutput.amount.toString()
        )
    }

    fun onSaveClick(
        address: String,
        amount: String
    ) {
        updateOutput(oldOutput, address, amount)
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