package com.lumiwallet.android.core.bitcoin.transaction

import com.lumiwallet.android.core.bitcoin.constant.ErrorMessages
import com.lumiwallet.android.core.bitcoin.constant.OpCodes
import com.lumiwallet.android.core.bitcoin.types.OpSize
import com.lumiwallet.android.core.bitcoin.util.ByteBuffer

object ScriptPubKeyProducer {

    fun produceScript(
            prefix: Byte,
            hash: ByteArray
    ): ByteArray {
        if (prefix == 0x00.toByte()) {
            //P2PKH
            val lockingScript = ByteBuffer()
            lockingScript.append(OpCodes.DUP, OpCodes.HASH160)
            lockingScript.append(OpSize.ofInt(hash.size).size)
            lockingScript.append(*hash)
            lockingScript.append(OpCodes.EQUALVERIFY, OpCodes.CHECKSIG)
            return lockingScript.bytes()

        } else if (prefix == 0x05.toByte()) {
            //P2SH
            val lockingScript = ByteBuffer()
            lockingScript.append(OpCodes.HASH160)
            lockingScript.append(OpSize.ofInt(hash.size).size)
            lockingScript.append(*hash)
            lockingScript.append(OpCodes.EQUAL)
            return lockingScript.bytes()

        }

        throw IllegalArgumentException(String.format(ErrorMessages.SPK_UNSUPPORTED_PRODUCER, prefix))
    }


}


