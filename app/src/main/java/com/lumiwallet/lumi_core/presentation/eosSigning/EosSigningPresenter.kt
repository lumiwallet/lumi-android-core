package com.lumiwallet.lumi_core.presentation.eosSigning

import com.arellomobile.mvp.InjectViewState
import com.lumiwallet.lumi_core.App
import com.lumiwallet.lumi_core.domain.eosSigning.SignEos
import com.lumiwallet.lumi_core.presentation.BasePresenter
import com.lumiwallet.lumi_core.utils.addTo
import com.lumiwallet.lumi_core.utils.androidAsync
import javax.inject.Inject

@InjectViewState
class EosSigningPresenter : BasePresenter<EosSigningView>() {

    init {
        App.appComponent.inject(this)
    }

    @Inject
    lateinit var signEos: SignEos

    fun txSignedDialogCollapsed() {
        viewState.clearSignetTransactionFields()
    }

    fun onDoneClicked() {
        viewState.hideSignTransactionDialog()
    }

    fun onShareClick(packedTx: String, signatures: String) {
        viewState.share("packed transaction: $packedTx \n signatures: $signatures")
    }


    fun onBuildClick(
        address: String,
        amount: String,
        memo: String,
        actor: String,
        permission: String,
        expiration: String,
        bin: String,
        blockPrefix: String,
        blockNum: String,
        chainId: String,
        privateKey: String
    ) {
        signEos(
            address,
            amount,
            memo,
            actor,
            permission,
            expiration,
            bin,
            blockPrefix,
            blockNum,
            chainId,
            privateKey
        )
            .androidAsync()
            .subscribe({
                viewState.showRawTransaction(
                    it.packedTx,
                    it.signatures
                )
            }) {
                viewState.showMessage(it.message)
            }
            .addTo(compositeDisposable)
    }
}