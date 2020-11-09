package com.lumiwallet.android.core.crypto.hd

import com.lumiwallet.android.core.crypto.ECKey
import com.lumiwallet.android.core.doge.script.ScriptType
import com.lumiwallet.android.core.utils.btc_based.core.Address
import com.lumiwallet.android.core.utils.btc_based.core.NetworkParameters

class Chain(
    val params: NetworkParameters,
    aKey: DeterministicKey,
    isReceive: Boolean
) {

    private var cKey: DeterministicKey

    init {
        val chain = if (isReceive) 0 else 1
        cKey = HDKeyDerivation.deriveChildKey(aKey, chain)
    }

    @Deprecated("deprecated")
    fun getAddressAt(addrIdx: Int): Address {
        val dk = HDKeyDerivation.deriveChildKey(cKey, ChildNumber(addrIdx, false))
        return Address.fromKey(params, dk, ScriptType.P2PKH)

    }

    fun getECKeyAt(index: Int): ECKey =
        HDKeyDerivation.deriveChildKey(cKey, ChildNumber(index, false))

}
