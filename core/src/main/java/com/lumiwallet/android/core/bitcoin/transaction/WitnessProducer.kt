package com.lumiwallet.android.core.bitcoin.transaction

import com.lumiwallet.android.core.bitcoin.constant.SigHashType
import com.lumiwallet.android.core.bitcoin.core.PrivateKey
import com.lumiwallet.android.core.bitcoin.types.OpSize
import com.lumiwallet.android.core.bitcoin.util.ByteBuffer

object WitnessProducer {

    internal fun produceWitness(segwit: Boolean, sigHash: ByteArray, key: PrivateKey): ByteArray {
        if (segwit) {
            val result = ByteBuffer()

            result.append(0x02.toByte())

            val sign = ByteBuffer(*key.sign(sigHash))
            sign.append(SigHashType.ALL.asByte())
            result.append(OpSize.ofInt(sign.size()).size)
            result.append(*sign.bytes())

            val pubKey = key.publicKey
            result.append(OpSize.ofInt(pubKey.size).size)
            result.append(*pubKey)

            return result.bytes()
        } else {
            return byteArrayOf(0x00.toByte())
        }
    }

}
