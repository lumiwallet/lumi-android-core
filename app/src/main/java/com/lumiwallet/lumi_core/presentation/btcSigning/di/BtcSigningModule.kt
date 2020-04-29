package com.lumiwallet.lumi_core.presentation.btcSigning.di

import com.lumiwallet.lumi_core.domain.btcSigning.*
import com.lumiwallet.lumi_core.domain.repository.BtcTransactionRepository
import com.lumiwallet.lumi_core.domain.repository.ResourcesProvider
import dagger.Module
import dagger.Provides

@Module
class BtcSigningModule {

    @Provides
    @BtcSigningScope
    fun provideTransactionRepository(): BtcTransactionRepository =
        com.lumiwallet.lumi_core.model.BtcTransactionRepository()

    @Provides
    @BtcSigningScope
    fun provideAddInputUseCase(
        txRepository: BtcTransactionRepository
    ): AddInputUseCase = AddInputUseCase(txRepository)

    @Provides
    @BtcSigningScope
    fun provideRemoveInputUseCase(
        txRepository: BtcTransactionRepository
    ): RemoveInputUseCase = RemoveInputUseCase(txRepository)

    @Provides
    @BtcSigningScope
    fun provideAddOutputUseCase(
        txRepository: BtcTransactionRepository
    ): AddOutputUseCase = AddOutputUseCase(txRepository)

    @Provides
    @BtcSigningScope
    fun provideRemoveOutputUseCase(
        txRepository: BtcTransactionRepository
    ): RemoveOutputUseCase = RemoveOutputUseCase(txRepository)

    @Provides
    @BtcSigningScope
    fun provideSetFeePerByteUseCase(
        txRepository: BtcTransactionRepository
    ): SetFeePerByteUseCase = SetFeePerByteUseCase(txRepository)

    @Provides
    @BtcSigningScope
    fun provideCalculateFeeUseCase(
        txRepository: BtcTransactionRepository
    ): CalculateFeeUseCase = CalculateFeeUseCase(txRepository)

    @Provides
    @BtcSigningScope
    fun provideSignBtcTransactionUseCase(
        txRepository: BtcTransactionRepository
    ): SignBtcTransactionUseCase = SignBtcTransactionUseCase(txRepository)

    @Provides
    @BtcSigningScope
    fun provideGetTransactionUseCase(
        txRepository: BtcTransactionRepository,
        resourcesProvider: ResourcesProvider
    ): GetTransactionUseCase = GetTransactionUseCase(txRepository, resourcesProvider)

    @Provides
    @BtcSigningScope
    fun provideGetInputUseCase(
        txRepository: BtcTransactionRepository
    ): GetInputUseCase = GetInputUseCase(txRepository)

    @Provides
    @BtcSigningScope
    fun provideUpdateInputUseCase(
        txRepository: BtcTransactionRepository
    ): UpdateInputUseCase = UpdateInputUseCase(txRepository)

    @Provides
    @BtcSigningScope
    fun provideUpdateOutputUseCase(
        txRepository: BtcTransactionRepository
    ): UpdateOutputUseCase = UpdateOutputUseCase(txRepository)

}