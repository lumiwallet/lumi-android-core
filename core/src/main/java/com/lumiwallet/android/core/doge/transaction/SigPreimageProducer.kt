package com.lumiwallet.android.core.doge.transaction

import com.lumiwallet.android.core.utils.btc_based.ByteBuffer
import com.lumiwallet.android.core.utils.btc_based.types.UInt
import com.lumiwallet.android.core.utils.btc_based.types.VarInt
import com.lumiwallet.android.core.utils.safeToByteArray

object SigPreimageProducer {

    internal fun producePreimage(
        inputs: List<Input>,
        outputs: List<Output>,
        singingInputIndex: Int
    ): ByteArray {

        val result = ByteBuffer()

        result.append(*VarInt.of(inputs.size.toLong()).asLitEndBytes())
        for (i in inputs.indices) {
            val input = inputs[i]
            result.append(*input.transactionHashBytesLitEnd)
            result.append(*UInt.of(input.index).asLitEndBytes())

            if (i == singingInputIndex) {
                val lockBytes = input.lock.safeToByteArray()
                result.append(*VarInt.of(lockBytes.size.toLong()).asLitEndBytes())
                result.append(*lockBytes)
            } else {
                result.append(*VarInt.of(0).asLitEndBytes())
            }

            result.append(*input.sequence.asLitEndBytes())
        }

        result.append(*VarInt.of(outputs.size.toLong()).asLitEndBytes())
        for (output in outputs) {
            val bytes = output.serializeForSigHash()
            result.append(*bytes)
        }

        return result.bytes()

    }
}
