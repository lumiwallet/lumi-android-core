/*
 * Copyright (c) 2017-2018 PLACTAL.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.lumiwallet.android.core.eos.models.types;

import java.util.Collection;

public class EosByteWriter implements EosType.Writer {
    private byte[] buffer;
    private int index;

    public EosByteWriter(int capacity) {
        buffer = new byte[capacity];
        index = 0;
    }

    public EosByteWriter(byte[] buf) {
        buffer = buf;
        index = buf.length;
    }

    private void ensureCapacity(int capacity) {
        if (buffer.length - index < capacity) {
            byte[] temp = new byte[buffer.length * 2 + capacity];
            System.arraycopy(buffer, 0, temp, 0, index);
            buffer = temp;
        }
    }

    @Override
    public void put(byte b) {
        ensureCapacity(1);
        buffer[index++] = b;
    }

    @Override
    public void putShortLE(short value) {
        ensureCapacity(2);
        buffer[index++] = (byte) (0xFF & (value));
        buffer[index++] = (byte) (0xFF & (value >> 8));
    }

    @Override
    public void putIntLE(int value) {
        ensureCapacity(4);
        buffer[index++] = (byte) (0xFF & (value));
        buffer[index++] = (byte) (0xFF & (value >> 8));
        buffer[index++] = (byte) (0xFF & (value >> 16));
        buffer[index++] = (byte) (0xFF & (value >> 24));
    }


    @Override
    public void putLongLE(long value) {
        ensureCapacity(8);
        buffer[index++] = (byte) (0xFFL & (value));
        buffer[index++] = (byte) (0xFFL & (value >> 8));
        buffer[index++] = (byte) (0xFFL & (value >> 16));
        buffer[index++] = (byte) (0xFFL & (value >> 24));
        buffer[index++] = (byte) (0xFFL & (value >> 32));
        buffer[index++] = (byte) (0xFFL & (value >> 40));
        buffer[index++] = (byte) (0xFFL & (value >> 48));
        buffer[index++] = (byte) (0xFFL & (value >> 56));
    }


    @Override
    public void putBytes(byte[] value) {
        ensureCapacity(value.length);
        System.arraycopy(value, 0, buffer, index, value.length);
        index += value.length;
    }

    public void putBytes(byte[] value, int offset, int length) {
        ensureCapacity(length);
        System.arraycopy(value, offset, buffer, index, length);
        index += length;
    }


    @Override
    public byte[] toBytes() {
        byte[] bytes = new byte[index];
        System.arraycopy(buffer, 0, bytes, 0, index);
        return bytes;
    }

    @Override
    public int length() {
        return index;
    }


    @Override
    public void putString(String value){
        if ( null == value ){
            putVariableUInt( 0 );
            return;
        }

        putVariableUInt( value.length() );
        putBytes( value.getBytes() );
    }

    @Override
    public void putCollection(Collection<? extends EosType.Packer> collection){
        if ( null == collection){
            putVariableUInt( 0 );
            return;
        }

        putVariableUInt( collection.size() );

        for ( EosType.Packer type : collection) {
            type.pack( this );
        }
    }

    @Override
    public void putVariableUInt(long val ) {

        do {
            byte b = (byte)((val) & 0x7f);
            val >>= 7;
            b |= ( ((val > 0) ? 1 : 0 ) << 7 );
            put(b);
        } while( val != 0 );
    }

}
