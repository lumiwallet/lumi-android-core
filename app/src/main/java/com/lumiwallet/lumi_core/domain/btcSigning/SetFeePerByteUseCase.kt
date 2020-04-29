package com.lumiwallet.lumi_core.domain.btcSigning

import com.lumiwallet.lumi_core.domain.repository.BtcTransactionRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class SetFeePerByteUseCase(
    private val txRepository: BtcTransactionRepository
) {

    operator fun invoke(feePerByte: String): Completable =
        Single.fromCallable {
            feePerByte.toLong()
        }.flatMapCompletable {
            txRepository.setFeePerByte(it)
        }
}