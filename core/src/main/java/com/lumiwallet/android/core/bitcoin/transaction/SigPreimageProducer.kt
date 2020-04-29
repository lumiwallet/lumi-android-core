package com.lumiwallet.android.core.bitcoin.transaction


import com.lumiwallet.android.core.bitcoin.constant.OpCodes
import com.lumiwallet.android.core.bitcoin.types.OpSize
import com.lumiwallet.android.core.bitcoin.types.UInt
import com.lumiwallet.android.core.bitcoin.types.ULong
import com.lumiwallet.android.core.bitcoin.types.VarInt
import com.lumiwallet.android.core.bitcoin.util.ByteBuffer
import com.lumiwallet.android.core.bitcoin.util.HexUtils
import com.lumiwallet.android.core.crypto.Ripemd160
import com.lumiwallet.android.core.utils.Sha256Hash

object SigPreimageProducer {

    internal fun producePreimage(segwit: Boolean, inputs: List<Input>, outputs: List<Output>, singingInputIndex: Int): ByteArray {
        if (segwit) {
            val result = ByteBuffer()
            val currentInput = inputs[singingInputIndex]

            val prevOuts = ByteBuffer() //hashPrevOuts
            for (input in inputs) {
                prevOuts.append(*input.transactionHashBytesLitEnd)
                prevOuts.append(*UInt.of(input.index).asLitEndBytes())
            }
            result.append(*Sha256Hash.hashTwice(prevOuts.bytes()))

            val sequences = ByteBuffer() //hashSequences
            for (input in inputs) {
                sequences.append(*input.sequence.asLitEndBytes())
            }
            result.append(*Sha256Hash.hashTwice(sequences.bytes()))

            result.append(*currentInput.transactionHashBytesLitEnd) //outpoint
            result.append(*UInt.of(currentInput.index).asLitEndBytes())

            val privateKey = currentInput.privateKey
            val pkh = Ripemd160.from(Sha256Hash.hash(privateKey.publicKey)) //scriptCode
            val scriptCode = ByteBuffer(OpCodes.DUP, OpCodes.HASH160, 0x14.toByte())
            scriptCode.append(*pkh)
            scriptCode.append(OpCodes.EQUALVERIFY, OpCodes.CHECKSIG)
            scriptCode.putFirst(OpSize.ofInt(scriptCode.size()).size)
            result.append(*scriptCode.bytes())

            result.append(*ULong.of(currentInput.satoshi).asLitEndBytes()) //amount in

            result.append(*currentInput.sequence.asLitEndBytes()) //sequence

            val outs = ByteBuffer() //hash outs
            for (output in outputs) {
                val bytes = output.serializeForSigHash()
                outs.append(*bytes)
            }
            result.append(*Sha256Hash.hashTwice(outs.bytes()))

            return result.bytes()
        } else {
            val result = ByteBuffer()

            result.append(*VarInt.of(inputs.size.toLong()).asLitEndBytes())
            for (i in inputs.indices) {
                val input = inputs[i]
                result.append(*input.transactionHashBytesLitEnd)
                result.append(*UInt.of(input.index).asLitEndBytes())

                if (i == singingInputIndex) {
                    val lockBytes = HexUtils.asBytes(input.lock)
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
