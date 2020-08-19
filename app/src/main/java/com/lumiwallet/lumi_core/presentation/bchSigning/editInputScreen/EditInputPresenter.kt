package com.lumiwallet.lumi_core.presentation.bchSigning.editInputScreen

import com.arellomobile.mvp.InjectViewState
import com.lumiwallet.lumi_core.App
import com.lumiwallet.lumi_core.domain.bchSigning.GetInputUseCase
import com.lumiwallet.lumi_core.domain.bchSigning.UpdateInputUseCase
import com.lumiwallet.lumi_core.domain.entity.InputViewModel
import com.lumiwallet.lumi_core.presentation.BasePresenter
import com.lumiwallet.lumi_core.utils.addTo
import com.lumiwallet.lumi_core.utils.androidAsync
import javax.inject.Inject

@InjectViewState
class EditInputPresenter(
    private val oldInputViewModel: InputViewModel
) : BasePresenter<EditInputView>() {

    init {
        App.getOrCreateBchSigningComponent().inject(this)
    }

    @Inject
    lateinit var getInput: GetInputUseCase
    @Inject
    lateinit var updateInput: UpdateInputUseCase

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getInput(oldInputViewModel)
            .androidAsync()
            .subscribe({ input ->
                viewState.showInput(
                    input.address,
                    input.value.toString(),
                    input.txOutputN.toString(),
                    input.txHash,
                    input.script,
                    input.keyAsWif
                )
            }) {
                it.printStackTrace()
                viewState.showMessage(it.message)
            }
            .addTo(compositeDisposable)
    }

    fun onSaveClick(
        address: String,
        amount: String,
        outN: String,
        script: String,
        hash: String,
        privateKey: String
    ) {
        updateInput(oldInputViewModel, address, amount, outN, script, hash, privateKey)
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