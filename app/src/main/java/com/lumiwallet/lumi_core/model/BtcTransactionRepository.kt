package com.lumiwallet.lumi_core.model

import com.lumiwallet.android.core.bitcoin.params.MainNetParams
import com.lumiwallet.android.core.bitcoin.transaction.TransactionBuilder
import com.lumiwallet.android.core.bitcoin.transaction.UnspentOutput
import com.lumiwallet.android.core.utils.btc_based.core.PrivateKey
import com.lumiwallet.lumi_core.domain.btcSigning.CalculateFeeUseCase
import com.lumiwallet.lumi_core.domain.entity.Input
import com.lumiwallet.lumi_core.domain.entity.Output
import com.lumiwallet.lumi_core.domain.entity.TransactionDataModel
import com.lumiwallet.lumi_core.domain.repository.BtcTransactionRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

class BtcTransactionRepository : BtcTransactionRepository {
    private val outputs: MutableList<Output> = mutableListOf()
    private val inputs: MutableList<Input> = mutableListOf()
    private val inputsSubject: BehaviorSubject<MutableList<Input>> =
        BehaviorSubject.create()
    private val outputsSubject: BehaviorSubject<MutableList<Output>> = BehaviorSubject.create()
    private var feePerByte: Long = 0L

    init {
        inputsSubject.onNext(inputs)
        outputsSubject.onNext(outputs)
    }

    override fun addInput(input: Input): Completable = Completable.fromCallable {
        inputs.add(input)
        inputsSubject.onNext(inputs)
    }

    override fun addOutput(output: Output): Completable = Completable.fromCallable {
        outputs.add(output)
        outputsSubject.onNext(outputs)
    }

    override fun getInputs(): Observable<MutableList<Input>> = inputsSubject

    override fun getOutputs(): Observable<MutableList<Output>> = outputsSubject

    override fun removeInput(script: String): Completable = Completable.fromCallable {
        val input = inputs.find {
            it.script == script
        }
        inputs.remove(input!!)
        inputsSubject.onNext(inputs)
    }

    override fun removeOutput(address: String): Completable = Completable.fromCallable {
        val output = outputs.find {
            it.address == address
        }
        outputs.remove(output!!)
        outputsSubject.onNext(outputs)
    }

    override fun updateInput(input: Input): Completable = Completable.fromCallable {
        inputsSubject.onNext(inputs)
    }

    override fun updateOutput(output: Output): Completable = Completable.fromCallable {
        outputsSubject.onNext(outputs)
    }

    override fun setFeePerByte(fee: Long): Completable = Completable.fromCallable {
        feePerByte = fee
        Unit
    }

    override fun getFeePerByte(): Single<Long> = Single.just(feePerByte)

    override fun sign(): Single<TransactionDataModel> = Single.fromCallable {
        val transactionBuilder = TransactionBuilder.create()
        for (input in inputs) {
            transactionBuilder.from(
                UnspentOutput(
                    input.txHash,
                    input.txOutputN,
                    input.script,
                    input.value,
                    PrivateKey.ofWif(input.keyAsWif, MainNetParams)
                )
            )
        }
        for (output in outputs) {
            transactionBuilder.to(output.address, output.amount)
        }
        transactionBuilder.withFee(
            CalculateFeeUseCase.getTransactionSize(inputs.size, outputs.size) * feePerByte
        )
        transactionBuilder.build().let {
            TransactionDataModel(it.hash, it.rawTransaction)
        }
    }
}