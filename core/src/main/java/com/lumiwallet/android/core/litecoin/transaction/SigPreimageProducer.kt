package com.lumiwallet.android.core.litecoin.transaction

import com.lumiwallet.android.core.litecoin.constant.OpCodes
import com.lumiwallet.android.core.utils.Sha256Hash
import com.lumiwallet.android.core.utils.btc_based.ByteBuffer
import com.lumiwallet.android.core.utils.btc_based.types.OpSize
import com.lumiwallet.android.core.utils.btc_based.types.UInt
import com.lumiwallet.android.core.utils.btc_based.types.ULong
import com.lumiwallet.android.core.utils.btc_based.types.VarInt
import com.lumiwallet.android.core.utils.safeToByteArray

object SigPreimageProducer {

    internal fun producePreimage(
        segwit: Boolean,
        inputs: List<Input>,
        outputs: List<Output>,
        singingInputIndex: Int
    ): ByteArray {
        if (segwit) {
            val preimage = ByteBuffer()
            val currentInput = inputs[singingInputIndex]

            //2. hashPrevOuts
            val prevOuts = ByteBuffer()
            inputs.forEach { input ->
                prevOuts.append(*input.transactionHashBytesLitEnd)
                prevOuts.append(*UInt.of(input.index).asLitEndBytes())
            }
            preimage.append(*Sha256Hash.hashTwice(prevOuts.bytes))

            //3. hashSequences
            val sequences = ByteBuffer()
            for (input in inputs) {
                sequences.append(*input.sequence.asLitEndBytes())
            }
            preimage.append(*Sha256Hash.hashTwice(sequences.bytes))

            //4. PreviousTransactionHash
            preimage.append(*currentInput.transactionHashBytesLitEnd)

            //5.InputIndex
            preimage.append(*UInt.of(currentInput.index).asLitEndBytes())

            val scriptCode = ByteBuffer(
                OpCodes.DUP,
                OpCodes.HASH160,
                OpSize.ofInt(20),
                *currentInput.privateKey.publicKeyHash,
                OpCodes.EQUALVERIFY,
                OpCodes.CHECKSIG
            )

            //6.ScriptDataCount
            preimage.append(OpSize.ofInt(scriptCode.size))

            //7.ScriptData
            preimage.append(*scriptCode.bytes)

            //8.Amount
            preimage.append(*ULong.of(currentInput.satoshi).asLitEndBytes())

            //9.Sequence
            preimage.append(*currentInput.sequence.asLitEndBytes())

            //10.OutputsHash
            val outs = ByteBuffer()
            outputs.forEach { output ->
                outs.append(*output.serializeForSigHash())
            }

            preimage.append(*Sha256Hash.hashTwice(outs.bytes))

            return preimage.bytes
        } else {
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
}
