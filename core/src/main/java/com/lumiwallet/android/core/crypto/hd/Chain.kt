package com.lumiwallet.android.core.crypto.hd

import com.lumiwallet.android.core.bitcoin.core.Address
import com.lumiwallet.android.core.bitcoin.core.NetworkParameters
import com.lumiwallet.android.core.bitcoin.script.ScriptType
import com.lumiwallet.android.core.crypto.ECKey

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

    fun getAddressAt(addrIdx: Int): Address {
        val dk = HDKeyDerivation.deriveChildKey(cKey, ChildNumber(addrIdx, false))
        return Address.fromKey(params, dk, ScriptType.P2PKH)

    }

    fun getECKeyAt(index: Int): ECKey {
         return HDKeyDerivation.deriveChildKey(cKey, ChildNumber(index, false))
    }

}
