package me.spencernold.jrs;

import java.util.Arrays;

public class NetworkByteBuf {

    private final byte[] internal;
    private int index;
    private boolean state; // true == reading, false == writing

    NetworkByteBuf(int size, boolean reading) {
        this(new byte[size], reading);
    }

    NetworkByteBuf(byte[] bytes, boolean reading) {
        this.internal = bytes;
        this.index = 0;
        this.state = reading;
    }

    byte[] internal() {
        return Arrays.copyOfRange(internal, 0, index);
    }

    public void write(byte[] bytes) {
        if ((index + bytes.length) > internal.length)
            throw new IllegalStateException("internal buffer overflow; size of the buffer is not big enough");
        System.arraycopy(bytes, 0, internal, index, bytes.length);
        index += bytes.length;
    }

    public byte[] read(int n) {
        if (n > (internal.length - index))
            throw new IllegalStateException("internal buffer underflow; not enough data left in the buffer");
        return Arrays.copyOfRange(internal, index, index += n);
    }

    public void seek(int index) {
        this.index = clamp(index, 0, internal.length);
    }

    public int getWriterPosition() {
        return index;
    }

    public void flip() {
        state = !state;
        index = 0;
    }

    public int remaining() {
        return internal.length - index;
    }

    public void writeByte(byte b) {
        write(new byte[]{b});
    }

    public byte readByte() {
        return read(1)[0];
    }

    public void writeShort(short s) {
        if (remaining() < 2)
            throw new IllegalStateException("internal buffer overflow; size of the buffer is not big enough");
        internal[index++] = (byte) ((s >>> 8) & 0xFF);
        internal[index++] = (byte) (s & 0xFF);
    }

    public short readShort() {
        if (remaining() < 2)
            throw new IllegalStateException("internal buffer underflow; not enough data left in the buffer");
        int b1 = internal[index++] & 0xFF;
        int b2 = internal[index++] & 0xFF;
        return (short) ((b1 << 8) | b2);
    }

    public void writeInt(int i) {
        if (remaining() < 4)
            throw new IllegalStateException("internal buffer overflow; size of the buffer is not big enough");
        internal[index++] = (byte) ((i >>> 24) & 0xFF);
        internal[index++] = (byte) ((i >>> 16) & 0xFF);
        internal[index++] = (byte) ((i >>> 8) & 0xFF);
        internal[index++] = (byte) (i & 0xFF);
    }

    public int readInt() {
        if (remaining() < 4)
            throw new IllegalStateException("internal buffer underflow; not enough data left in the buffer");
        int b1 = internal[index++] & 0xFF;
        int b2 = internal[index++] & 0xFF;
        int b3 = internal[index++] & 0xFF;
        int b4 = internal[index++] & 0xFF;
        return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
    }

    private int clamp(int i, int min, int max) {
        if (i > max)
            return max;
        if (i < min)
            return min;
        return i;
    }

    public static boolean compare(byte[] src, int srcI, byte[] dst, int dstI, int length) {
        for (int i = 0; i < length; i++) {
            byte s = src[srcI + i];
            byte d = dst[dstI + i];
            if (s != d)
                return false;
        }
        return true;
    }
}
