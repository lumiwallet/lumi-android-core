package com.lumiwallet.lumi_core.domain.dogeSigning

import com.lumiwallet.lumi_core.domain.entity.Output
import com.lumiwallet.lumi_core.domain.repository.DogeTransactionRepository
import io.reactivex.rxjava3.core.Completable

class UpdateOutputUseCase (
    private val txRepository: DogeTransactionRepository
) {

    operator fun invoke(
        oldOutput: Output,
        address: String,
        amount: String
    ): Completable = txRepository.getOutputs()
        .firstOrError()
        .map { outputs ->
            outputs.find {
                it.address == oldOutput.address && it.amount == oldOutput.amount
            }?.apply {
                this.address = address
                this.amount = amount.toLongOrNull() ?: 0L
            }
        }
        .flatMapCompletable {
            txRepository.updateOutput(it!!)
        }
}