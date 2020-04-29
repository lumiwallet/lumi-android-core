package com.lumiwallet.lumi_core.domain.derivation

import com.lumiwallet.android.core.crypto.hd.DeterministicSeed
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetDeterministicSeedFromMnemonicUseCase @Inject constructor() {

    operator fun invoke(mnemonic: MutableList<String>): Single<DeterministicSeed> =
        Single.just(DeterministicSeed(mnemonic))
            .doOnError {
                throw Exception("Wrong mnemonic")
            }

}