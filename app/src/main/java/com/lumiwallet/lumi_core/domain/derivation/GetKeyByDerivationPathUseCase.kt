package com.lumiwallet.lumi_core.domain.derivation

import com.lumiwallet.android.core.crypto.hd.DeterministicKey
import com.lumiwallet.android.core.crypto.hd.DeterministicSeed
import com.lumiwallet.android.core.crypto.hd.HDUtils
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetKeyByDerivationPathUseCase @Inject constructor() {

    operator fun invoke(
        mnemonic: MutableList<String>,
        derivationPath: String
    ): Single<DeterministicKey> = Single.create { emitter ->
        val seed = DeterministicSeed(mnemonic)
        val derivedKey = HDUtils.getKeyFromSeed(derivationPath, seed.seedBytes)
        emitter.onSuccess(derivedKey)
    }
}