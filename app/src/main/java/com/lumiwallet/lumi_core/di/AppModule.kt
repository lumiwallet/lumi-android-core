package com.lumiwallet.lumi_core.di

import android.content.Context
import com.lumiwallet.lumi_core.domain.repository.PermissionHelper
import com.lumiwallet.lumi_core.domain.repository.ResourcesProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideResourceProvider(
        context: Context
    ): ResourcesProvider = com.lumiwallet.lumi_core.model.ResourcesProvider(context)

    @Provides
    @Singleton
    fun providePermissionHelper(
        context: Context
    ): PermissionHelper = com.lumiwallet.lumi_core.model.PermissionHelper(context)
}