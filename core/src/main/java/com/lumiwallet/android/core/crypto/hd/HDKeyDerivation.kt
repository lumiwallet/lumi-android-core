package com.lumiwallet.android.core.crypto.hd


import com.google.common.base.Preconditions.checkArgument
import com.google.common.base.Preconditions.checkState
import com.google.common.collect.ImmutableList
import com.lumiwallet.android.core.crypto.ECKey
import com.lumiwallet.android.core.crypto.LazyECPoint
import com.lumiwallet.android.core.utils.Utils
import org.bouncycastle.math.ec.ECPoint
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.*

/**
 * Implementation of the [BIP 32](https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki)
 * deterministic wallet child key generation algorithm.
 */
object HDKeyDerivation {

    // Some arbitrary random number. Doesn't matter what it is.
    private val RAND_INT: BigInteger = BigInteger(256, SecureRandom())

    @Throws(HDDerivationException::class)
    fun createMasterPrivateKey(seed: ByteArray): DeterministicKey {
        checkArgument(seed.size > 8, "Seed is too short and could be brute forced")
        val i = HDUtils.hmacSha512(HDUtils.createHmacSha512Digest("Bitcoin seed".toByteArray()), seed)
        // Use Il as master secret key, and Ir as master chain code.
        checkState(i.size == 64, i.size)
        val il = i.copyOfRange(0, 32)
        val ir = i.copyOfRange(32, 64)
        Arrays.fill(i, 0.toByte())
        val masterPrivKey = createMasterPrivKeyFromBytes(il, ir)
        Arrays.fill(il, 0.toByte())
        Arrays.fill(ir, 0.toByte())
        // Child deterministic keys will chain up to their parents to find the keys.
        masterPrivKey.creationTimeSeconds = Utils.currentTimeSeconds()
        return masterPrivKey
    }

    /**
     * @throws HDDerivationException if privKeyBytes is invalid (not between 0 and n inclusive).
     */
    @Throws(HDDerivationException::class)
    @JvmOverloads
    fun createMasterPrivKeyFromBytes(
            privKeyBytes: ByteArray,
            chainCode: ByteArray,
            childNumberPath: ImmutableList<ChildNumber> = ImmutableList.of()
    ): DeterministicKey {
        val priv = BigInteger(1, privKeyBytes)
        assertNonZero(priv, "Generated master key is invalid.")
        assertLessThanN(priv, "Generated master key is invalid.")
        return DeterministicKey(childNumberPath, chainCode, priv, null)
    }

    fun createMasterPubKeyFromBytes(pubKeyBytes: ByteArray, chainCode: ByteArray): DeterministicKey {
        return DeterministicKey(ImmutableList.of(), chainCode, LazyECPoint(ECKey.CURVE.curve, pubKeyBytes), null, null)
    }

    fun deriveChildKey(
            parent: DeterministicKey,
            childNumber: Int
    ): DeterministicKey {
        return deriveChildKey(parent, ChildNumber(childNumber))
    }

    /**
     * @throws HDDerivationException if private derivation is attempted for a public-only parent key, or
     * if the resulting derived key is invalid (eg. private key == 0).
     */
    @Throws(HDDerivationException::class)
    fun deriveChildKey(parent: DeterministicKey, childNumber: ChildNumber): DeterministicKey {
        return if (parent.hasPrivKey()) {
            val rawKey = deriveChildKeyBytesFromPrivate(parent, childNumber)
            DeterministicKey(
                    HDUtils.append(parent.path, childNumber),
                    rawKey.chainCode,
                    BigInteger(1, rawKey.keyBytes),
                    parent
            )
        } else {
            val rawKey = deriveChildKeyBytesFromPublic(parent, childNumber, PublicDeriveMode.NORMAL)
            DeterministicKey(
                    HDUtils.append(parent.path, childNumber),
                    rawKey.chainCode,
                    LazyECPoint(ECKey.CURVE.curve, rawKey.keyBytes),
                    null,
                    parent
            )
        }
    }

    @Throws(HDDerivationException::class)
    fun deriveChildKeyBytesFromPrivate(
            parent: DeterministicKey,
            childNumber: ChildNumber
    ): RawKeyBytes {
        checkArgument(parent.hasPrivKey(), "Parent key must have private key bytes for this method.")
        val parentPublicKey = parent.pubKeyPoint.getEncoded(true)
        checkState(parentPublicKey.size == 33, "Parent pubkey must be 33 bytes, but is " + parentPublicKey.size)
        val data = ByteBuffer.allocate(37)
        if (childNumber.isHardened) {
            data.put(parent.privKeyBytes33)
        } else {
            data.put(parentPublicKey)
        }
        data.putInt(childNumber.i)
        val i = HDUtils.hmacSha512(parent.chainCode, data.array())
        checkState(i.size == 64, i.size)
        val il = i.copyOfRange(0, 32)
        val chainCode = i.copyOfRange(32, 64)
        val ilInt = BigInteger(1, il)
        assertLessThanN(ilInt, "Illegal derived key: I_L >= n")
        val priv = parent.privKey
        val ki = priv.add(ilInt).mod(ECKey.CURVE.n)
        assertNonZero(ki, "Illegal derived key: derived private key equals 0.")
        return RawKeyBytes(ki.toByteArray(), chainCode)
    }

    enum class PublicDeriveMode {
        NORMAL,
        WITH_INVERSION
    }

    @Throws(HDDerivationException::class)
    fun deriveChildKeyBytesFromPublic(parent: DeterministicKey, childNumber: ChildNumber, mode: PublicDeriveMode): RawKeyBytes {
        checkArgument(!childNumber.isHardened, "Can't use private derivation with public keys only.")
        val parentPublicKey = parent.pubKeyPoint.getEncoded(true)
        checkState(parentPublicKey.size == 33, "Parent pubkey must be 33 bytes, but is " + parentPublicKey.size)
        val data = ByteBuffer.allocate(37)
        data.put(parentPublicKey)
        data.putInt(childNumber.i)
        val i = HDUtils.hmacSha512(parent.chainCode, data.array())
        checkState(i.size == 64, i.size)
        val il = i.copyOfRange(0, 32)
        val chainCode = i.copyOfRange(32, 64)
        val ilInt = BigInteger(1, il)
        assertLessThanN(ilInt, "Illegal derived key: I_L >= n")

        val N = ECKey.CURVE.n
        var Ki: ECPoint
        when (mode) {
            PublicDeriveMode.NORMAL -> Ki = ECKey.publicPointFromPrivate(ilInt).add(parent.pubKeyPoint)
            PublicDeriveMode.WITH_INVERSION -> {
                Ki = ECKey.publicPointFromPrivate(ilInt.add(RAND_INT).mod(N))
                val additiveInverse = RAND_INT.negate().mod(N)
                Ki = Ki.add(ECKey.publicPointFromPrivate(additiveInverse))
                Ki = Ki.add(parent.pubKeyPoint)
            }
        }

        assertNonInfinity(Ki, "Illegal derived key: derived public key equals infinity.")
        return RawKeyBytes(Ki.getEncoded(true), chainCode)
    }

    private fun assertNonZero(integer: BigInteger, errorMessage: String) {
        if (integer == BigInteger.ZERO)
            throw HDDerivationException(errorMessage)
    }

    private fun assertNonInfinity(point: ECPoint, errorMessage: String) {
        if (point.equals(ECKey.CURVE.curve.infinity))
            throw HDDerivationException(errorMessage)
    }

    private fun assertLessThanN(integer: BigInteger, errorMessage: String) {
        if (integer > ECKey.CURVE.n)
            throw HDDerivationException(errorMessage)
    }

    class RawKeyBytes(val keyBytes: ByteArray, val chainCode: ByteArray)
}