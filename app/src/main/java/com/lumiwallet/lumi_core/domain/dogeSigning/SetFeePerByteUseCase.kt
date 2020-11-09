package com.lumiwallet.lumi_core.domain.dogeSigning

import com.lumiwallet.lumi_core.domain.repository.DogeTransactionRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class SetFeePerByteUseCase(
    private val txRepository: DogeTransactionRepository
) {

    operator fun invoke(feePerByte: String): Completable =
        Single.fromCallable {
            feePerByte.toLong()
        }.flatMapCompletable {
            txRepository.setFeePerByte(it)
        }
}