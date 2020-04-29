# TransactionHeader
Class with base eos transaction fields and implementation of EosPacker

## Properties

**delaySec** - delay
```kotlin
private var delaySec: Long = 0
```

**maxCpuUsageMs** - max CPU usage per transaction
```kotlin
private var maxCpuUsageMs: Long = 0
```

**maxNetUsageWords** - max NET usage per transaction (in words)
```kotlin
private var maxNetUsageWords: Long = 0
```

**refBlockNum** - reference block number
```kotlin
var refBlockNum = 0
```

**expiration** - expiration time
```kotlin
var expiration: String? = null
```

**refBlockPrefix** - reference block prefix
```kotlin
var refBlockPrefix = 0L
```

## methods

Implementation of "pack" method
```kotlin
override fun pack(paramWriter: EosType.Writer)
```

# Transaction
Eos transaction

## Properties

**actions** - array of actions
```kotlin
private var actions: MutableList<Action> = mutableListOf()
```

# SignedTransaction 
Eos transaction with signature, extends transaction class

## Properties

**signatures** - array of signatures
```kotlin
internal var signatures: MutableList<String> = mutableListOf()
```

**ctxFreeData** - array of string with "context free data". Not implemented here, but field is required in transaction
```kotlin
var ctxFreeData: List<String> = ArrayList()
```

## Methods

**sign** sign this transaction with private key and chainId
```kotlin
fun sign(privateKey: EosPrivateKey, chainId: TypeChainId)
```

# PackedTransaction
Packed eos transaction

## Construcrors
```kotlin
constructor(
  stxn: SignedTransaction,
  compressType: CompressType = CompressType.none
)
```

## Properties

**packedTrx** - packed transaction data
```kotlin
private val packedTrx: String
```

**compression** - compression type for packer
```kotlin
internal val compression: String = compressType.name
```

## methods 

**toString** - return packed transaction
 
# Usage example

```kotlin

val privateKey = EosPrivateKey("base58key")
val expiration = 1580811780.835552
val dataAsHex: String = "10f2d4142193b1ca20f2d4142193b1ca010000000000000004454f5300000000044d656d6f"
val chainId: String = "aca376f206b8fc25a6ed44dbdc66547c36c6c33e3a119ffbeaef943642f0e906"
val blockNum: Int = 103423741
val blockPrefix: Int = 2142807285
val permission = "testaccount1@active"

val action = Action(CODE_EOSIO_TOKEN, ACTION_TRANSFER).apply {
  setAuthorization(permissions)
  setData(dataAsHex)
}

val signedTx = SignedTransaction().apply {
  addAction(action)
  putSignatures(ArrayList())
  setBlockNum(blockNum)
  setBlockPrefix(blockPrefix)
  this.expiration = expiration
  sign(privateKey, TypeChainId(chainId))
}

val packedTx = PackedTransaction(signedTx)
```
