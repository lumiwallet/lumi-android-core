package com.lumiwallet.android.core.bitcoinCash.transaction

import com.lumiwallet.android.core.bitcoinCash.constant.OpCodes
import com.lumiwallet.android.core.bitcoinCash.types.OpSize
import com.lumiwallet.android.core.bitcoinCash.types.UInt
import com.lumiwallet.android.core.bitcoinCash.types.ULong
import com.lumiwallet.android.core.bitcoinCash.util.ByteBuffer
import com.lumiwallet.android.core.crypto.Ripemd160
import com.lumiwallet.android.core.utils.Sha256Hash

object SigPreimageProducer {

    internal fun producePreimage(
        inputs: List<Input>,
        outputs: List<Output>,
        currentInput: Input
    ): ByteArray {

        val preimage = ByteBuffer()

        //2. hashPrevOuts
        val prevOuts = ByteBuffer()
        inputs.forEach { input ->
            prevOuts.append(*input.transactionHash)
            prevOuts.append(*UInt.of(input.index).litEndBytes)
        }
        preimage.append(*Sha256Hash.hashTwice(prevOuts.bytes))

        //3. hashSequences
        val sequences = ByteBuffer()
        for (input in inputs) {
            sequences.append(*input.sequence.asLitEndBytes())
        }
        preimage.append(*Sha256Hash.hashTwice(sequences.bytes))

        //4. PreviousTransactionHash
        preimage.append(*currentInput.transactionHash)

        //5.InputIndex
        preimage.append(*UInt.of(currentInput.index).asLitEndBytes())

        val scriptCode = ByteBuffer(
            OpCodes.DUP,
            OpCodes.HASH160,
            OpSize.ofInt(20),
            *Ripemd160.from(Sha256Hash.hash(currentInput.privateKey.publicKey)),
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

        return preimage.bytes()

    }
}
