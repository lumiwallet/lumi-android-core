package com.lumiwallet.android.core.cardano.transaction

import com.lumiwallet.android.core.utils.blake2b256
import com.upokecenter.cbor.CBORObject
import java.io.ByteArrayOutputStream


class CardanoTransaction(
    val inputs: List<Input>,
    val outputs: List<Output>,
    val ttl: Long,
) {

    class Input (
        var hash: ByteArray,
        var outputIndex: Long,
        var amount: Long,
        var address: String
    )

    class Output (
        var payload: ByteArray,
        var amount: Long,
        val tokens: List<TokenOutput>
    ) {
        data class TokenOutput (
            var tokenName: String,
            val tokenAmount: Long,
            val polocyId: ByteArray
        )
    }

    class Signature(
        val signature: ByteArray,
        val publicKey: ByteArray
    )

    private var baseFee: Long = 0
    private var feePerByte: Long = 0

    val fee: Long
    get() = baseFee + (buildFakeTx().size * feePerByte)

    val hash: ByteArray
    get() {
        val data = CBORObject.NewMap()
        val cborInputs = encodeInputs()
        val cborOutputs = encodeOutputs()
        val stream = ByteArrayOutputStream()

        data.Add(0, cborInputs)
            .Add(1, cborOutputs)
            .Add(2, fee)
            .Add(3, ttl)
            .WriteTo(stream)

        return stream.toByteArray().blake2b256()
    }

    val signatures: MutableList<Signature> = mutableListOf()

    fun addSignature(signature: Signature) {
        signatures.add(signature)
    }

    fun setFeeParams(baseFee: Long, feePerByte: Long) {
        this.baseFee = baseFee
        this.feePerByte = feePerByte
    }

    fun build(): ByteArray {
        val tx = CBORObject.NewArray()
        val data = CBORObject.NewMap()
        val cborInputs = encodeInputs()
        val cborOutputs = encodeOutputs()

        data.Add(0, cborInputs)
            .Add(1, cborOutputs)
            .Add(2, fee)
            .Add(3, ttl)

        val stream = ByteArrayOutputStream()
        val witness = encodeWitness()
        tx.Add(data)
            .Add(witness)
            .Add(CBORObject.Null)
            .WriteTo(stream)

        return stream.toByteArray()
    }

    private fun buildFakeTx(): ByteArray {
        val tx = CBORObject.NewArray()
        val data = CBORObject.NewMap()
        val cborInputs = encodeInputs()
        val cborOutputs = encodeOutputs()

        data.Add(0, cborInputs)
            .Add(1, cborOutputs)
            .Add(2, 100000)
            .Add(3, 35000000)

        val cborWitness = CBORObject.NewMap()
        val cborSignatures = CBORObject.NewArray()
        val fakePublicKey = ByteArray(32).apply { fill(0x00.toByte()) }
        val fakeSignature = ByteArray(64).apply { fill(0x00.toByte()) }

        inputs.forEach{ _ ->
            cborSignatures.Add(
                CBORObject
                    .NewArray()
                    .Add(fakePublicKey)
                    .Add(fakeSignature)
            )
        }
        cborWitness.Add(0, cborSignatures)

        val stream = ByteArrayOutputStream()

        tx.Add(data)
            .Add(cborWitness)
            .Add(CBORObject.Null)
            .WriteTo(stream)
        return stream.toByteArray()
    }

    private fun encodeInputs(): CBORObject = CBORObject.NewArray().apply {
        inputs.forEach {
            Add(CBORObject.NewArray().Add(it.hash).Add(it.outputIndex))
        }
    }

    private fun encodeWitness(): CBORObject = CBORObject.NewMap().apply {
        val cborSignatures = CBORObject.NewArray()
        signatures.forEach{ signature ->
            cborSignatures.Add(
                CBORObject.NewArray()
                    .Add(signature.publicKey)
                    .Add(signature.signature)
            )
        }
        Add(0, cborSignatures)
    }

    private fun encodeOutputs(): CBORObject = CBORObject.NewArray().apply {
        outputs.forEach {
            if (it.tokens.isEmpty()) {
                Add(CBORObject.NewArray().Add(it.payload).Add(it.amount))
            } else {
                val amounts = CBORObject.NewArray().apply {
                    Add(it.amount)
                    for (token in it.tokens) {
                        Add(
                            CBORObject.NewMap().Add(
                                token.polocyId,
                                CBORObject.NewMap()
                                    .Add(token.tokenName.toByteArray(), token.tokenAmount)
                            )
                        )
                    }

                }
                val output = CBORObject.NewArray()
                output.Add(it.payload)
                output.Add(amounts)
                Add(output)
            }
        }
    }

}