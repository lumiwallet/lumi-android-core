package com.lumiwallet.android.core.crypto.hd

import com.google.common.base.Joiner
import com.google.common.collect.ImmutableList
import com.google.common.collect.Iterables
import com.lumiwallet.android.core.crypto.ECKey
import org.bouncycastle.crypto.digests.SHA512Digest
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.crypto.params.KeyParameter
import java.nio.ByteBuffer
import java.util.*

/**
 * Static utilities used in BIP 32 Hierarchical Deterministic Wallets (HDW).
 */
object HDUtils {
    private val PATH_JOINER = Joiner.on("/")

    fun createHmacSha512Digest(key: ByteArray): HMac {
        val digest = SHA512Digest()
        val hMac = HMac(digest)
        hMac.init(KeyParameter(key))
        return hMac
    }

    fun hmacSha512(hmacSha512: HMac, input: ByteArray): ByteArray {
        hmacSha512.reset()
        hmacSha512.update(input, 0, input.size)
        val out = ByteArray(64)
        hmacSha512.doFinal(out, 0)
        return out
    }

    fun hmacSha512(key: ByteArray, data: ByteArray): ByteArray {
        return hmacSha512(createHmacSha512Digest(key), data)
    }

    internal fun toCompressed(uncompressedPoint: ByteArray): ByteArray {
        return ECKey.CURVE.curve.decodePoint(uncompressedPoint).getEncoded(true)
    }

    internal fun longTo4ByteArray(n: Long): ByteArray {
        val bytes = Arrays.copyOfRange(ByteBuffer.allocate(8).putLong(n).array(), 4, 8)
        assert(bytes.size == 4) { bytes.size }
        return bytes
    }

    /** Append a derivation level to an existing path  */
    fun append(path: List<ChildNumber>, childNumber: ChildNumber): ImmutableList<ChildNumber> {
        return ImmutableList.builder<ChildNumber>().addAll(path).add(childNumber).build()
    }

    fun concat(path: List<ChildNumber>, path2: List<ChildNumber>): ImmutableList<ChildNumber> {
        return ImmutableList.builder<ChildNumber>().addAll(path).addAll(path2).build()
    }

    /** Convert to a string path, starting with "M/"  */
    fun formatPath(path: List<ChildNumber>): String {
        return PATH_JOINER.join(Iterables.concat(Collections.singleton("M"), path))
        //PATH_JOINER.join(Iterables.concat<Comparable<out Comparable<*>>>(setOf("M"), path))
    }

    fun getKeyFromSeed(path: String, seed: ByteArray): DeterministicKey {
        var key: DeterministicKey = HDKeyDerivation.createMasterPrivateKey(seed)
        for (chunk in path.split( '/')) {
            var hardened = false
            var indexText = chunk

            if (chunk.contains("'") || chunk.contains("H")) {
                hardened = true
                indexText = indexText.dropLast(1)
            }

            val index = indexText.toInt()

            key = HDKeyDerivation.deriveChildKey(key, ChildNumber(index, hardened))
        }
        return key
    }

    /**
     * The path is a human-friendly representation of the deterministic path. For example:
     *
     * "44H / 0H / 0H / 1 / 1"
     *
     * Where a letter "H" means hardened key. Spaces are ignored.
     */
    fun parsePath(path: String): List<ChildNumber> {
        val parsedNodes = path.replace("M", "").split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val nodes = ArrayList<ChildNumber>()

        for (n in parsedNodes) {
            var N = n.replace(" ".toRegex(), "")
            if (N.isEmpty()) continue
            val isHard = N.endsWith("H")
            if (isHard) N = N.substring(0, n.length - 1)
            val nodeNumber = Integer.parseInt(n)
            nodes.add(ChildNumber(nodeNumber, isHard))
        }

        return nodes
    }
}
