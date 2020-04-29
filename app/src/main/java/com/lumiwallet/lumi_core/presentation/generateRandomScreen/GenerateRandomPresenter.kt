package com.lumiwallet.lumi_core.presentation.generateRandomScreen

import com.arellomobile.mvp.InjectViewState
import com.lumiwallet.lumi_core.App
import com.lumiwallet.lumi_core.domain.generateRandom.GenerateRandomUseCase
import com.lumiwallet.lumi_core.presentation.BasePresenter
import com.lumiwallet.lumi_core.utils.addTo
import com.lumiwallet.lumi_core.utils.androidAsync
import javax.inject.Inject

@InjectViewState
class GenerateRandomPresenter : BasePresenter<GenerateRandomView>() {

    init {
        App.appComponent.inject(this)
    }

    @Inject
    lateinit var generateRandomBytes: GenerateRandomUseCase

    fun onGenerateClick(length: String) {
        generateRandomBytes(length)
            .androidAsync()
            .subscribe({
                viewState.showMessage("generated successfully!")
            }) {
                it.printStackTrace()
                if (it is GenerateRandomUseCase.PermissionDeniedException) {
                    viewState.requestFilesystemPermission()
                } else {
                    viewState.showMessage(it.message)
                }
            }
            .addTo(compositeDisposable)
    }

    fun onPermissionGranted(length: String) {
        onGenerateClick(length)
    }
}