package com.lumiwallet.android.core.cardano.transaction

import com.upokecenter.cbor.CBORObject
import java.io.ByteArrayOutputStream


class CardanoTransaction(
    val inputs: List<Input>,
    val outputs: List<Output>,
    val fee: Long,
    val ttl: Long
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

    // first - public key, second - signature
    val signatures: MutableList<Pair<ByteArray, ByteArray>> = mutableListOf()

    fun addSignature(signature: Pair<ByteArray, ByteArray>) {
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
            if (it.tokens.isEmpty()) {
                cborOutputs.Add(CBORObject.NewArray().Add(it.payload).Add(it.amount))
            } else {
                val output = CBORObject.NewArray()
                output.Add(it.payload)
                val amounts = CBORObject.NewArray()
                amounts.Add(it.amount)
                for (token in it.tokens) {
                    amounts.Add(
                        CBORObject.NewMap()
                            .Add(
                                token.polocyId,
                                CBORObject.NewMap()
                                    .Add(token.tokenName.toByteArray(), token.tokenAmount)
                            )
                    )
                }
                output.Add(amounts)
                cborOutputs.Add(output)
            }
        }

        txMap.Add(0, cborInputs)
            .Add(1, cborOutputs)
            .Add(2, fee)
            .Add(3, ttl)
            .WriteTo(stream)

        return stream.toByteArray()
    }

    fun build(): ByteArray {
        val stream = ByteArrayOutputStream()

        val tx = CBORObject.NewArray()
        val txMap = CBORObject.NewMap()
        val cborInputs = CBORObject.NewArray()

        inputs.forEach {
            cborInputs.Add(CBORObject.NewArray().Add(it.hash).Add(it.outputIndex))
        }

        val cborOutputs = CBORObject.NewArray()

        outputs.forEach {
            if (it.tokens.isEmpty()) {
                cborOutputs.Add(CBORObject.NewArray().Add(it.payload).Add(it.amount))
            } else {
                val output = CBORObject.NewArray()
                    output.Add(it.payload)
                val amounts = CBORObject.NewArray()
                amounts.Add(it.amount)
                for (token in it.tokens) {
                    amounts.Add(
                        CBORObject.NewMap()
                            .Add(
                                token.polocyId,
                                CBORObject.NewMap()
                                    .Add(token.tokenName.toByteArray(), token.tokenAmount)
                            )
                    )
                }
                output.Add(amounts)
                cborOutputs.Add(output)
            }

        }

        txMap.Add(0, cborInputs)
            .Add(1, cborOutputs)
            .Add(2, fee)
            .Add(3, ttl)

        val signatureMap = CBORObject.NewMap()

        val cborSignatures = CBORObject.NewArray()

        signatures.forEach {
            cborSignatures.Add(it)
        }

        signatures.forEachIndexed { index, signature ->
            signatureMap.Add(
                index,
                CBORObject
                    .NewArray()
                    .Add(signature.first)
                    .Add(signature.second)
            )
        }


        tx.Add(txMap)
            .Add(signatureMap)
            .Add(CBORObject.Null)
            .WriteTo(stream)

        return stream.toByteArray()
    }

}