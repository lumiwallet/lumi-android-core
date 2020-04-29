package com.lumiwallet.android.core.eos.utils;

public class HexUtils {

    public static byte[] toBytes(String paramString) {
        if ((paramString != null) && (paramString.length() % 2 == 0)) {
            char[] charArray = paramString.toCharArray();
            int m = charArray.length / 2;
            byte[] localObject = new byte[m];
            int i = 0;
            while (i < m) {
                int j = i * 2;
                int k = Character.digit(charArray[j], 16);
                int n = j + 1;
                int i1 = Character.digit(charArray[n], 16);
                if ((k >= 0) && (i1 >= 0)) {
                    k = k << 4 | i1;
                    j = k;
                    if (k > 127) {
                        j = k - 256;
                    }
                    localObject[i] = ((byte) j);
                    i += 1;
                } else {
                    String message = new StringBuilder()
                        .append("Invalid hex digit ")
                        .append(charArray[j])
                        .append(charArray[n])
                        .toString();
                    throw new RuntimeException(message);
                }
            }
            return localObject;
        }
        throw new RuntimeException("Input string must contain an even number of characters");
    }


    public static String toHex(byte[] paramArrayOfByte) {
        return toHex(paramArrayOfByte, null);
    }

    public static String toHex(byte[] paramArrayOfByte, int paramInt1, int paramInt2, String paramString) {
        StringBuilder localStringBuilder = new StringBuilder();
        int i = 0;
        while (i < paramInt2) {
            int j = paramArrayOfByte[(i + paramInt1)] & 0xFF;
            if (j < 16) {
                localStringBuilder.append("0");
            }
            localStringBuilder.append(Integer.toHexString(j));
            if ((paramString != null) && (i + 1 < paramInt2)) {
                localStringBuilder.append(paramString);
            }
            i += 1;
        }
        return localStringBuilder.toString();
    }

    public static String toHex(byte[] paramArrayOfByte, String paramString) {
        return toHex(paramArrayOfByte, 0, paramArrayOfByte.length, paramString);
    }
}