//
//  sign.h
//  LumiCore
//
//  Created by Any Key on 13.07.2021.
//  Copyright © 2021 Lumi Technologies. All rights reserved.
//

#ifndef sign_h
#define sign_h

#include <assert.h>
#include <stdint.h>
#include <string.h>

#include "ed25519/ed25519.h"

#define SYM_KEY_SIZE     32
#define SYM_NONCE_SIZE   8
#define SYM_BUF_SIZE     (SYM_KEY_SIZE+SYM_NONCE_SIZE)

#define SECRET_KEY_SEED_SIZE 32
#define ENCRYPTED_KEY_SIZE 64
#define PUBLIC_KEY_SIZE    32
#define CHAIN_CODE_SIZE    32

#define FULL_KEY_SIZE      (ENCRYPTED_KEY_SIZE + PUBLIC_KEY_SIZE + CHAIN_CODE_SIZE)

typedef struct {
    uint8_t ekey[ENCRYPTED_KEY_SIZE];
    uint8_t pkey[PUBLIC_KEY_SIZE];
    uint8_t cc[CHAIN_CODE_SIZE];
} encrypted_key;

typedef struct {
    uint8_t pkey[PUBLIC_KEY_SIZE];
    uint8_t cc[CHAIN_CODE_SIZE];
} public_key;

typedef enum {
    DERIVATION_V1 = 1,
    DERIVATION_V2 = 2,
} derivation_scheme_mode;

static void multiply8_v1(uint8_t *dst, uint8_t *src, int bytes)
{
    int i;
    uint8_t prev_acc = 0;
    for (i = 0; i < bytes; i++) {
        dst[i] = (src[i] << 3) + (prev_acc & 0x8);
        prev_acc = src[i] >> 5;
    }
}

static void multiply8_v2(uint8_t *dst, uint8_t *src, int bytes)
{
    int i;
    uint8_t prev_acc = 0;
    for (i = 0; i < bytes; i++) {
        dst[i] = (src[i] << 3) + (prev_acc & 0x7);
        prev_acc = src[i] >> 5;
    }
    dst[bytes] = src[bytes-1] >> 5;
}

static void add_256bits_v1(uint8_t *dst, uint8_t *src1, uint8_t *src2)
{
    int i;
    for (i = 0; i < 32; i++) {
        uint8_t a = src1[i];
        uint8_t b = src2[i];
        uint16_t r = a + b;
        dst[i] = r & 0xff;
    }
}

static void add_256bits_v2(uint8_t *dst, uint8_t *src1, uint8_t *src2)
{
    int i; uint8_t carry = 0;
    for (i = 0; i < 32; i++) {
        uint8_t a = src1[i];
        uint8_t b = src2[i];
        uint16_t r = (uint16_t) a + (uint16_t) b + (uint16_t) carry;
        dst[i] = r & 0xff;
        carry = (r >= 0x100) ? 1 : 0;
    }
}

#define TAG_DERIVE_Z_NORMAL    "\x2"
#define TAG_DERIVE_Z_HARDENED  "\x0"
#define TAG_DERIVE_CC_NORMAL   "\x3"
#define TAG_DERIVE_CC_HARDENED "\x1"

static int index_is_hardened(uint32_t index)
{
    return (index & (1 << 31));
}

/* sk1 is zl8 and contains only 29 bytes of active data,
 * so it's not going to overflow when adding to sk2 */
void scalar_add_no_overflow(const ed25519_secret_key sk1, const ed25519_secret_key sk2, ed25519_secret_key res)
{
    uint16_t r = 0; int i;
    for (i = 0; i < 32; i++) {
        r = (uint16_t) sk1[i] + (uint16_t) sk2[i] + r;
        res[i] = (uint8_t) r;
        r >>= 8;
    }
}

static void serialize_index32(uint8_t *out, uint32_t index, derivation_scheme_mode mode)
{
    switch (mode) {
    case DERIVATION_V1: /* BIG ENDIAN */
        out[0] = index >> 24;
        out[1] = index >> 16;
        out[2] = index >> 8;
        out[3] = index;
        break;
    case DERIVATION_V2: /* LITTLE ENDIAN */
        out[3] = index >> 24;
        out[2] = index >> 16;
        out[1] = index >> 8;
        out[0] = index;
        break;
    }
}

static void add_left(ed25519_secret_key res_key, uint8_t *z, ed25519_secret_key priv_key, derivation_scheme_mode mode)
{
    uint8_t zl8[32];

    memset(zl8, 0, 32);
    switch (mode) {
    case DERIVATION_V1:
        /* get 8 * Zl */
        multiply8_v1(zl8, z, 32);

        /* Kl = 8*Zl + parent(K)l */
        cardano_crypto_ed25519_scalar_add(zl8, priv_key, res_key);
        break;
    case DERIVATION_V2:
        /* get 8 * Zl */
        multiply8_v2(zl8, z, 28);
        /* Kl = 8*Zl + parent(K)l */
        scalar_add_no_overflow(zl8, priv_key, res_key);
        break;
    }
}

static void add_right(ed25519_secret_key res_key, uint8_t *z, ed25519_secret_key priv_key, derivation_scheme_mode mode)
{
    switch (mode) {
    case DERIVATION_V1:
        add_256bits_v1(res_key + 32, z+32, priv_key+32);
        break;
    case DERIVATION_V2:
        add_256bits_v2(res_key + 32, z+32, priv_key+32);
        break;
    }
}

#endif /* sign_h */
