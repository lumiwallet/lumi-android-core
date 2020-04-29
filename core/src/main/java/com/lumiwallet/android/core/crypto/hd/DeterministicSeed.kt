package com.lumiwallet.android.core.crypto.hd


import com.google.common.base.MoreObjects
import com.google.common.base.Objects
import com.google.common.base.Preconditions.checkArgument
import com.google.common.base.Splitter
import com.lumiwallet.android.core.utils.Utils
import com.lumiwallet.android.core.utils.Utils.HEX
import java.nio.charset.StandardCharsets
import java.security.SecureRandom

class DeterministicSeed {

    val seedBytes: ByteArray
    val mnemonicCode: List<String>

    private var creationTimeSeconds: Long = 0

    private val mnemonicAsBytes: ByteArray
        get() = Utils.SPACE_JOINER.join(mnemonicCode).toByteArray(StandardCharsets.UTF_8)

    internal val entropyBytes: ByteArray
        @Throws(MnemonicException::class)
        get() = MnemonicCode.INSTANCE.toEntropy(mnemonicCode, true)

    constructor(
            mnemonicCode: List<String>,
            passphrase: String = "",
            creationTimeSeconds: Long = 1409478661L
    ) {
        this.mnemonicCode = mnemonicCode
        this.seedBytes = MnemonicCode.toSeed(mnemonicCode, passphrase)
        this.creationTimeSeconds = creationTimeSeconds
    }

    constructor(
            random: SecureRandom = SecureRandom(),
            bits: Int = DEFAULT_SEED_ENTROPY_BITS,
            passphrase: String = ""
    ) {
        val entropy: ByteArray = getEntropy(random, bits)

        checkArgument(entropy.size % 4 == 0, "entropy size in bits not divisible by 32")
        checkArgument(entropy.size * 8 >= DEFAULT_SEED_ENTROPY_BITS, "entropy size too small")

        this.mnemonicCode = MnemonicCode.INSTANCE.toMnemonic(entropy)
        this.seedBytes = MnemonicCode.toSeed(mnemonicCode, passphrase)
        this.creationTimeSeconds = Utils.currentTimeSeconds()
    }


    override fun toString(): String = toString(false)

    fun toString(includePrivate: Boolean): String {
        val helper = MoreObjects.toStringHelper(this)
        if (includePrivate)
            helper.addValue(toHexString()).add("mnemonicCode", Utils.SPACE_JOINER.join(mnemonicCode))
        else
            helper.addValue("unencrypted")
        return helper.toString()
    }

    fun toHexString(): String = HEX.encode(seedBytes)

    fun setCreationTimeSeconds(creationTimeSeconds: Long) {
        this.creationTimeSeconds = creationTimeSeconds
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val other = o as DeterministicSeed?
        return creationTimeSeconds == other?.creationTimeSeconds && Objects.equal(mnemonicCode, other.mnemonicCode)
    }

    override fun hashCode(): Int = Objects.hashCode(creationTimeSeconds, mnemonicCode)

    /**
     * Check if our mnemonic is a valid mnemonic phrase for our word list.
     */
    @Throws(MnemonicException::class)
    fun check(ignoreChecksum: Boolean) {
        MnemonicCode.INSTANCE.check(mnemonicCode, ignoreChecksum)
    }

    companion object {
        // It would take more than 10^12 years to brute-force a 128 bit seed using $1B worth of computing equipment.
        const val DEFAULT_SEED_ENTROPY_BITS: Int = 128
        private const val MAX_SEED_ENTROPY_BITS = 512

        private fun getEntropy(random: SecureRandom, bits: Int): ByteArray {
            checkArgument(bits <= MAX_SEED_ENTROPY_BITS, "requested entropy size too large")

            val seed = ByteArray(bits / 8)
            random.nextBytes(seed)
            return seed
        }

        private fun decodeMnemonicCode(mnemonicCode: ByteArray): List<String> {
            return decodeMnemonicCode(String(mnemonicCode, StandardCharsets.UTF_8))
        }

        private fun decodeMnemonicCode(mnemonicCode: String): List<String> {
            return Splitter.on(" ").splitToList(mnemonicCode)
        }
    }
}
