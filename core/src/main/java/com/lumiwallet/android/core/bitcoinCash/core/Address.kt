package com.lumiwallet.android.core.bitcoinCash.core

import com.google.common.base.Objects
import com.google.common.base.Preconditions.checkNotNull
import com.google.common.primitives.UnsignedBytes
import com.lumiwallet.android.core.bitcoinCash.script.ScriptType
import com.lumiwallet.android.core.crypto.ECKey
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.*

abstract class Address(
    @field:Transient
    protected val params: NetworkParameters,
    protected val bytes: ByteArray
) : Serializable, Cloneable, Comparable<Address> {

    abstract val hash: ByteArray

    abstract val outputScriptType: ScriptType

    override fun hashCode(): Int {
        return Objects.hashCode(params, Arrays.hashCode(bytes))
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val other = o as Address?
        return this.params == other!!.params && Arrays.equals(this.bytes, other.bytes)
    }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Address {
        return super.clone() as Address
    }

    override fun compareTo(o: Address): Int {
        val result = this.params.id.compareTo(o.params.id)
        return if (result != 0) result else UnsignedBytes.lexicographicalComparator().compare(this.bytes, o.bytes)
    }

    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) {
        out.defaultWriteObject()
        out.writeUTF(params.id)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(inputStream: ObjectInputStream) {
        inputStream.defaultReadObject()
        try {
            val paramsField = Address::class.java.getDeclaredField("params")
            paramsField.isAccessible = true
            paramsField.set(this, checkNotNull(NetworkParameters.fromID(inputStream.readUTF())!!))
            paramsField.isAccessible = false
        } catch (x: NoSuchFieldException) {
            throw RuntimeException(x)
        } catch (x: IllegalAccessException) {
            throw RuntimeException(x)
        }

    }

    companion object {

        fun fromKey(params: NetworkParameters, key: ECKey, outputScriptType: ScriptType): Address =
            when (outputScriptType) {
                ScriptType.P2PKH -> LegacyAddress.fromKey(params, key)
                else -> throw IllegalArgumentException(outputScriptType.toString())
            }
    }
}
