# DeterministicSeed
An object for working with mnemonic and seed

## Constructors

```kotlin
constructor(
  random: SecureRandom = SecureRandom(),
  bits: Int = DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS,
  passphrase: String = ""
)
```

#### Parameters
* **random** - Implimentation of java.security.SecureRandom
* **bits** - Entropy bits length
* **passphrase** - Password phrase that is used as a salt to PBKDF2 function

```kotlin
constructor(
  mnemonicCode: List<String>,
  passphrase: String = "",
  creationTimeSeconds: Long = 1409478661L
)
```

#### Parameters
* **mnemonicCode** - Mnemonic words
* **passphrase** - Password phrase that is used as a salt to PBKDF2 function (empty by default)
* **creationTimeSeconds** - creation time of this object, is used in overrided methods hashCode() and equals()

## Properties

**val seedBytes: ByteArray** Seed bytes

**val mnemonicCode: List<String>** Mnemonic words

**internal val entropyBytes: ByteArray** Entropy bytes
    
## Exceptions
* MnemonicException
* IllegalArgumentException (for incorrect entropy sizes)

## Usage examples

 * #### Create mnemonic:

```kotlin 
val seed = DeterministicSeed()
val mnemonic = Joiner.on(" ").join(seed.mnemonicCode)
val seedHexString = Hex.decode(seed.seedBytes)

println(seedHexString)
println(mnemonic)
```

* #### Import mnemonic:

```kotlin 
val mnemonic = arrayListOf("inject", "quiz", "garbage", "deliver", "vote", "stay", "slush", "south", "security", "focus", "tiny", "cruel")
val passphrase = "" 

val seed = DeterministicSeed(mnemonic, passphrase)

val seedHexString = Hex.decode(seed.seedBytes)
println(seedHexString)
```

# Mnemonic
An object for working with mnemonic 

## Constructors

create instance with default worldlist
```java
  public MnemonicCode()
```

create instance with custom wordlist
```java
  public MnemonicCode(InputStream wordstream, String wordListDigest)
```
#### Parameters
* **InputStream wordstream** stream from file with words
* **String wordListDigest** sha256 digest
    
## Exceptions
* MnemonicException
* IllegalArgumentException

## Usage examples

Entropy to mnemonic:
```kotlin
val testEntropy = byteArrayOf( /*entropy*/ )
val path = "src/main/assets/bip39-wordlist-en"
val sign = "ad90bf3beb7b0eb7e5acd74727dc0da96e0a280a258354e7293fb7e211ac03db"
val stream = File(path).inputStream()
val mnemonicCode = MnemonicCode(stream, sign)

val mnemonic = mnemonicCode.toMnemonic(testEntropy)

println(mnemonic.toTypedArray())
```
