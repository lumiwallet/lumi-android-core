package com.lumiwallet.lumi_core.domain.derivation

import com.lumiwallet.android.core.crypto.hd.DeterministicSeed
import com.lumiwallet.android.core.crypto.hd.MnemonicCode
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GenerateMnemonicUseCase @Inject constructor(){

    operator fun invoke(size: Int): Single<MutableList<String>> = Single.create { emitter ->
        val mnemonicBitsSize = MnemonicCode.WORDLIST_BITS_SIZE * size
        val checksumLength = mnemonicBitsSize / 33
        val entropyLength = mnemonicBitsSize - checksumLength
        val deterministicSeed = DeterministicSeed(bits = entropyLength)
        emitter.onSuccess(deterministicSeed.mnemonicCode as MutableList<String>)
    }

}