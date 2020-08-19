package com.lumiwallet.lumi_core.presentation.btcSigning.mainScreen

import com.arellomobile.mvp.InjectViewState
import com.lumiwallet.lumi_core.App
import com.lumiwallet.lumi_core.domain.btcSigning.*
import com.lumiwallet.lumi_core.domain.entity.HeaderViewModel
import com.lumiwallet.lumi_core.domain.entity.InputViewModel
import com.lumiwallet.lumi_core.domain.entity.Output
import com.lumiwallet.lumi_core.presentation.BasePresenter
import com.lumiwallet.lumi_core.utils.addTo
import com.lumiwallet.lumi_core.utils.androidAsync
import javax.inject.Inject

@InjectViewState
class BtcSigningPresenter : BasePresenter<BtcSigningView>() {

    init {
        App.getOrCreateBtcSigningComponent().inject(this)
    }

    @Inject
    lateinit var addInput: AddInputUseCase
    @Inject
    lateinit var removeInput: RemoveInputUseCase
    @Inject
    lateinit var addOutput: AddOutputUseCase
    @Inject
    lateinit var removeOutput: RemoveOutputUseCase
    @Inject
    lateinit var setFeePerByte: SetFeePerByteUseCase
    @Inject
    lateinit var calculateFee: CalculateFeeUseCase
    @Inject
    lateinit var signBtcTransaction: SignBtcTransactionUseCase
    @Inject
    lateinit var getTransaction: GetTransactionUseCase

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupView()
        getTransaction()
            .androidAsync()
            .subscribe({
                viewState.setData(it)
                onTransactionChanged()
            }) {
                it.printStackTrace()
                viewState.showMessage(it.message)
            }
            .addTo(compositeDisposable)
    }

    override fun onDestroy() {
        App.destroyBtcSigningComponent()
        super.onDestroy()
    }

    fun deleteInput(input: InputViewModel) {
        removeInput(input)
            .androidAsync()
            .subscribe({
                onTransactionChanged()
            }) {
                it.printStackTrace()
                viewState.showMessage(it.message)
            }
            .addTo(compositeDisposable)

    }

    fun deleteOutput(output: Output) {
        removeOutput(output.address)
            .androidAsync()
            .subscribe({
                onTransactionChanged()
            }) {
                it.printStackTrace()
                viewState.showMessage(it.message)
            }
            .addTo(compositeDisposable)
    }

    fun addClick(headerType: Int) {
        when (headerType) {
            HeaderViewModel.HEADER_TYPE_INPUT -> {
                viewState.navigateToAddInputFragment()
            }
            HeaderViewModel.HEADER_TYPE_OUTPUT -> {
                viewState.navigateToAddOutputFragment()
            }
        }
    }

    fun txSignedDialogCollapsed() {
        viewState.clearSignetTransactionFields()
    }

    fun onDoneClicked() {
        viewState.hideSignTransactionDialog()
    }

    fun onBuildClick() {
        signBtcTransaction()
            .androidAsync()
            .subscribe({
                viewState.showRawTransaction(it.hash, it.rawTx)
            }) {
                it.printStackTrace()
                viewState.showMessage(it.message)
            }
            .addTo(compositeDisposable)
    }

    private fun onTransactionChanged() {
        calculateFee()
            .androidAsync()
            .subscribe({
                viewState.showFeePerTx(it.toString())
            }) {
                it.printStackTrace()
                viewState.showMessage(it.message)
            }
            .addTo(compositeDisposable)

    }

    fun onFeeChanged(fee: String) {
        setFeePerByte(fee)
            .andThen(calculateFee(fee))
            .androidAsync()
            .subscribe({
                viewState.showFeePerTx(it.toString())
            }, {
                it.printStackTrace()
            })
            .addTo(compositeDisposable)
    }

    fun onSignTransactionBackClicked() {
        viewState.hideSignTransactionDialog()
    }

    fun onShareClick(hash: String, rawtx: String) {
        viewState.share("hash: $hash \n rawTx: $rawtx")
    }

    fun onInputClick(input: InputViewModel) {
        viewState.navigateToEditInputFragment(input)

    }

    fun onOutputClick(output: Output) {
        viewState.navigateToEditOutputFragment(output)
    }
}