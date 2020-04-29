package com.lumiwallet.lumi_core.domain

import com.lumiwallet.lumi_core.domain.repository.ResourcesProvider
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetStringUseCase @Inject constructor(
    private val resourcesProvider: ResourcesProvider
){

    operator fun invoke(resId: Int): Single<String> = resourcesProvider.getString(resId)
}