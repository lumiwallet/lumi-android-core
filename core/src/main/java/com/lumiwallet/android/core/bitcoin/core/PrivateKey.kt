package com.lumiwallet.android.core.bitcoin.core


import com.google.common.base.Objects
import com.google.common.base.Preconditions
import com.google.common.primitives.UnsignedBytes
import com.lumiwallet.android.core.bitcoin.params.MainNetParams
import com.lumiwallet.android.core.crypto.ECKey
import com.lumiwallet.android.core.utils.Base58
import com.lumiwallet.android.core.utils.Sha256Hash
import java.io.Serializable


class PrivateKey : Serializable, Cloneable, Comparable<PrivateKey> {

    companion object {

        private fun encode(
            keyBytes: ByteArray,
            compressed: Boolean
        ): ByteArray {
            Preconditions.checkArgument(keyBytes.size == 32, "Private keys must be 32 bytes")
            return if (!compressed) {
                keyBytes
            } else {
                val bytes = ByteArray(33)
                System.arraycopy(keyBytes, 0, bytes, 0, 32)
                bytes[32] = 1
                bytes
            }
        }

        fun ofWif(
            wif: String
        ): PrivateKey {
            validateWifFormat(wif)
            var compressed = false
            val prefix = 0x80.toByte()
            val decoded: ByteArray = Base58.decode(wif)
            require(decoded[0] == prefix) {
                "Decoded WIF must start with 0x" + prefix.toString(16) + " byte"
            }
            val payload: ByteArray = decoded.copyOfRange(0, decoded.size - 4)
            val checksum: ByteArray = decoded.copyOfRange(decoded.size - 4, decoded.size)

            val shaOfSha = Sha256Hash.hashTwice(payload) ?: byteArrayOf()

            for (i in 0..3) {
                require(shaOfSha[i] == checksum[i]) { "Wrong checksum" }
            }

            val pkBytes: ByteArray
            when (payload.size) {
                33 -> {
                    pkBytes = payload.copyOfRange(1, payload.size)
                }
                34 -> {
                    require(payload[payload.size - 1] == 0x01.toByte()) { "The last byte of decoded compressed WIF is expected to be 0x01" }
                    compressed = true
                    pkBytes = payload.copyOfRange(1, payload.size - 1)
                }
                else -> {
                    throw IllegalArgumentException("Incorrect WIF length: " + payload.size)
                }
            }
            return PrivateKey(ECKey.fromPrivate(pkBytes, compressed))
        }

        private fun validateWifFormat(
            wif: String
        ) {
            val prefixWif = listOf('5')
            val prefixWifComp = listOf('K', 'L')
            val prefix = wif[0]
            require(!(!prefixWif.contains(prefix) && !prefixWifComp.contains(prefix))) {
                "WIF must start with " + prefixWif + " (for uncompressed) " +
                        "or " + prefixWifComp + " (for compressed)"
            }
        }

    }

    constructor(
        params: NetworkParameters,
        keyBytes: ByteArray,
        compressed: Boolean
    ) : this(params, encode(keyBytes, compressed))

    constructor(ecKey: ECKey) {
        this.params = MainNetParams
        this.bytes = encode(ecKey.privKeyBytes, ecKey.isCompressed)
        this.key = ecKey
        if (bytes.size != 32 && bytes.size != 33)
            throw AddressFormatException.InvalidDataLength(
                "Wrong number of bytes for a private key (32 or 33): " + bytes.size
            )
    }

    private constructor(params: NetworkParameters, bytes: ByteArray) {
        this.params = params
        this.bytes = bytes
        this.key = ECKey.fromPrivate(bytes)
        if (bytes.size != 32 && bytes.size != 33)
            throw AddressFormatException.InvalidDataLength(
                "Wrong number of bytes for a private key (32 or 33): " + bytes.size
            )
    }

    @Transient
    private val params: NetworkParameters
    private val bytes: ByteArray
    val key: ECKey

    @Suppress("unused")
    val isPubKeyCompressed: Boolean
        get() = bytes.size == 33 && bytes[32].toInt() == 1

    val publicKey: ByteArray
        get() = key.pubKey

    /**
     * Sha256 + RipeMD160
     * */
    val publicKeyHash: ByteArray
        get() = key.pubKeyHash

    @Suppress("MemberVisibilityCanBePrivate")
    fun toBase58(): String = Base58.encodeChecked(params.dumpedPrivateKeyHeader, bytes)

    fun sign(bytesToSign: ByteArray): ByteArray =
        key.sign(Sha256Hash.wrap(bytesToSign)).encodeToDER()

    override fun hashCode(): Int = Objects.hashCode(params, bytes.contentHashCode())

    override fun toString(): String = toBase58()

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): PrivateKey = super.clone() as PrivateKey

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        return this.params == (other as PrivateKey).params && this.bytes.contentEquals(other.bytes)
    }

    override fun compareTo(other: PrivateKey): Int {
        val result = this.params.id.compareTo(other.params.id)
        return if (result != 0) result else UnsignedBytes.lexicographicalComparator()
            .compare(this.bytes, other.bytes)
    }
}