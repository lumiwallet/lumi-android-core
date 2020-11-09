package com.lumiwallet.lumi_core.domain.dogeSigning

import com.lumiwallet.lumi_core.domain.entity.Input
import com.lumiwallet.lumi_core.domain.entity.Output
import com.lumiwallet.lumi_core.domain.repository.DogeTransactionRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Function3

class CalculateFeeUseCase(
    private val txRepository: DogeTransactionRepository
) {
    companion object {
        private const val INPUT_SIZE = 148
        private const val OUTPUT_SIZE = 34
        private const val SIZE_EXTRA = 10

        fun getTransactionSize(inputs: Int, outputs: Int) =
            (INPUT_SIZE * inputs + OUTPUT_SIZE * outputs + SIZE_EXTRA)
    }

    operator fun invoke(fee: String? = null): Single<Long> = Single.zip(
        txRepository.getInputs().firstOrError(),
        txRepository.getOutputs().firstOrError(),
        txRepository.getFeePerByte(),
        Function3<MutableList<Input>, MutableList<Output>, Long, Long> { inputs, outputs, feePerByte ->
            getTransactionSize(
                inputs.size,
                outputs.size
            ) * (fee?.toLongOrNull() ?: feePerByte)
        }
    )
}