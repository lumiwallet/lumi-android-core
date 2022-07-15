
#ifndef HMAC_SHA2_H
#define HMAC_SHA2_H

#include "sha512.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    Sha512Context ctx_inside;
    Sha512Context ctx_outside;

    /* for hmac_reinit */
    Sha512Context ctx_inside_reinit;
    Sha512Context ctx_outside_reinit;

    unsigned char block_ipad[SHA512_BLOCK_SIZE];
    unsigned char block_opad[SHA512_BLOCK_SIZE];
} hmac_sha512_ctx;

void hmac_sha512_init(hmac_sha512_ctx *ctx, const unsigned char *key,
                      unsigned int key_size);
void hmac_sha512_update(hmac_sha512_ctx *ctx, const unsigned char *message,
                        unsigned int message_len);
void hmac_sha512_final(hmac_sha512_ctx *ctx, unsigned char *mac,
                       unsigned int mac_size);
void hmac_sha512(const unsigned char *key, unsigned int key_size,
                 const unsigned char *message, unsigned int message_len,
                 unsigned char *mac, unsigned mac_size);

#ifdef __cplusplus
}
#endif

#endif /* !HMAC_SHA2_H */

