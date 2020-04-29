package com.lumiwallet.lumi_core.presentation.derivationScreen

import com.arellomobile.mvp.InjectViewState
import com.lumiwallet.android.core.crypto.hd.DeterministicKey
import com.lumiwallet.android.core.crypto.hd.DeterministicSeed
import com.lumiwallet.lumi_core.App
import com.lumiwallet.lumi_core.R
import com.lumiwallet.lumi_core.domain.GetStringUseCase
import com.lumiwallet.lumi_core.domain.derivation.GetKeyByDerivationPathUseCase
import com.lumiwallet.lumi_core.domain.derivation.GetKeysInRangeUseCase
import com.lumiwallet.lumi_core.domain.entity.DerivedKeyViewModel
import com.lumiwallet.lumi_core.presentation.BasePresenter
import com.lumiwallet.lumi_core.utils.addTo
import com.lumiwallet.lumi_core.utils.androidAsync
import javax.inject.Inject

@InjectViewState
class DerivationPresenter(
    private val mnemonic: MutableList<String>
) : BasePresenter<DerivationView>() {

    @Inject
    lateinit var getKeysInRange: GetKeysInRangeUseCase
    @Inject
    lateinit var getKeyByDerivationPath: GetKeyByDerivationPathUseCase
    @Inject
    lateinit var getString: GetStringUseCase

    private var masterKey: DeterministicKey? = null

    init {
        App.appComponent.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showBaseInfo(
            mnemonic.joinToString (separator = " ") { it },
            DeterministicSeed(mnemonic).toHexString()
        )
        getString(R.string.default_div_path)
            .androidAsync()
            .subscribe({ defaultPath ->
                deriveKey(defaultPath)
            }) {
                it.printStackTrace()
                viewState.showMessage(it.message)
            }
            .addTo(compositeDisposable)
    }

    fun onBackClicked() {
        viewState.back()
    }

    fun deriveKey(bip32Path: String) {
        getKeyByDerivationPath(mnemonic, bip32Path)
            .androidAsync()
            .subscribe({ derivedKey ->
                masterKey = derivedKey
                viewState.showDerivedKey(
                    DerivedKeyViewModel(
                        pubKey = derivedKey.publicKeyAsHex,
                        privKey = derivedKey.privateKeyAsHex
                    )
                )
            }) {
                it.printStackTrace()
                viewState.showMessage(it.message)
            }
            .addTo(compositeDisposable)
    }

    fun deriveKeysInRange(
        fromKey: String,
        toKey: String
    ) {
        getKeysInRange(masterKey, fromKey, toKey)
            .androidAsync()
            .subscribe({ keys ->
                viewState.showKeysInRange(keys)
            }) {
                it.printStackTrace()
                viewState.showMessage(it.message)
            }
            .addTo(compositeDisposable)
    }

}