package com.lumiwallet.lumi_core.domain.derivation

import com.lumiwallet.android.core.bitcoin.core.LegacyAddress
import com.lumiwallet.android.core.bitcoin.params.MainNetParams
import com.lumiwallet.android.core.crypto.hd.DeterministicKey
import com.lumiwallet.android.core.crypto.hd.HDKeyDerivation
import com.lumiwallet.android.core.doge.params.DogeNetParams
import com.lumiwallet.android.core.eos.ec.EosPrivateKey
import com.lumiwallet.lumi_core.domain.entity.DerivedKeyViewModel
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetKeysInRangeUseCase @Inject constructor() {

    operator fun invoke(
        key: DeterministicKey?,
        start: String,
        end: String
    ): Single<MutableList<DerivedKeyViewModel>> = Single.create { emitter ->

        if (key == null) throw NullPointerException("generate master key first!")

        val startInt: Int = start.toIntOrNull() ?: throw IllegalAccessException("cant parse start key index")
        val endInt: Int = end.toIntOrNull() ?: throw IllegalAccessException("cant parse end key index")

        emitter.onSuccess((startInt..endInt)
            .map { childIndex ->
                val childKey = HDKeyDerivation.deriveChildKey(key, childIndex)
                DerivedKeyViewModel(
                    childIndex,
                    LegacyAddress.fromKey(MainNetParams, childKey).toString(),
                    EosPrivateKey(key).publicKey.toString(),
                    com.lumiwallet.android.core.doge.core.LegacyAddress.fromKey(DogeNetParams, key).toString(),
                    childKey.publicKeyAsHex,
                    childKey.privateKeyAsHex
                )
            } as MutableList<DerivedKeyViewModel>)
    }
}