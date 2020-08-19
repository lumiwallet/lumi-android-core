package com.lumiwallet.lumi_core.domain.bchSigning

import com.lumiwallet.lumi_core.domain.entity.InputViewModel
import com.lumiwallet.lumi_core.domain.repository.BtcTransactionRepository
import io.reactivex.rxjava3.core.Completable

class RemoveInputUseCase(
    private val txRepository: BtcTransactionRepository
) {

    operator fun invoke(input: InputViewModel): Completable = txRepository.removeInput(input.script)
}