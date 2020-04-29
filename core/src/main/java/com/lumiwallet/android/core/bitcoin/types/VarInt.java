package com.lumiwallet.android.core.bitcoin.types;

import org.bouncycastle.util.encoders.Hex;

public class VarInt {

    private byte[] litEndBytes;

    private VarInt(long value) {
        this.litEndBytes = toBytes(value);
    }

    private byte[] toBytes(long value) {
        if (value < 0xFD && value >= 0) {
            return new byte[] { (byte) value };
        }

        if (value <= 0xFFFF && value > 0) {
            return new byte[] {
                    (byte) 0xFD,
                    (byte) value,
                    (byte) (value >> 8)
            };
        }

        if (value <= 0xFFFFFFFFL && value > 0) {
            return new byte[] {
                    (byte) 0xFE,
                    (byte) value,
                    (byte) (value >> 8),
                    (byte) (value >> 16),
                    (byte) (value >> 24),
            };
        }

        return new byte[] {
                (byte) 0xFF,
                (byte) value,
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24),
                (byte) (value >> 32),
                (byte) (value >> 40),
                (byte) (value >> 48),
                (byte) (value >> 56)
        };
    }

    public static VarInt of(long value) {
        return new VarInt(value);
    }

    public byte[] asLitEndBytes() {
        return litEndBytes;
    }

    @Override
    public String toString() {
        return Hex.toHexString(litEndBytes);
    }
}
