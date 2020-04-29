package com.lumiwallet.lumi_core.presentation.mainScreen

import com.arellomobile.mvp.InjectViewState
import com.lumiwallet.lumi_core.App
import com.lumiwallet.lumi_core.domain.derivation.CheckMnemonicUseCase
import com.lumiwallet.lumi_core.domain.derivation.GenerateMnemonicUseCase
import com.lumiwallet.lumi_core.domain.derivation.GetAvailableMnemonicSizesUseCase
import com.lumiwallet.lumi_core.presentation.BasePresenter
import com.lumiwallet.lumi_core.utils.addTo
import com.lumiwallet.lumi_core.utils.androidAsync
import javax.inject.Inject

@InjectViewState
class MainPresenter : BasePresenter<MainView>() {

    companion object {
        private const val DEFAULT_MNEMONIC_SIZE = 12
    }

    init {
        App.appComponent.inject(this)
    }

    @Inject
    lateinit var generateMnemonic: GenerateMnemonicUseCase
    @Inject
    lateinit var getMnemonicSizes: GetAvailableMnemonicSizesUseCase
    @Inject
    lateinit var checkMnemonic: CheckMnemonicUseCase

    fun onCreateMnemonicClick() {
        getMnemonicSizes()
            .androidAsync()
            .subscribe({ mnemonicSizes ->
                viewState.showCreateBottomSheet(mnemonicSizes)
            }) {
                viewState.showMessage(it.message)
                it.printStackTrace()
            }
            .addTo(compositeDisposable)
    }

    fun onSelectingDoneClicked(mnemonicSize: String) {
        generateMnemonic(mnemonicSize.toIntOrNull() ?: DEFAULT_MNEMONIC_SIZE)
            .androidAsync()
            .subscribe({ mnemonic ->
                viewState.hideCreateBottomSheet()
                viewState.navigateToDerivationFragment(mnemonic)
            }) {
                it.printStackTrace()
                viewState.showMessage(it.message)
            }
            .addTo(compositeDisposable)

    }

    fun onImportConfirmClicked(mnemonic: String) {
        checkMnemonic(mnemonic)
            .androidAsync()
            .subscribe({ validatedMnemonic ->
                viewState.hideImportBottomSheet()
                viewState.navigateToDerivationFragment(validatedMnemonic)
            }) {
                it.printStackTrace()
                viewState.showMessage(it.message)
            }
            .addTo(compositeDisposable)

    }

    fun onImportButtonClicked() {
        viewState.showImportBottomSheet()
    }

    fun onClickOutsideOfCreateBottomSheet() {
        viewState.hideCreateBottomSheet()
    }

    fun onImportBackClicked() {
        viewState.hideImportBottomSheet()
    }

    fun onAboutClicked() {
        viewState.navigateToAboutScreen()
    }

    fun onGenerateClick() {
        viewState.navigateToGenerateRandomBytesFragment()
    }

}