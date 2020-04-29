package com.lumiwallet.lumi_core.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class AppContextModule(private val application: Application) {

    @Provides
    fun provideApplication(): Application = application

    @Provides
    fun provideContext(): Context = application.applicationContext
}
