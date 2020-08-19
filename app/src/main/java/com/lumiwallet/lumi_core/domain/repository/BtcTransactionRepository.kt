package com.lumiwallet.lumi_core.domain.repository

import com.lumiwallet.lumi_core.domain.entity.Input
import com.lumiwallet.lumi_core.domain.entity.Output
import com.lumiwallet.lumi_core.domain.entity.TransactionDataModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface BtcTransactionRepository {
    fun addInput(input: Input): Completable
    fun addOutput(output: Output): Completable

    fun getInputs(): Observable<MutableList<Input>>
    fun getOutputs(): Observable<MutableList<Output>>

    fun removeInput(script: String): Completable
    fun removeOutput(address: String): Completable

    fun updateInput(input: Input): Completable
    fun updateOutput(output: Output): Completable

    fun setFeePerByte(fee: Long): Completable
    fun getFeePerByte(): Single<Long>

    fun sign(): Single<TransactionDataModel>

}