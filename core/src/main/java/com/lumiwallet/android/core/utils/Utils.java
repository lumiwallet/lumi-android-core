package com.lumiwallet.android.core.utils;

import com.google.common.base.Joiner;
import com.google.common.io.BaseEncoding;

import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.jcajce.provider.digest.Keccak;

import java.math.BigInteger;

import static com.google.common.base.Preconditions.checkArgument;

public class Utils {

    /**
     * Joiner for concatenating words with a space inbetween.
     */
    public static final Joiner SPACE_JOINER = Joiner.on(" ");
    /**
     * Hex encoding used throughout the framework. Use with HEX.encode(byte[]) or HEX.decode(CharSequence).
     */
    public static final BaseEncoding HEX = BaseEncoding.base16().lowerCase();


    /**
     * The regular {@link BigInteger#toByteArray()} includes the sign bit of the number and
     * might result in an extra byte addition. This method removes this extra byte.
     *
     * @param b        the integer to format into a byte array
     * @param numBytes the desired size of the resulting byte array
     * @return numBytes byte long array.
     */
    public static byte[] bigIntegerToBytes(BigInteger b, int numBytes) {
        checkArgument(b.signum() >= 0, "b must be positive or zero");
        checkArgument(numBytes > 0, "numBytes must be positive");
        byte[] src = b.toByteArray();
        byte[] dest = new byte[numBytes];
        boolean isFirstByteOnlyForSign = src[0] == 0;
        int length = isFirstByteOnlyForSign ? src.length - 1 : src.length;
        checkArgument(length <= numBytes, "The given number does not fit in " + numBytes);
        int srcPos = isFirstByteOnlyForSign ? 1 : 0;
        int destPos = numBytes - length;
        System.arraycopy(src, srcPos, dest, destPos, length);
        return dest;
    }

    /**
     * Returns a copy of the given byte array in reverse order.
     */
    public static byte[] reverseBytes(byte[] bytes) {
        // We could use the XOR trick here but it's easier to understand if we don't. If we find this is really a
        // performance issue the matter can be revisited.
        byte[] buf = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++)
            buf[i] = bytes[bytes.length - 1 - i];
        return buf;
    }

    /**
     * Calculates RIPEMD160(SHA256(input)).
     */
    public static byte[] sha256hash160(byte[] input) {
        byte[] sha256 = Sha256Hash.hash(input);
        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(sha256, 0, sha256.length);
        byte[] out = new byte[20];
        digest.doFinal(out, 0);
        return out;
    }


    public static long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }


    /**
     * Encode string to hex Keccak 256.
     */

    public static String hexKeccak256(String data) {
        byte[] dataBytes = data.getBytes();
        Keccak.Digest256 keccak256 = new Keccak.Digest256();
        keccak256.reset();
        keccak256.update(dataBytes, 0, dataBytes.length);
        byte[] hashedBytes = keccak256.digest();
        return HEX.encode(hashedBytes);
    }

}
