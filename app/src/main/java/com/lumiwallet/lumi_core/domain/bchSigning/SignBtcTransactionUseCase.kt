package com.lumiwallet.lumi_core.domain.bchSigning

import com.lumiwallet.lumi_core.domain.entity.TransactionDataModel
import com.lumiwallet.lumi_core.domain.repository.BtcTransactionRepository
import io.reactivex.rxjava3.core.Single

class SignBtcTransactionUseCase(
    private val txRepository: BtcTransactionRepository
) {

    operator fun invoke(): Single<TransactionDataModel> = txRepository.sign()
}