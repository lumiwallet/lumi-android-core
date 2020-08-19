package com.lumiwallet.lumi_core.domain.btcSigning

import com.lumiwallet.lumi_core.domain.entity.Input
import com.lumiwallet.lumi_core.domain.entity.InputViewModel
import com.lumiwallet.lumi_core.domain.repository.BtcTransactionRepository
import io.reactivex.rxjava3.core.Single

class GetInputUseCase(
    private val txRepository: BtcTransactionRepository
) {

    operator fun invoke(
        inputViewModel: InputViewModel
    ): Single<Input> = txRepository.getInputs()
        .firstOrError()
        .map { inputs ->
            inputs.find {
                it.address == inputViewModel.address &&
                        it.script == inputViewModel.script &&
                        it.txHash == inputViewModel.txHash
            } ?: throw NullPointerException()
        }
}