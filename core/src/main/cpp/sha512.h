////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WjCryptLib_Sha512
//
//  Implementation of SHA512 hash function.
//  Original author: Tom St Denis, tomstdenis@gmail.com, http://libtom.org
//  Modified by WaterJuice retaining Public Domain license.
//
//  This is free and unencumbered software released into the public domain - June 2013 waterjuice.org
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

#pragma once

#include <stdint.h>
#include <stdio.h>

#define SHA512_BLOCK_SIZE  (1024 / 8)

typedef struct
{
    uint64_t    length;
    uint64_t    state[8];
    uint32_t    curlen;
    uint8_t     buf[SHA512_BLOCK_SIZE];
} Sha512Context;

#define SHA512_HASH_SIZE           ( 512 / 8 )

typedef struct
{
    uint8_t      bytes [SHA512_HASH_SIZE];
} SHA512_HASH;

void Sha512Initialise(Sha512Context* Context );

void Sha512Update(
    Sha512Context*      Context,
    void const*         Buffer,
    uint32_t            BufferSize
);

void Sha512Finalise(
    Sha512Context*      Context,
    SHA512_HASH*        Digest
);

void Sha512Calculate (
    void  const*        Buffer,
    uint32_t            BufferSize,
    SHA512_HASH*        Digest
);