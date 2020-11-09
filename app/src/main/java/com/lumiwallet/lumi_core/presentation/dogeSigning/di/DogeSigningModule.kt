package com.lumiwallet.lumi_core.presentation.dogeSigning.di

import com.lumiwallet.lumi_core.domain.dogeSigning.*
import com.lumiwallet.lumi_core.domain.repository.DogeTransactionRepository
import com.lumiwallet.lumi_core.domain.repository.ResourcesProvider
import dagger.Module
import dagger.Provides

@Module
class DogeSigningModule {

    @Provides
    @DogeSigningScope
    fun provideTransactionRepository(): DogeTransactionRepository =
        com.lumiwallet.lumi_core.model.DogeTransactionRepository()

    @Provides
    @DogeSigningScope
    fun provideAddInputUseCase(
        txRepository: DogeTransactionRepository
    ): AddInputUseCase = AddInputUseCase(txRepository)

    @Provides
    @DogeSigningScope
    fun provideRemoveInputUseCase(
        txRepository: DogeTransactionRepository
    ): RemoveInputUseCase = RemoveInputUseCase(txRepository)

    @Provides
    @DogeSigningScope
    fun provideAddOutputUseCase(
        txRepository: DogeTransactionRepository
    ): AddOutputUseCase = AddOutputUseCase(txRepository)

    @Provides
    @DogeSigningScope
    fun provideRemoveOutputUseCase(
        txRepository: DogeTransactionRepository
    ): RemoveOutputUseCase = RemoveOutputUseCase(txRepository)

    @Provides
    @DogeSigningScope
    fun provideSetFeePerByteUseCase(
        txRepository: DogeTransactionRepository
    ): SetFeePerByteUseCase = SetFeePerByteUseCase(txRepository)

    @Provides
    @DogeSigningScope
    fun provideCalculateFeeUseCase(
        txRepository: DogeTransactionRepository
    ): CalculateFeeUseCase = CalculateFeeUseCase(txRepository)

    @Provides
    @DogeSigningScope
    fun provideSignBtcTransactionUseCase(
        txRepository: DogeTransactionRepository
    ): SignDogeTransactionUseCase = SignDogeTransactionUseCase(txRepository)

    @Provides
    @DogeSigningScope
    fun provideGetTransactionUseCase(
        txRepository: DogeTransactionRepository,
        resourcesProvider: ResourcesProvider
    ): GetTransactionUseCase = GetTransactionUseCase(txRepository, resourcesProvider)

    @Provides
    @DogeSigningScope
    fun provideGetInputUseCase(
        txRepository: DogeTransactionRepository
    ): GetInputUseCase = GetInputUseCase(txRepository)

    @Provides
    @DogeSigningScope
    fun provideUpdateInputUseCase(
        txRepository: DogeTransactionRepository
    ): UpdateInputUseCase = UpdateInputUseCase(txRepository)

    @Provides
    @DogeSigningScope
    fun provideUpdateOutputUseCase(
        txRepository: DogeTransactionRepository
    ): UpdateOutputUseCase = UpdateOutputUseCase(txRepository)

}