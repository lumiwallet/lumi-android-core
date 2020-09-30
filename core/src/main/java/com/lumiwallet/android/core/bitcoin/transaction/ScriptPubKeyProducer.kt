package com.lumiwallet.android.core.bitcoin.transaction

import com.lumiwallet.android.core.bitcoin.constant.ErrorMessages
import com.lumiwallet.android.core.bitcoin.constant.OpCodes
import com.lumiwallet.android.core.bitcoin.script.ScriptType
import com.lumiwallet.android.core.bitcoin.types.OpSize
import com.lumiwallet.android.core.bitcoin.util.ByteBuffer

object ScriptPubKeyProducer {

    fun produceScript(
        hash: ByteArray,
        type: ScriptType
    ): ByteArray = when (type) {
        ScriptType.P2SH -> {
            ByteBuffer(
                OpCodes.HASH160,
                OpSize.ofInt(hash.size),
                *hash,
                OpCodes.EQUAL
            )
                .bytes
        }
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
        ScriptType.P2WPKH -> {
            ByteBuffer(
                OpCodes.FALSE,
                OpSize.ofInt(hash.size),
                *hash
            )
                .bytes
        }
        else -> throw IllegalArgumentException(
            String.format(ErrorMessages.SPK_UNSUPPORTED_PRODUCER, type.toString())
        )
    }
}


