package com.lumiwallet.lumi_core.domain.btcSigning

import com.lumiwallet.android.core.bitcoin.core.PrivateKey
import com.lumiwallet.android.core.bitcoin.transaction.UnspentOutput
import com.lumiwallet.lumi_core.domain.entity.InputViewModel
import com.lumiwallet.lumi_core.domain.repository.BtcTransactionRepository
import io.reactivex.rxjava3.core.Completable

class UpdateInputUseCase(
    private val txRepository: BtcTransactionRepository
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
                        it.unspentOutput.script == oldInput.script &&
                        it.unspentOutput.txHash == oldInput.txHash
            }?.apply {
                this.keyAsWif = privateKey
                this.address = address
                this.unspentOutput = UnspentOutput(
                    txHash = hash,
                    txOutputN = outN.toIntOrNull() ?: 0,
                    script = script,
                    value = amount.toLongOrNull() ?: 0L,
                    privateKey = PrivateKey.ofWif(privateKey)
                )
            }
        }
        .flatMapCompletable {
            txRepository.updateInput(it!!)
        }
}