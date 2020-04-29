package com.lumiwallet.lumi_core.domain.repository

import io.reactivex.rxjava3.core.Single

interface ResourcesProvider {

    fun getString(id: Int): Single<String>

}