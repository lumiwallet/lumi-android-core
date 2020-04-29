package com.lumiwallet.lumi_core.domain.derivation

import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetAvailableMnemonicSizesUseCase @Inject constructor(){

    private var words = arrayOf("12", "15", "18", "21", "24")

    operator fun invoke(): Single<Array<String>> = Single.just(words)

}