package com.lumiwallet.lumi_core.domain.derivation

import com.lumiwallet.android.core.crypto.hd.MnemonicCode
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject

class CheckMnemonicUseCase @Inject constructor() {

    operator fun invoke(mnemonic: String): Single<MutableList<String>> = Single.create {
        val wordArray = mnemonic
            .trim { char -> char <= ' ' }
            .replace("[ \n]+".toRegex(), " ")
            .toLowerCase(Locale.getDefault())
            .split(' ')
            .dropLastWhile { word -> word.isEmpty() }
            .toList()
        MnemonicCode.INSTANCE.check(wordArray, false)
        it.onSuccess(wordArray as MutableList<String>)
    }
}