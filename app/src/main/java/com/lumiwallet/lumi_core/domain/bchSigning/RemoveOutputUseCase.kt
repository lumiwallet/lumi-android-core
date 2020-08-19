package com.lumiwallet.lumi_core.domain.bchSigning

import com.lumiwallet.lumi_core.domain.repository.BtcTransactionRepository

class RemoveOutputUseCase(
    private val txRepository: BtcTransactionRepository
) {

    operator fun invoke(address: String) = txRepository.removeOutput(address)
}