# LegacyAddress and SegwitAddress

bitcoin addresses

## Initializers

from public key hash
```kotlin 
  fun fromPubKeyHash(params: NetworkParameters, hash160: ByteArray)
```

from key 
```kotlin
fun fromKey(params: NetworkParameters, key: ECKey)
```

## Properties

**hash** - RIPEMD160 hash

**outputScriptType** - script type

## Exceptions
* CloneNotSupportedException
* AddressFormatException

## Usage example
get address from ECKey
```kotlin
val seed = byteArrayOf()
val key = HDUtils.getKeyFromSeed("44'/3'/0'/0", seed)
val legacyAddress = LegacyAddress.fromKey(MainNetParams, key)
val segwitAddress = SegwitAddress.fromKey(MainNetParams, key)
println(legacyAddress.toString())
println(segwitAddress.toBech32())
```

# TransactionBuilder
## Initializers

```kotlin
TransactionBuilder.create()
```

#### Methods

**from** - add input to transaction
```kotlin
fun from(
  fromTransactionBigEnd: String,
  fromToutNumber: Int,
  closingScript: String,
  satoshi: Long,
  privateKey: PrivateKey
)
```
```kotlin
fun from(
  unspentOutput: UnspentOutput
)
```

**to** - add output to transaction
```kotlin
fun to(address: String, value: Long)
```

**changeTo** - set output for change
```kotlin
fun changeTo(changeAddress: String)
```
**withFee** - set fee for a single transaction
```kotlin 
fun withFee(fee: Long)
```

**build** - build a transaction
```kotlin
fun build(): Transaction
```

**toString** - return string with all info about transaction, without private keys
```kotlin
override fun toString(): String
```
## Exceptions
* RuntimeException
    
## Usage example


create a transaction builder:
```kotlin 
val builder = TransactionBuilder.create()
```
set inputs, outputs, change address and fee
```kotlin 
val fee = 1000L
val value = 10000L
val output: UnspentOutput = UnspentOutput(
    txHash = "3fd0841a99ea61cd46c8d375c4eb4a85cea24ce0d808dbcc9764ace9b2a57ade",
    txOutputN = 0,
    script = "76a9145677bbdf319a342ac2a51b7d561a6faf05e316fa88ac",
    value = 10000L,
    privateKey = PrivateKey(ECKey.fromPrivate(BigInteger("78621967918052147416620238406219140484586232493349584411343239335111139143052")))
)

builder.from(output)
	.withFee(fee)
	.to("1P7zTvgHxo1WnAAXYV5X5HmfHWDbgdJNH1", value)
	.changeTo("1P7zTvgHxo1WnAAXYV5X5HmfHWDbgdJNH1")
```
Using multiple inputs:
```kotlin 
val onspentOutputs = arraList<UnspentOutput>(/*some outputs here*/)
for (output in onspentOutputs) {
	builder.from(output)
}
```

# Transaction
class with information about transaction

## Properties
**rawTransaction** - raw transaction as string
```kotlin
val rawTransaction: String
```

**rawTransactionAsBytes** - raw transaction as byte array
```kotlin
val rawTransactionAsBytes: ByteArray
```

**hash** - hash
```kotlin
val hash: String
```

**transactionInfo** - text information about transaction
```kotlin
val transactionInfo: String
```
