package com.lumiwallet.android.core.bitcoin.transaction

import com.lumiwallet.android.core.bitcoin.constant.SigHashType
import com.lumiwallet.android.core.bitcoin.core.PrivateKey
import com.lumiwallet.android.core.bitcoin.types.OpSize
import com.lumiwallet.android.core.bitcoin.util.ByteBuffer

object WitnessProducer {

    internal fun produceWitness(
        sigHash: ByteArray,
        key: PrivateKey
    ): ByteArray = ByteBuffer().apply {
        val sign = ByteBuffer(
            *key.sign(sigHash),
            SigHashType.ALL.asByte()
        )

        val pubKeyEncoded = key.key.pubKeyPoint.getEncoded(true)

        append(0x02.toByte())

        append(OpSize.ofInt(sign.size))
        append(*sign.bytes())

        append(OpSize.ofInt(pubKeyEncoded.size))
        append(*pubKeyEncoded)
    }
        .bytes
}
