package com.lumiwallet.lumi_core.domain.dogeSigning

import com.lumiwallet.lumi_core.domain.entity.TransactionDataModel
import com.lumiwallet.lumi_core.domain.repository.DogeTransactionRepository
import io.reactivex.rxjava3.core.Single

class SignDogeTransactionUseCase(
    private val txRepository: DogeTransactionRepository
) {

    operator fun invoke(): Single<TransactionDataModel> = txRepository.sign()
}