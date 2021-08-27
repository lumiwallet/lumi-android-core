package com.lumiwallet.android.core.cardano.transaction

import com.upokecenter.cbor.CBORObject
import java.io.ByteArrayOutputStream


class CardanoTransaction(
    val inputs: List<Input>,
    val outputs: List<Output>,
    val fee: Long,
    val ttl: Long
) {

    val signatures: MutableList<ByteArray> = mutableListOf()

    class Input (
        var hash: ByteArray,
        var outputIndex: Long,
        var amount: Long,
        var address: String
    )

    class Output (
        var payload: ByteArray,
        var amount: Long
    )

    fun addSignature(signature: ByteArray) {
        signatures.add(signature)
    }

    fun raw(): ByteArray {
        val stream = ByteArrayOutputStream()
        val txMap = CBORObject.NewMap()
        val cborInputs = CBORObject.NewArray()

        inputs.forEach {
            cborInputs.Add(CBORObject.NewArray().Add(it.hash).Add(it.outputIndex))
        }

        val cborOutputs = CBORObject.NewArray()

        outputs.forEach {
            cborOutputs.Add(CBORObject.NewArray().Add(it.payload).Add(it.amount))
        }

        txMap.Add(0, inputs)
            .Add(1, outputs)
            .Add(2, fee)
            .Add(3, ttl)
            .WriteTo(stream)

        return stream.toByteArray()
    }

    fun build(): ByteArray {
        val nul: ByteArray? = null

        val stream = ByteArrayOutputStream()

        val tx = CBORObject.NewArray()
        val txMap = CBORObject.NewMap()
        val cborInputs = CBORObject.NewArray()

        inputs.forEach {
            cborInputs.Add(CBORObject.NewArray().Add(it.hash).Add(it.outputIndex))
        }

        val cborOutputs = CBORObject.NewArray()

        outputs.forEach {
            cborOutputs.Add(CBORObject.NewArray().Add(it.payload).Add(it.amount))
        }

        txMap.Add(0, inputs)
            .Add(1, outputs)
            .Add(2, fee)
            .Add(3, ttl)

        val signatureMap = CBORObject.NewMap()

        val cborSignatures = CBORObject.NewArray()

        signatures.forEach {
            cborSignatures.Add(it)
        }

        signatureMap.Add(
            0,
            CBORObject
                .NewArray()
                .Add(signatures)
        )

        tx.Add(txMap)
            .Add(signatureMap)
            .Add(nul)
            .WriteTo(stream)

        return stream.toByteArray()
    }

}