package com.lumiwallet.lumi_core.domain.dogeSigning

import com.lumiwallet.lumi_core.domain.entity.Output
import com.lumiwallet.lumi_core.domain.repository.DogeTransactionRepository
import io.reactivex.rxjava3.core.Single

class AddOutputUseCase(
    private val txRepository: DogeTransactionRepository
) {
    operator fun invoke(
        address: String,
        amount: String
    ): Single<Output> = Single.fromCallable {
        Output(address, amount.toLong())
    }.flatMap {
        txRepository.addOutput(it).toSingleDefault(it)
    }
}