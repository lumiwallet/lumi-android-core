package com.lumiwallet.lumi_core.presentation.bchSigning.di

import com.lumiwallet.lumi_core.domain.bchSigning.*
import com.lumiwallet.lumi_core.domain.repository.BtcTransactionRepository
import com.lumiwallet.lumi_core.domain.repository.ResourcesProvider
import dagger.Module
import dagger.Provides

@Module
class BchSigningModule {

    @Provides
    @BchSigningScope
    fun provideTransactionRepository(): BtcTransactionRepository =
        com.lumiwallet.lumi_core.model.BchTransactionRepository()

    @Provides
    @BchSigningScope
    fun provideAddInputUseCase(
        txRepository: BtcTransactionRepository
    ): AddInputUseCase = AddInputUseCase(txRepository)

    @Provides
    @BchSigningScope
    fun provideRemoveInputUseCase(
        txRepository: BtcTransactionRepository
    ): RemoveInputUseCase = RemoveInputUseCase(txRepository)

    @Provides
    @BchSigningScope
    fun provideAddOutputUseCase(
        txRepository: BtcTransactionRepository
    ): AddOutputUseCase = AddOutputUseCase(txRepository)

    @Provides
    @BchSigningScope
    fun provideRemoveOutputUseCase(
        txRepository: BtcTransactionRepository
    ): RemoveOutputUseCase = RemoveOutputUseCase(txRepository)

    @Provides
    @BchSigningScope
    fun provideSetFeePerByteUseCase(
        txRepository: BtcTransactionRepository
    ): SetFeePerByteUseCase = SetFeePerByteUseCase(txRepository)

    @Provides
    @BchSigningScope
    fun provideCalculateFeeUseCase(
        txRepository: BtcTransactionRepository
    ): CalculateFeeUseCase = CalculateFeeUseCase(txRepository)

    @Provides
    @BchSigningScope
    fun provideSignBtcTransactionUseCase(
        txRepository: BtcTransactionRepository
    ): SignBtcTransactionUseCase = SignBtcTransactionUseCase(txRepository)

    @Provides
    @BchSigningScope
    fun provideGetTransactionUseCase(
        txRepository: BtcTransactionRepository,
        resourcesProvider: ResourcesProvider
    ): GetTransactionUseCase = GetTransactionUseCase(txRepository, resourcesProvider)

    @Provides
    @BchSigningScope
    fun provideGetInputUseCase(
        txRepository: BtcTransactionRepository
    ): GetInputUseCase = GetInputUseCase(txRepository)

    @Provides
    @BchSigningScope
    fun provideUpdateInputUseCase(
        txRepository: BtcTransactionRepository
    ): UpdateInputUseCase = UpdateInputUseCase(txRepository)

    @Provides
    @BchSigningScope
    fun provideUpdateOutputUseCase(
        txRepository: BtcTransactionRepository
    ): UpdateOutputUseCase = UpdateOutputUseCase(txRepository)

}