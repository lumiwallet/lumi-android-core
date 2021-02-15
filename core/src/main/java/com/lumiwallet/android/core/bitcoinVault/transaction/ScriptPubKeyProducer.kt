package com.lumiwallet.android.core.bitcoinVault.transaction

import com.lumiwallet.android.core.bitcoinVault.constant.OpCodes
import com.lumiwallet.android.core.bitcoinVault.script.ScriptType
import com.lumiwallet.android.core.utils.btc_based.ByteBuffer
import com.lumiwallet.android.core.utils.btc_based.ErrorMessages
import com.lumiwallet.android.core.utils.btc_based.types.OpSize

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
