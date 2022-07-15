package com.lumiwallet.android.core.crypto


import com.google.common.base.Preconditions.checkNotNull
import org.bouncycastle.math.ec.ECCurve
import org.bouncycastle.math.ec.ECFieldElement
import org.bouncycastle.math.ec.ECPoint
import java.math.BigInteger
import java.util.*

/**
 * A wrapper around ECPoint that delays decoding of the point for as long as possible. This is useful because point
 * encode/decode in Bouncy Castle is quite slow especially on Dalvik, as it often involves decompression/recompression.
 */
class LazyECPoint {

    private val curve: ECCurve?
    private val bits: ByteArray?

    // This field is effectively final - once set it won't change again. However it can be set after
    // construction.
    private var point: ECPoint? = null

    // Delegated methods.

    val detachedPoint: ECPoint
        get() = get().detachedPoint

    val encoded: ByteArray
        get() = if (bits != null)
            Arrays.copyOf(bits, bits.size)
        else
            get().getEncoded(true)

    val isInfinity: Boolean
        get() = get().isInfinity

    val yCoord: ECFieldElement
        get() = get().yCoord

    val zCoords: Array<ECFieldElement>
        get() = get().zCoords

    val isNormalized: Boolean
        get() = get().isNormalized

    val isCompressed: Boolean
        get() = if (bits != null)
            bits[0].toInt() == 2 || bits[0].toInt() == 3
        else {
            get()
            true//get().isCompressed
        }


    val isValid: Boolean
        get() = get().isValid

    val xCoord: ECFieldElement
        get() = get().xCoord

    val y: ECFieldElement
        get() = this.normalize().yCoord

    val affineYCoord: ECFieldElement
        get() = get().affineYCoord

    val affineXCoord: ECFieldElement
        get() = get().affineXCoord

    val x: ECFieldElement
        get() = this.normalize().xCoord

    private val canonicalEncoding: ByteArray
        get() = getEncoded(true)

    constructor(curve: ECCurve, bits: ByteArray) {
        this.curve = curve
        this.bits = bits
    }

    constructor(point: ECPoint) {
        this.point = checkNotNull(point)
        this.curve = null
        this.bits = null
    }

    fun get(): ECPoint {
        if (point == null)
            point = curve?.decodePoint(bits!!)
        return point!!
    }

    fun timesPow2(e: Int): ECPoint {
        return get().timesPow2(e)
    }

    fun multiply(k: BigInteger): ECPoint {
        return get().multiply(k)
    }

    fun subtract(b: ECPoint): ECPoint {
        return get().subtract(b)
    }

    fun scaleY(scale: ECFieldElement): ECPoint {
        return get().scaleY(scale)
    }

    fun scaleX(scale: ECFieldElement): ECPoint {
        return get().scaleX(scale)
    }

    fun equals(other: ECPoint): Boolean {
        return get().equals(other)
    }

    fun negate(): ECPoint {
        return get().negate()
    }

    fun threeTimes(): ECPoint {
        return get().threeTimes()
    }

    fun getZCoord(index: Int): ECFieldElement {
        return get().getZCoord(index)
    }

    fun getEncoded(compressed: Boolean): ByteArray {
        return if (compressed == isCompressed && bits != null)
            Arrays.copyOf(bits, bits.size)
        else
            get().getEncoded(compressed)
    }

    fun add(b: ECPoint): ECPoint {
        return get().add(b)
    }

    fun twicePlus(b: ECPoint): ECPoint {
        return get().twicePlus(b)
    }

    fun getCurve(): ECCurve {
        return get().curve
    }

    fun normalize(): ECPoint {
        return get().normalize()
    }

    fun twice(): ECPoint {
        return get().twice()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        return if (o == null || javaClass != o.javaClass) false else canonicalEncoding.contentEquals((o as LazyECPoint).canonicalEncoding)
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(canonicalEncoding)
    }
}
