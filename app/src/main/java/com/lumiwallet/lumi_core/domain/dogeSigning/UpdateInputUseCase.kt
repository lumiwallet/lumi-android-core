package com.lumiwallet.lumi_core.domain.dogeSigning

import com.lumiwallet.lumi_core.domain.entity.InputViewModel
import com.lumiwallet.lumi_core.domain.repository.DogeTransactionRepository
import io.reactivex.rxjava3.core.Completable

class UpdateInputUseCase(
    private val txRepository: DogeTransactionRepository
) {

    operator fun invoke(
        oldInput: InputViewModel,
        address: String,
        amount: String,
        outN: String,
        script: String,
        hash: String,
        privateKey: String
    ): Completable = txRepository.getInputs()
        .firstOrError()
        .map { inputs ->
            inputs.find {
                it.address == oldInput.address &&
                        it.script == oldInput.script &&
                        it.txHash == oldInput.txHash
            }?.apply {
                this.keyAsWif = privateKey
                this.address = address
                this.script = script
                this.txHash = hash
                this.txOutputN = outN.toIntOrNull() ?: 0
                this.value = amount.toLongOrNull() ?: 0L
            }
        }
        .flatMapCompletable {
            txRepository.updateInput(it!!)
        }
}