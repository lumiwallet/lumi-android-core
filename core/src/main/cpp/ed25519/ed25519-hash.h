#include "../sha512.h"

typedef Sha512Context ed25519_hash_context;

static void
ed25519_hash_init(ed25519_hash_context *ctx) {
    Sha512Initialise(ctx);
}

static void
ed25519_hash_update(ed25519_hash_context *ctx, const uint8_t *in, size_t inlen) {
    Sha512Update(ctx, in, inlen);
}

static void
ed25519_hash_final(ed25519_hash_context *ctx, uint8_t *hash) {
    Sha512Finalise(ctx, hash);
}

static void
ed25519_hash(uint8_t *hash, const uint8_t *in, size_t inlen) {
    Sha512Calculate(in, inlen, hash);
}
