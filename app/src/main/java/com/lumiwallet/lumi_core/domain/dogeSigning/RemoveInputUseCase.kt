package com.lumiwallet.lumi_core.domain.dogeSigning

import com.lumiwallet.lumi_core.domain.entity.InputViewModel
import com.lumiwallet.lumi_core.domain.repository.DogeTransactionRepository
import io.reactivex.rxjava3.core.Completable

class RemoveInputUseCase(
    private val txRepository: DogeTransactionRepository
) {

    operator fun invoke(input: InputViewModel): Completable = txRepository.removeInput(input.script)
}