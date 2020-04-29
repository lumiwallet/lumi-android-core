package com.lumiwallet.android.core.crypto.hd

import com.google.common.base.MoreObjects
import com.google.common.base.Objects
import com.google.common.base.Preconditions.*
import com.google.common.collect.ImmutableList
import com.lumiwallet.android.core.crypto.ECKey
import com.lumiwallet.android.core.crypto.LazyECPoint
import com.lumiwallet.android.core.utils.Utils
import com.lumiwallet.android.core.utils.Utils.HEX
import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.*

class DeterministicKey : ECKey {

    private var parent: DeterministicKey? = null

    val path: ImmutableList<ChildNumber>
    val depth: Int

    var parentFingerprint: Int = 0
        private set // 0 if this key is root node of key hierarchy

    /** 32 bytes  */
    val chainCode: ByteArray

    /**
     * Returns the path of this key as a human readable string starting with M to indicate the master key.
     */
    val pathAsString: String
        get() = HDUtils.formatPath(path)

    val childNumber: ChildNumber
        get() = if (path.size == 0) ChildNumber.ZERO else path[path.size - 1]

    val identifier: ByteArray
        get() = Utils.sha256hash160(pubKey)

    /** Returns the first 32 bits of the result of [identifier].  */
    val fingerprint: Int
        get() = ByteBuffer.wrap(identifier.copyOfRange(0, 4)).int

    val privKeyBytes33: ByteArray
        get() {
            val bytes33 = ByteArray(33)
            System.arraycopy(
                    privKeyBytes,
                    0,
                    bytes33,
                    33 - privKeyBytes.size,
                    privKeyBytes.size
            )
            return bytes33
        }

    /** Constructs a key from its components. This is not normally something you should use.  */
    constructor(
            childNumberPath: ImmutableList<ChildNumber>,
            chainCode: ByteArray,
            publicAsPoint: LazyECPoint,
            priv: BigInteger?,
            parent: DeterministicKey?
    ) : super(
            priv,
            compressPoint(checkNotNull<LazyECPoint>(publicAsPoint))
    ) {
        checkArgument(chainCode.size == 32)
        this.parent = parent
        this.path = checkNotNull(childNumberPath)
        this.chainCode = Arrays.copyOf(chainCode, chainCode.size)
        this.depth = if (parent == null) 0 else parent.depth + 1
        this.parentFingerprint = parent?.fingerprint ?: 0
    }

    /** Constructs a key from its components. This is not normally something you should use.  */
    constructor(
            childNumberPath: ImmutableList<ChildNumber>,
            chainCode: ByteArray,
            priv: BigInteger,
            parent: DeterministicKey?
    ) : super(
            priv,
            compressPoint(publicPointFromPrivate(priv))
    ) {
        checkArgument(chainCode.size == 32)
        this.parent = parent
        this.path = checkNotNull(childNumberPath)
        this.chainCode = chainCode.copyOf(chainCode.size)
        this.depth = if (parent == null) 0 else parent.depth + 1
        this.parentFingerprint = parent?.fingerprint ?: 0
    }

    fun dropParent(): DeterministicKey {
        val key = DeterministicKey(path, chainCode, pub, priv, null)
        key.parentFingerprint = parentFingerprint
        return key
    }

    override val isPubKeyOnly: Boolean
        get() = super.isPubKeyOnly && (parent?.isPubKeyOnly ?: true)

    override fun hasPrivKey(): Boolean {
        return findParentWithPrivKey() != null
    }

    private fun findParentWithPrivKey(): DeterministicKey? {
        var cursor: DeterministicKey? = this
        while (cursor != null) {
            if (cursor.priv != null) break
            cursor = cursor.parent
        }
        return cursor
    }

    private fun findOrDerivePrivateKey(): BigInteger? {
        val cursor = findParentWithPrivKey() ?: return null
        return derivePrivateKeyDownwards(cursor, cursor.priv!!.toByteArray())
    }

    private fun derivePrivateKeyDownwards(cursor: DeterministicKey, parentalPrivateKeyBytes: ByteArray): BigInteger {
        var downCursor = DeterministicKey(cursor.path, cursor.chainCode,
                cursor.pub, BigInteger(1, parentalPrivateKeyBytes), cursor.parent)
        val path = this.path.subList(cursor.path.size, this.path.size)
        for (num in path) {
            downCursor = HDKeyDerivation.deriveChildKey(downCursor, num)
        }
        return downCursor.priv!!
    }

    override val privKey: BigInteger
        get() {
            val key = findOrDerivePrivateKey()
            checkState(key != null, "Private key bytes not available")
            return key!!
        }

    public override var creationTimeSeconds: Long = 0L
    set(value) {
        check(parent == null) { "Creation time can only be set on root keys." }
        field = value
        super.creationTimeSeconds = value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val other = other as DeterministicKey?
        return (super.equals(other)
                && this.chainCode.contentEquals(other.chainCode)
                && Objects.equal(this.path, other.path))
    }

    override fun hashCode(): Int {
        return Objects.hashCode(super.hashCode(), Arrays.hashCode(chainCode), path)
    }

    override fun toString(): String {
        val helper = MoreObjects.toStringHelper(this).omitNullValues()
        helper.add("pub", HEX.encode(pub.encoded))
        helper.add("chainCode", HEX.encode(chainCode))
        helper.add("path", pathAsString)
        if (creationTimeSeconds > 0)
            helper.add("creationTimeSeconds", creationTimeSeconds)
        helper.add("isPubKeyOnly", isPubKeyOnly)
        return helper.toString()
    }

}
