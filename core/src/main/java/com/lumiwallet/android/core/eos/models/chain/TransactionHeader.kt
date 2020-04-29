package com.lumiwallet.android.core.eos.models.chain

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.lumiwallet.android.core.eos.models.types.EosType
import com.lumiwallet.android.core.eos.utils.HexUtils
import com.lumiwallet.android.core.utils.BitUtils
import java.math.BigInteger
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

open class TransactionHeader : EosType.Packer {
    @SerializedName("delay_sec")
    @Expose
    private var delaySec: Long = 0

    @SerializedName("expiration")
    @Expose
    var expiration: String? = null

    @SerializedName("max_cpu_usage_ms")
    @Expose
    private var maxCpuUsageMs: Long = 0

    @SerializedName("max_net_usage_words")
    @Expose
    private var maxNetUsageWords: Long = 0

    @SerializedName("ref_block_num")
    @Expose
    var refBlockNum = 0
        private set

    @SerializedName("ref_block_prefix")
    @Expose
    var refBlockPrefix = 0L
        private set

    constructor()

    constructor(paramTransactionHeader: TransactionHeader) {
        this.expiration = paramTransactionHeader.expiration
        this.refBlockNum = paramTransactionHeader.refBlockNum
        this.refBlockPrefix = paramTransactionHeader.refBlockPrefix
        this.maxNetUsageWords = paramTransactionHeader.maxNetUsageWords
        this.maxCpuUsageMs = paramTransactionHeader.maxCpuUsageMs
        this.delaySec = paramTransactionHeader.delaySec
    }

    private fun getExpirationAsDate(paramString: String?): Date {
        val localSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        try {
            localSimpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return localSimpleDateFormat.parse(paramString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return Date()
    }

    override fun pack(paramWriter: EosType.Writer) {
        paramWriter.putIntLE((getExpirationAsDate(this.expiration).time / 1000L).toInt())
        paramWriter.putShortLE((this.refBlockNum and 0xFFFF).toShort())
        paramWriter.putIntLE((this.refBlockPrefix and -1L/*0xFFFFFFFFFFFFFFFF*/).toInt())
        paramWriter.putVariableUInt(this.maxNetUsageWords)
        paramWriter.putVariableUInt(this.maxCpuUsageMs)
        paramWriter.putVariableUInt(this.delaySec)
    }

    fun putKcpuUsage(paramLong: Long) {
        this.maxCpuUsageMs = paramLong
    }

    fun putNetUsageWords(paramLong: Long) {
        this.maxNetUsageWords = paramLong
    }

    fun setReferenceBlock(paramString: String) {
        this.refBlockNum = BigInteger(1, HexUtils.toBytes(paramString.substring(0, 8))).toInt()
        this.refBlockPrefix = BitUtils.uint32ToLong(HexUtils.toBytes(paramString.substring(16, 24)), 0)
    }

    fun setBlockNum(blockNum: Int) {
        this.refBlockNum = blockNum
    }

    fun setBlockPrefix(blockPrefix: Long) {
        this.refBlockPrefix = blockPrefix
    }

}
