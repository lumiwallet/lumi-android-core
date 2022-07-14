package com.lumiwallet.android.core.litecoin.transaction

import com.lumiwallet.android.core.litecoin.constant.SigHashType
import com.lumiwallet.android.core.utils.btc_based.ByteBuffer
import com.lumiwallet.android.core.utils.btc_based.core.PrivateKey
import com.lumiwallet.android.core.utils.btc_based.types.OpSize

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
