package com.lumiwallet.android.core.bitcoinCash.transaction

import com.lumiwallet.android.core.bitcoinCash.constant.ErrorMessages
import com.lumiwallet.android.core.bitcoinCash.constant.OpCodes
import com.lumiwallet.android.core.bitcoinCash.types.OpSize
import com.lumiwallet.android.core.bitcoinCash.util.ByteBuffer

object ScriptPubKeyProducer {

    fun produceScript(
            prefix: Byte,
            hash: ByteArray
    ): ByteArray = when (prefix) {
            0x00.toByte() -> { //P2PKH
                ByteBuffer(
                    OpCodes.DUP,
                    OpCodes.HASH160,
                    OpSize.ofInt(hash.size),
                    *hash,
                    OpCodes.EQUALVERIFY,
                    OpCodes.CHECKSIG
                ).bytes
            }
            0X05.toByte() -> { //P2SH
                ByteBuffer(
                    OpCodes.HASH160,
                    OpSize.ofInt(hash.size),
                    *hash,
                    OpCodes.EQUAL
                ).bytes
            }
        else -> throw IllegalArgumentException(String.format(ErrorMessages.SPK_UNSUPPORTED_PRODUCER, prefix))
    }

}


