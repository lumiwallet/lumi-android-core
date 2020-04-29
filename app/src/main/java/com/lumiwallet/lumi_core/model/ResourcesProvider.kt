package com.lumiwallet.lumi_core.model

import android.content.Context
import com.lumiwallet.lumi_core.domain.repository.ResourcesProvider
import io.reactivex.rxjava3.core.Single

class ResourcesProvider(
    private val context: Context
) : ResourcesProvider {

    override fun getString(id: Int): Single<String> = Single.just(context.getString(id))
}