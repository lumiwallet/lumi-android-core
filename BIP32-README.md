 # DeterministicKey
 this object is a node in a deterministic hierarchy. "Child" of ECKey
 
 ## Initializers

from seed:
```kotlin
val seed = byteArrayOf()
val dKey = HDKeyDerivation.createMasterPrivateKey(seed)
```

from parent key:
```kotlin
val childIndex = 42
val dKey = HDKeyDerivation.deriveChildKey(key, childIndex)
```

from seed with derivation path:
```kotlin
val seed = byteArrayOf()
val path =  "44'/0'/0'/1"
val dKey = HDUtils.getKeyFromSeed(path, seed)
```

## Properties
* **path** derivation path
```kotlin 
  val path: ImmutableList<ChildNumber>
```

* **depth** derivation depth
```kotlin 
  val depth: Int
```

* **identifier** RIPEMD160 of Sha256 of public key
```kotlin 
  val identifier: ByteArray
```

* **parentFingerprint** First 4 bytes of identifier (0 if root is this key)
```kotlin 
  var parentFingerprint: Int = 0
```



## Exceptions 

* HDDerivationException
* IllegalArgumentException

## Usage example

async generation keys in range:
* RxJava3 required

```kotlin 
fun generate(
  key: DeterministicKey,
  start: String,
  end: String
): Single<MutableList<String>> = Single.create { emitter ->
  
  emitter.onSuccess((startInt..endInt)
      .map { childIndex ->
          HDKeyDerivation.deriveChildKey(key, childIndex).privateKeyAsHex
      } as MutableList<String>)
}
```

print keys and addresses for BTC and EOS:

```kotlin 
val startIndex = 0
val endIndex = 100
val seed = DeterministicSeed().seedBytes // generate random seed
val key = HDUtils.getKeyFromSeed("44'/0'/0'/0", seed)

for (index in startIndex..endIndex) {
     val dk = HDKeyDerivation.deriveChildKey(key, ChildNumber(i, false))
     val btcAddress = Address.fromKey(DogeNetParams, dk, ScriptType.P2PKH).toString()
     val btcKey = ProvateKey(dk).toBase58()
     val eosPrivateKey = EosPrivateKey(dk).toString()
     val eosPublicKey = EosPrivateKey(dk).publicKey.toString()
     println("-----")
     println(btcAddress)
     println(btcKey)
     println(eosPrivateKey)
     println(eosPublicKey)
}
```
