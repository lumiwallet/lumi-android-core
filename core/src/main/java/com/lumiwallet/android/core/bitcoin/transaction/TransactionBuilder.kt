package com.lumiwallet.android.core.bitcoin.transaction

import com.lumiwallet.android.core.bitcoin.constant.ErrorMessages
import com.lumiwallet.android.core.bitcoin.constant.SigHashType
import com.lumiwallet.android.core.bitcoin.core.PrivateKey
import com.lumiwallet.android.core.bitcoin.types.UInt
import com.lumiwallet.android.core.bitcoin.types.VarInt
import com.lumiwallet.android.core.bitcoin.util.ByteBuffer
import com.lumiwallet.android.core.utils.Sha256Hash
import org.bouncycastle.util.encoders.Hex
import java.util.*

class TransactionBuilder private constructor() {

    private val inputs = LinkedList<Input>()
    private val outputs = LinkedList<Output>()

    private var changeAddress: String? = null
    private var fee: Long = 0

    private var testTx = false

    private val change: Long
        get() {
            var income = 0L
            for (input in inputs) {
                val satoshi = input.satoshi
                income += satoshi
            }
            var outcome = 0L
            for (output in outputs) {
                val satoshi = output.satoshi
                outcome += satoshi
            }
            val change = income - outcome - fee
            if (testTx && (change < 0 || changeAddress == null)) {
                return 1
            } else {
                check(change >= 0) {
                    "Not enough satoshi. All inputs: " + income +
                            ". All outputs with fee: " + (outcome + fee)
                }

                check(!(change > 0 && changeAddress == null)) { "Transaction contains change ($change satoshi) but no address to send them to" }
            }


            return change
        }

    fun from(
        fromTransactionBigEnd: String,
        fromToutNumber: Int,
        closingScript: String,
        satoshi: Long,
        privateKey: PrivateKey
    ): TransactionBuilder {
        inputs.add(Input(fromTransactionBigEnd, fromToutNumber, closingScript, satoshi, privateKey))
        return this
    }

    fun from(
        unspentOutput: UnspentOutput
    ): TransactionBuilder {
        inputs.add(
            Input(
                unspentOutput.txHash,
                unspentOutput.txOutputN,
                unspentOutput.script,
                unspentOutput.value,
                unspentOutput.privateKey
            )
        )
        return this
    }

    fun rmInputAt(i: Int): TransactionBuilder {
        val index = i - 1
        if (index < 0 || index >= inputs.size) {
            throw RuntimeException("No input found with index $i")
        }
        inputs.removeAt(index)
        return this
    }

    fun rmInput(script: String) {
        inputs.find { it.lock == script }?.let {
            inputs.remove(it)
        }
    }

    fun to(address: String, value: Long): TransactionBuilder {
        outputs.add(Output(value, address, OutputType.CUSTOM))
        return this
    }

    fun rmOutputAt(i: Int): TransactionBuilder {
        val index = i - 1
        if (index < 0 || index >= outputs.size) {
            throw RuntimeException("No output found with index $i")
        }
        outputs.removeAt(index)
        return this
    }

    fun rmOutput(address: String) {
        outputs.find { it.getDestination() == address }?.let {
            outputs.remove(it)
        }
    }

    fun changeTo(changeAddress: String): TransactionBuilder {
        this.changeAddress = changeAddress
        return this
    }

    fun withFee(fee: Long): TransactionBuilder {
        require(fee >= 0) { ErrorMessages.FEE_NEGATIVE }
        this.fee = fee
        return this
    }

    fun build(): Transaction {
        check(!inputs.isEmpty()) { "Transaction must contain at least one input" }

        val buildOutputs = LinkedList(outputs)

        if (change > 0) {
            buildOutputs.add(Output(change, changeAddress!!, OutputType.CHANGE))
        }

        check(!buildOutputs.isEmpty()) { "Transaction must contain at least one output" }

        val buildSegWitTransaction = containsSegwitInput()

        val transaction = Transaction()
        transaction.addData("Version", VERSION.toString())
        if (buildSegWitTransaction) {
            transaction.addData("Marker", Hex.toHexString(byteArrayOf(SEGWIT_MARKER)), false)
            transaction.addData("Flag", Hex.toHexString(byteArrayOf(SEGWIT_FLAG)), false)
        }

        transaction.addData("Input count", VarInt.of(inputs.size.toLong()).toString())
        val witnesses = LinkedList<ByteArray>()
        for (i in inputs.indices) {
            val sigHash = getSigHash(buildOutputs, i)
            inputs[i].fillTransaction(sigHash, transaction)
            if (buildSegWitTransaction) {
                witnesses.add(inputs[i].getWitness(sigHash))
            }
        }

        transaction.addData("Output count", VarInt.of(buildOutputs.size.toLong()).toString())
        for (output in buildOutputs) {
            output.fillTransaction(transaction)
        }

        if (buildSegWitTransaction) {
            transaction.addHeader("Witnesses")
            for (w in witnesses) {
                transaction.addData("   Witness", Hex.toHexString(w), false)
            }
        }

        transaction.addData("Locktime", LOCK_TIME.toString())

        transaction.setFee(fee)

        return transaction
    }

    fun buildTest(): Transaction {
        testTx = true
        check(!inputs.isEmpty()) { "Transaction must contain at least one input" }

        val buildOutputs = LinkedList(outputs)

        val change = change
        if (change > 0) {
            buildOutputs.add(Output(change, changeAddress!!, OutputType.CHANGE))
        }

        check(!buildOutputs.isEmpty()) { "Transaction must contain at least one output" }

        val buildSegWitTransaction = containsSegwitInput()

        val transaction = Transaction()
        transaction.addData("Version", VERSION.toString())
        if (buildSegWitTransaction) {
            transaction.addData("Marker", Hex.toHexString(byteArrayOf(SEGWIT_MARKER)), false)
            transaction.addData("Flag", Hex.toHexString(byteArrayOf(SEGWIT_FLAG)), false)
        }

        transaction.addData("Input count", VarInt.of(inputs.size.toLong()).toString())
        val witnesses = LinkedList<ByteArray>()
        for (i in inputs.indices) {
            val sigHash = getSigHash(buildOutputs, i)
            inputs[i].fillTransaction(sigHash, transaction)
            if (buildSegWitTransaction) {
                witnesses.add(inputs[i].getWitness(sigHash))
            }
        }

        transaction.addData("Output count", VarInt.of(buildOutputs.size.toLong()).toString())
        for (output in buildOutputs) {
            output.fillTransaction(transaction)
        }

        if (buildSegWitTransaction) {
            transaction.addHeader("Witnesses")
            for (w in witnesses) {
                transaction.addData("   Witness", Hex.toHexString(w), false)
            }
        }

        transaction.addData("Locktime", LOCK_TIME.toString())

        transaction.setFee(fee)

        return transaction
    }

    private fun getSigHash(buildOutputs: List<Output>, signedInputIndex: Int): ByteArray {
        val signBase = ByteBuffer()

        signBase.append(*VERSION.asLitEndBytes())
        signBase.append(
            *SigPreimageProducer.producePreimage(
                inputs[signedInputIndex].isSegWit,
                inputs,
                buildOutputs,
                signedInputIndex
            )
        )
        signBase.append(*LOCK_TIME.asLitEndBytes())
        signBase.append(*SigHashType.ALL.asLitEndBytes())

        return Sha256Hash.hashTwice(signBase.bytes())
    }


    private fun containsSegwitInput(): Boolean {
        for (input in inputs) {
            if (input.isSegWit) {
                return true
            }
        }
        return false
    }

    override fun toString(): String {
        val result = StringBuilder()

        result.appendln("Network: MainNet")
        if (inputs.size > 0) {
            result.appendln("Segwit transaction: ").append(containsSegwitInput())
            var sum = 0L
            for (input in inputs) {
                val satoshi = input.satoshi
                sum += satoshi
            }
            result.appendln("Inputs: ").append(sum)
            for (i in inputs.indices) {
                result.appendln("   ").append(i + 1).append(". ").append(inputs[i])
            }
        }
        if (outputs.size > 0) {
            var sum = 0L
            for (output in outputs) {
                val satoshi = output.satoshi
                sum += satoshi
            }
            result.appendln("Outputs: ").append(sum)
            for (i in outputs.indices) {
                result.appendln("   ").append(i + 1).append(". ").append(outputs[i])
            }
        }
        changeAddress?.let {
            result.appendln("Change to: ").append(it)
        }
        if (fee > 0) {
            result.appendln("Fee: ").append(fee)
        }
        result.appendln()

        return result.toString()
    }

    companion object {

        private val VERSION = UInt.of(1)
        private val SEGWIT_MARKER = 0x00.toByte()
        private val SEGWIT_FLAG = 0x01.toByte()
        private val LOCK_TIME = UInt.of(0)

        fun create(): TransactionBuilder {
            return TransactionBuilder()
        }
    }
}
