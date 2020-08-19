
[![License](https://img.shields.io/badge/license-MIT-black.svg?style=flat)](https://mit-license.org)
[![Version](https://img.shields.io/badge/Version-1.0-orange.svg)]()

![Frame 12](https://user-images.githubusercontent.com/63339503/79841746-88c82900-83c0-11ea-9ab4-a2a98635e167.png)

# LumiCore

The LumiCore library is a Kotlin implementation of tools for working with cryptocurrency transactions, such as Bitcoin, Bitcoin Cash and EOS.

## Requirements

* Android Studio 3.6

## Installation

1. Add it in your root build.gradle at the end of repositories:

```
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```
  
2. Add the dependency:

```
dependencies {
  implementation 'com.github.lumiwallet:lumi-android-core:v1.1.0'
}
```

## Content

* [Mnemonic and seed](/BIP39-README.md)
* [Key generation](/BIP32-README.md)
* [Bitcoin](/BTC-README.md)
* [EOS](/EOS-README.md)
* **ECDSA** for signing we using ECDSASigner from bouncycastle. ECKey class can sign anithing with ```sign(input: Sha256Hash)``` method

# LumiCoreApp

## Run

To run the test application, you need to:
 
 1. Clone or download repo
 2. Open in Android Studio and wait for gradle sync
 3. Click Run button
 
## NIST SP 800-22 testing

1. Generate data file in the application (NIST SP 800-2 button) and copy to pc

2. Download test suite sources from https://csrc.nist.gov/projects/random-bit-generation/documentation-and-software

3. run ./make -f makefile to compile it.

4. run ./assess n (n is file length in bits, divided by the number of bytestreams)

5. follow the instructions

6. result will be writed in experiments/AlgoritmTesting/finalAnalysisReport.txt
 
 
## Created using

BitcoinJ (https://github.com/bitcoinj/bitcoinj) Apache License Version 2.0

Gerbera (https://github.com/aafomin/gerbera) MIT License

Bouncy Castle Java (https://github.com/bcgit/bc-java)

Guava: Google Core Libraries for Java (https://github.com/google/guava) Apache License Version 2.0

GSON (https://github.com/google/gson) Apache License Version 2.0
