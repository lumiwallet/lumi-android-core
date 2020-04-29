package com.lumiwallet.android.core.eos.models.types;


import com.lumiwallet.android.core.utils.Sha256Hash;

public class TypeChainId {
    private final Sha256Hash mId;

    public TypeChainId() {
        this.mId = Sha256Hash.ZERO_HASH;
    }

    public TypeChainId(String paramString) {
        this.mId = Sha256Hash.wrap(getSha256FromHexStr(paramString));
    }

    public byte[] getBytes() {
        return this.mId.getBytes();
    }

    byte[] getSha256FromHexStr(String paramString) {
        int k = paramString.length();
        byte[] arrayOfByte = new byte[32];
        int j;
        for (int i = 0; i < k; i = j) {
            j = i + 2;
            int m = Integer.parseInt(paramString.substring(i, j), 16);
            arrayOfByte[(i / 2)] = Integer.valueOf(m & 0xFF).byteValue();
        }
        return arrayOfByte;
    }
}