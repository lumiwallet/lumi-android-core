#include <jni.h>
#include <string>
#include "ed25519/ed25519.h"
#include "sha512_hmac.h"
#include "sign.h"

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_lumiwallet_android_core_cardano_crypto_Native_cardanoCryptoEd25519publickey(
        JNIEnv *env,
        jobject thiz,
        jbyteArray private_key
) {

    ed25519_secret_key sk;
    ed25519_public_key pk;

    jbyteArray jPublicKey = env->NewByteArray(32);
    env->GetByteArrayRegion(private_key, 0, 64,(jbyte*) sk);

    cardano_crypto_ed25519_publickey(sk, pk);

    env->SetByteArrayRegion(jPublicKey, 0, 32, (jbyte*) pk);
    return jPublicKey;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_lumiwallet_android_core_cardano_crypto_Native_cardanoCryptoEd25519sign(
        JNIEnv *env,
        jobject thiz,
        jbyteArray message,
        jint message_len,
        jbyteArray private_key
) {
    ed25519_secret_key sk;
    ed25519_public_key pk;
    ed25519_signature sig;
    unsigned char msg[message_len];

    jbyteArray jSig = env->NewByteArray(64);

    env->GetByteArrayRegion(private_key, 0, 64, (jbyte*) sk);
    env->GetByteArrayRegion(message, 0, message_len, (jbyte*) msg);

    cardano_crypto_ed25519_publickey(sk, pk);

    cardano_crypto_ed25519_sign(
        msg,
        message_len,
        NULL,
        0,
        sk,
        pk,
        sig
    );

    env->SetByteArrayRegion (jSig, 0, 64, (jbyte*) sig);

    return jSig;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_lumiwallet_android_core_cardano_crypto_Native_sha512HMAC(
        JNIEnv *env, jobject thiz,
        jbyteArray key,
        jint key_size,
        jbyteArray message,
        jint message_len
) {

    uint8_t c_message[message_len];
    int c_message_len = message_len;
    uint8_t c_key[key_size];
    int c_key_len = (int) key_size;
    uint8_t c_hash[SHA512_HASH_SIZE];
    int c_hash_len = SHA512_HASH_SIZE;

    env->GetByteArrayRegion(message, 0, message_len, (jbyte*) c_message);
    env->GetByteArrayRegion(key, 0, key_size, (jbyte*) c_key);


    hmac_sha512(c_key, c_key_len, c_message, c_message_len, c_hash, c_hash_len);

    jbyteArray hash = env->NewByteArray(SHA512_HASH_SIZE);
    env->SetByteArrayRegion (hash, 0, SHA512_HASH_SIZE, (jbyte*) c_hash);
    return hash;
}


extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_lumiwallet_android_core_cardano_crypto_Native_deriveCardanoKey(
        JNIEnv *env,
        jobject thiz,
        jbyteArray key,
        jbyteArray chainCode,
        jint sequence,
        jboolean hardened
) {
    uint32_t _sequence = (hardened ? (0x80000000 |  sequence) : sequence);
    uint8_t index[4];
    index[0] = _sequence;
    index[1] = _sequence >> 8;
    index[2] = _sequence >> 16;
    index[3] = _sequence >> 24;

    static const int privateKeyLength = 64;
    static const int ccLength = 32;

    unsigned char prvKey[privateKeyLength];
    unsigned char c_chainCode[ccLength];

    env->GetByteArrayRegion(key, 0, privateKeyLength, (jbyte *) prvKey);
    env->GetByteArrayRegion(chainCode, 0, ccLength, (jbyte *) c_chainCode);

    unsigned char derivedKey[privateKeyLength];
    memset(derivedKey, 0, privateKeyLength);

    uint8_t publicKey[64];

    cardano_crypto_ed25519_publickey(prvKey, publicKey);

    hmac_sha512_ctx ctx;

    hmac_sha512_init(&ctx, c_chainCode, 32);
    if (hardened) {
        hmac_sha512_update(&ctx, reinterpret_cast<const unsigned char *>(TAG_DERIVE_Z_HARDENED), 1);
        hmac_sha512_update(&ctx, prvKey, privateKeyLength);
    } else {
        hmac_sha512_update(&ctx, reinterpret_cast<const unsigned char *>(TAG_DERIVE_Z_NORMAL), 1);
        hmac_sha512_update(&ctx, publicKey, 32);
    }

    unsigned int sha512DigestLength = 64;

    unsigned char z[sha512DigestLength];
    memset(z, 0, sizeof(z));

    hmac_sha512_update(&ctx, index, 4);
    hmac_sha512_final(&ctx, z, sha512DigestLength);

    add_left(derivedKey, z, prvKey, derivation_scheme_mode::DERIVATION_V2);
    add_right(derivedKey, z, prvKey, derivation_scheme_mode::DERIVATION_V2);


    hmac_sha512_ctx ctx1;
    hmac_sha512_init(&ctx1, c_chainCode, 32);
    if (hardened) {
        hmac_sha512_update(&ctx1, reinterpret_cast<const unsigned char *>(TAG_DERIVE_CC_HARDENED), 1);
        hmac_sha512_update(&ctx1, prvKey, privateKeyLength);
    } else {
        hmac_sha512_update(&ctx1, reinterpret_cast<const unsigned char *>(TAG_DERIVE_CC_NORMAL), 1);
        hmac_sha512_update(&ctx1, publicKey, 32);
    }

    unsigned char output[sha512DigestLength];
    memset(output, 0, sizeof(output));

    hmac_sha512_update(&ctx1, index, 4);
    hmac_sha512_final(&ctx1, output, sha512DigestLength);

    jbyteArray jKeyWithCC = env->NewByteArray(96);
    env->SetByteArrayRegion (jKeyWithCC, 0, 64, (jbyte*) derivedKey);
    env->SetByteArrayRegion (jKeyWithCC, 64, 32, (jbyte*) output + 32);

    return jKeyWithCC;
}