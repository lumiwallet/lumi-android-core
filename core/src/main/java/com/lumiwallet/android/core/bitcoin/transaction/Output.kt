package com.lumiwallet.android.core.bitcoin.transaction

import com.lumiwallet.android.core.bitcoin.constant.ErrorMessages
import com.lumiwallet.android.core.bitcoin.types.ULong
import com.lumiwallet.android.core.bitcoin.types.VarInt
import com.lumiwallet.android.core.bitcoin.util.ByteBuffer
import com.lumiwallet.android.core.bitcoin.util.ValidationUtils.isBase58
import com.lumiwallet.android.core.bitcoin.util.ValidationUtils.isEmpty
import com.lumiwallet.android.core.utils.Base58
import com.lumiwallet.android.core.utils.Sha256Hash
import org.bouncycastle.util.encoders.Hex
import java.util.*

enum class OutputType(val desc: String) {
    CUSTOM("Custom"),
    CHANGE("Change")
}


internal class Output(
        val satoshi: Long,
        private val destination: String,
        private val type: OutputType
) {

    private val decodedAddress: ByteArray

    private val lockingScript: ByteArray
        get() = ScriptPubKeyProducer.produceScript(decodedAddress[0], decodedAddress.copyOfRange(1, decodedAddress.size))

    init {
        validateOutputData(satoshi, destination)
        this.decodedAddress = decodeAndValidateAddress(destination)
    }

    fun serializeForSigHash(): ByteArray {
        val lockingScript = lockingScript
        val serialized = ByteBuffer().apply {
            append(*ULong.of(satoshi).asLitEndBytes())
            append(*VarInt.of(lockingScript.size.toLong()).asLitEndBytes())
            append(*lockingScript)
        }

        return serialized.bytes()
    }

    fun fillTransaction(transaction: Transaction) {
        transaction.addHeader("   Output (${type.desc})")
        transaction.addData("      Satoshi", ULong.of(satoshi).toString())
        transaction.addData("      Lock length", VarInt.of(lockingScript.size.toLong()).toString())
        transaction.addData("      Lock", Hex.toHexString(lockingScript))
    }

    override fun toString(): String {
        return "$destination $satoshi"
    }

    fun getDestination(): String = destination

    private fun validateOutputData(satoshi: Long, destination: String) {
        validateDestinationAddress(destination)
        validateAmount(satoshi);
    }

    private fun validateAmount(satoshi: Long) {
        require(satoshi > 0) { ErrorMessages.OUTPUT_AMOUNT_NOT_POSITIVE }
    }

    private fun validateDestinationAddress(destination: String) {
        require(!isEmpty(destination)) { ErrorMessages.OUTPUT_ADDRESS_EMPTY }
        require(isBase58(destination)) { ErrorMessages.OUTPUT_ADDRESS_NOT_BASE_58 }

        val prefixP2PKH = listOf('1')
        val prefixP2SH = listOf('3')
        val prefix = destination[0]

        require(!(!prefixP2PKH.contains(prefix) && !prefixP2SH.contains(prefix))) {
            String.format(ErrorMessages.OUTPUT_ADDRESS_WRONG_PREFIX, prefixP2PKH, prefixP2SH)
        }
    }

    private fun decodeAndValidateAddress(base58: String): ByteArray {

        val decoded = Base58.decode(base58)

        val payload = Arrays.copyOfRange(decoded, 0, decoded.size - 4)
        val checksum = Arrays.copyOfRange(decoded, decoded.size - 4, decoded.size)

        val shaOfSha = Sha256Hash.hashTwice(payload) ?: byteArrayOf()

        for (i in 0..3) {
            require(shaOfSha[i] == checksum[i]) { "Wrong checksum" }
        }

        return payload

    }
}
