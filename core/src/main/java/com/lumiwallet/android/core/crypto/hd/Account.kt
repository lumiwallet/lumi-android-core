package com.lumiwallet.android.core.crypto.hd

import com.lumiwallet.android.core.utils.btc_based.core.NetworkParameters
import java.util.*

class Account internal constructor(params: NetworkParameters, wKey: DeterministicKey, child: Int) {

    private var chains: MutableList<Chain> = mutableListOf()

    init {

        var childnum = child
        childnum = childnum or ChildNumber.HARDENED_BIT
        val aKey = HDKeyDerivation.deriveChildKey(wKey, childnum)

        chains = ArrayList()
        chains.add(Chain(params, aKey, true))
        chains.add(Chain(params, aKey, false))

    }

    fun getChain(idx: Int): Chain = chains[idx]

}