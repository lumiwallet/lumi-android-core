package com.lumiwallet.lumi_core.domain.btcSigning

import com.lumiwallet.android.core.bitcoin.transaction.Transaction
import com.lumiwallet.lumi_core.domain.repository.BtcTransactionRepository
import io.reactivex.rxjava3.core.Single

class SignBtcTransactionUseCase(
    private val txRepository: BtcTransactionRepository
) {

    operator fun invoke(): Single<Transaction> = txRepository.sign()
}