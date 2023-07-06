package com.lumiwallet.android.core.cardano.transaction

import com.lumiwallet.android.core.utils.blake2b256
import com.lumiwallet.android.core.utils.sumByLong
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
        var amount: List<Amount>,
        var address: String
    ) {

        class Amount (
            val unit: String,
            val quantity: Long
        )
    }

    class Output (
        var type: Type,
        var payload: ByteArray,
        var amount: Long,
        val tokens: List<TokenOutput>,
        val address: String
    ) {

        enum class Type {
            ORDINARY, CHANGE
        }

        data class TokenOutput (
            val polocyId: ByteArray,
            val tokens: MutableList<Token>
        ) {
            data class Token (
                var tokenName: ByteArray,
                var tokenAmount: Long,
            )
        }
    }

    class Signature(
        val signature: ByteArray,
        val publicKey: ByteArray
    )

    private var baseFee: Long = 0
    private var feePerByte: Long = 0
    private var claimRewardCbor: CBORObject? = null
    private var stakingCbor: CBORObject? = null
    private var fakeSignaturesForStaking = 0

    val fee: Long
    get() {
        val tokensCount = outputs
            .map {
                it.tokens
                    .map { tokenOutput ->
                        tokenOutput.tokens.map { token ->
                            token.tokenName
                        }
                    }
                    .flatten()
            }
            .flatten()
            .toSet()
            .count()
        return baseFee + ((buildFakeTx().size + 32 + (tokensCount * 40 * outputs.size)) * feePerByte)
    }

    val hash: ByteArray
    get() {
        val data = encodeData()
        val stream = ByteArrayOutputStream()
        data.WriteTo(stream)

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

    fun addClaimReward(rewardAmount: Long, stakePublicKeyHash: ByteArray) {
        claimRewardCbor = CBORObject.NewMap()
            .Add(stakePublicKeyHash, rewardAmount)
    }

    fun addRegistration(stakePublicKeyHash: ByteArray) {
        if (stakingCbor == null) {
            stakingCbor = CBORObject.NewArray()
        }
        fakeSignaturesForStaking = +1
        stakingCbor!!
            .Add(
                CBORObject.NewArray()
                    .Add(0)
                    .Add(
                        CBORObject.NewArray()
                            .Add(0)
                            .Add(stakePublicKeyHash)
                    )
            )
    }

    fun addDeregistration(stakePublicKeyHash: ByteArray) {
        if (stakingCbor == null) {
            stakingCbor = CBORObject.NewArray()
        }
        fakeSignaturesForStaking = +1
        stakingCbor!!
            .Add(
                CBORObject.NewArray()
                    .Add(1)
                    .Add(
                        CBORObject.NewArray()
                            .Add(0)
                            .Add(stakePublicKeyHash)
                    )
            )
    }

    fun addDelegation(stakePublicKeyHash: ByteArray, poolKeyHash: ByteArray) {
        if (stakingCbor == null) {
            stakingCbor = CBORObject.NewArray()
        }
        fakeSignaturesForStaking = +1
        stakingCbor!!
            .Add(
                CBORObject.NewArray()
                    .Add(2)
                    .Add(
                        CBORObject.NewArray()
                            .Add(0)
                            .Add(stakePublicKeyHash)
                    )
                    .Add(poolKeyHash)
            )
    }



    fun build(): ByteArray {
        val tx = CBORObject.NewArray()
        val data = encodeData()
        val witness = encodeWitness()
        val stream = ByteArrayOutputStream()
        tx.Add(data)
            .Add(witness)
            .Add(CBORObject.Null)
            .WriteTo(stream)

        return stream.toByteArray()
    }

    private fun buildFakeTx(): ByteArray {
        val tx = CBORObject.NewArray()
        val data = CBORObject.NewMap()

        data.Add(0, encodeInputs())
            .Add(1, encodeOutputs())
            .Add(2, 180000)
            .Add(3, 35000000)

        claimRewardCbor?.let {
            data.Add(5, it)
        }

        stakingCbor?.let {
            data.Add(4, it)
        }

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
        claimRewardCbor?.let {
            cborSignatures.Add(
                CBORObject
                    .NewArray()
                    .Add(fakePublicKey)
                    .Add(fakeSignature)
            )
        }

        stakingCbor?.let {
            for (i in 0..fakeSignaturesForStaking) {
                cborSignatures.Add(
                    CBORObject
                        .NewArray()
                        .Add(fakePublicKey)
                        .Add(fakeSignature)
                )
            }
        }
        cborWitness.Add(0, cborSignatures)

        val stream = ByteArrayOutputStream()

        tx.Add(data)
            .Add(cborWitness)
            .Add(CBORObject.Null)
            .WriteTo(stream)
        return stream.toByteArray()
    }

    private fun encodeData(): CBORObject {
        val fee = fee
        val cborInputs = encodeInputs()
        val cborOutputs = encodeOutputs()
        val data = CBORObject.NewMap()
        data.Add(0, cborInputs)
            .Add(1, cborOutputs)
            .Add(2, fee)
            .Add(3, ttl)

        claimRewardCbor?.let {
            data.Add(5, it)
        }

        stakingCbor?.let {
            data.Add(4, it)
        }

        return data
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
                val cborAmount = CBORObject.NewArray().apply {
                    Add(it.amount)
                    val cborTokensMap = CBORObject.NewMap()
                    for (tokenOutput in it.tokens) {
                        val cborTokens = CBORObject.NewMap()
                        for (tokenName in tokenOutput.tokens) {
                            cborTokens.Add(tokenName.tokenName, tokenName.tokenAmount)
                        }
                        cborTokensMap.Add(tokenOutput.polocyId, cborTokens)
                    }
                    Add(cborTokensMap)
                }
                val cborOutput = CBORObject.NewArray()
                cborOutput.Add(it.payload)
                cborOutput.Add(cborAmount)
                Add(cborOutput)
            }
        }
    }

    /**
     * moves all available funds to the output with [Output.type] == [Output.Type.CHANGE]
     * */
    fun calculateChangeAmount(unit: String) {
        val changeOutputs = outputs.filter { it.type == Output.Type.CHANGE }
        when {
            changeOutputs.size > 1 -> throw IllegalStateException()
            changeOutputs.isEmpty() -> return
            else -> {
                changeOutputs.first().let { changeOutput ->
                    val inputsAmount = inputs.sumByLong { input ->
                        input.amount.find { it.unit == unit }?.quantity ?: 0L
                    }
                    val outputsAmount = outputs
                        .filter { output -> output.type == Output.Type.ORDINARY }
                        .sumByLong { output -> output.amount }
                    val changeAmount = inputsAmount - outputsAmount - fee
                    if (changeAmount < 0)
                        throw IllegalStateException()
                    changeOutput.amount = changeAmount
                }
            }
        }
    }

}
