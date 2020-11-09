package com.lumiwallet.android.core.doge.transaction

import com.lumiwallet.android.core.doge.constant.OpCodes
import com.lumiwallet.android.core.doge.script.ScriptType
import com.lumiwallet.android.core.utils.btc_based.ByteBuffer
import com.lumiwallet.android.core.utils.btc_based.types.OpSize

object ScriptPubKeyProducer {

    fun produceScript(
        hash: ByteArray,
        type: ScriptType
    ): ByteArray = when (type) {
        ScriptType.P2PKH -> {
            ByteBuffer(
                OpCodes.DUP,
                OpCodes.HASH160,
                OpSize.ofInt(hash.size),
                *hash,
                OpCodes.EQUALVERIFY,
                OpCodes.CHECKSIG
            )
                .bytes
        }
    }
}


