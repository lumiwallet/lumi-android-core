package com.lumiwallet.lumi_core.domain.dogeSigning

import com.lumiwallet.lumi_core.domain.repository.DogeTransactionRepository

class RemoveOutputUseCase(
    private val txRepository: DogeTransactionRepository
) {

    operator fun invoke(address: String) = txRepository.removeOutput(address)
}