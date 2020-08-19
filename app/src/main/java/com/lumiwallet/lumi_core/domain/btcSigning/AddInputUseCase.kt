package com.lumiwallet.lumi_core.domain.btcSigning

import com.lumiwallet.lumi_core.domain.entity.Input
import com.lumiwallet.lumi_core.domain.repository.BtcTransactionRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class AddInputUseCase(
    private val txRepository: BtcTransactionRepository
) {

    operator fun invoke(
        address: String,
        amount: String,
        outN: String,
        script: String,
        hash: String,
        privateKey: String
    ): Completable = Single.fromCallable {
        if (privateKey.isEmpty()) throw IllegalArgumentException("please enter private key first")

        Input(
            txHash = hash,
            txOutputN = outN.toInt(),
            script = script,
            value = amount.toLong(),
            address =  address,
            keyAsWif = privateKey
        )
    }.flatMapCompletable { input ->
        txRepository.addInput(input)
    }
}