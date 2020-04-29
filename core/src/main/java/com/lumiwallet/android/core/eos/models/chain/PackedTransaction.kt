package com.lumiwallet.android.core.eos.models.chain

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.lumiwallet.android.core.eos.models.types.EosByteWriter
import com.lumiwallet.android.core.eos.utils.HexUtils

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.DataFormatException
import java.util.zip.Deflater
import java.util.zip.Inflater

class PackedTransaction @JvmOverloads constructor(
        stxn: SignedTransaction,
        compressType: CompressType = CompressType.none
) {

    @Expose
    @SerializedName("signatures")
    internal val signatures: List<String>? = stxn.signatures

    @Expose
    @SerializedName("compression")
    internal val compression: String = compressType.name

    @Expose
    @SerializedName("packed_context_free_data")
    private val packedContextFreeData: String

    @Expose
    @SerializedName("packed_trx")
    private val packedTrx: String

    enum class CompressType {
        none, zlib
    }

    init {

        packedTrx = HexUtils.toHex(packTransaction(stxn, compressType))

        val packedCtxFreeBytes = packContextFreeData(stxn.ctxFreeData, compressType)
        packedContextFreeData = if (packedCtxFreeBytes.isEmpty()) "" else HexUtils.toHex(packedCtxFreeBytes)
    }

    private fun packTransaction(transaction: Transaction, compressType: CompressType): ByteArray {
        val byteWriter = EosByteWriter(512)
        transaction.pack(byteWriter)

        // pack -> compress
        return compress(byteWriter.toBytes(), compressType)
    }


    private fun packContextFreeData(ctxFreeData: List<String>?, compressType: CompressType): ByteArray {
        val byteWriter = EosByteWriter(64)

        val ctxFreeDataCount = ctxFreeData?.size ?: 0
        if (ctxFreeDataCount == 0) {
            return byteWriter.toBytes()
        }

        byteWriter.putVariableUInt(ctxFreeDataCount.toLong())

        for (hexData in ctxFreeData!!) {
            byteWriter.putBytes(HexUtils.toBytes(hexData))
        }

        return compress(byteWriter.toBytes(), compressType)
    }

    private fun compress(uncompressedBytes: ByteArray, compressType: CompressType?): ByteArray {
        if (compressType == null || CompressType.zlib != compressType) {
            return uncompressedBytes
        }

        // zip!
        val deflater = Deflater(Deflater.BEST_COMPRESSION)
        deflater.setInput(uncompressedBytes)

        val outputStream = ByteArrayOutputStream(uncompressedBytes.size)
        deflater.finish()
        val buffer = ByteArray(1024)
        while (!deflater.finished()) {
            val count = deflater.deflate(buffer)
            outputStream.write(buffer, 0, count)
        }

        try {
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return uncompressedBytes
        }

        return outputStream.toByteArray()
    }

    private fun decompress(compressedBytes: ByteArray): ByteArray {
        val inflater = Inflater()
        inflater.setInput(compressedBytes)

        val outputStream = ByteArrayOutputStream(compressedBytes.size)
        val buffer = ByteArray(1024)

        try {
            while (!inflater.finished()) {
                val count = inflater.inflate(buffer)
                outputStream.write(buffer, 0, count)
            }
            outputStream.close()
        } catch (e: DataFormatException) {
            e.printStackTrace()
            return compressedBytes
        } catch (e: IOException) {
            e.printStackTrace()
            return compressedBytes
        }


        return outputStream.toByteArray()
    }

    override fun toString(): String = packedTrx
}